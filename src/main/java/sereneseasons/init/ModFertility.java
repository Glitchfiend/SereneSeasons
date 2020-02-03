package sereneseasons.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.FertilityConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.core.SereneSeasons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Constructs efficient data structures to process, store, and give access to data from the FertilityConfig file
 */
public class ModFertility
{

    private static HashSet<String> springPlants = new HashSet<String>();
    private static HashSet<String> summerPlants = new HashSet<String>();
    private static HashSet<String> autumnPlants = new HashSet<String>();
    private static HashSet<String> winterPlants = new HashSet<String>();
    private static HashSet<String> allListedPlants = new HashSet<String>();

    //Maps seed name to all fertile seasons via byte
    private static HashMap<String, Integer> seedSeasons = new HashMap<String, Integer>();

    public static void init()
    {
        //Store crops in hash sets for quick and easy retrieval
        initSeasonCrops((List<String>)FertilityConfig.springCrops.get(), springPlants, 1);
        initSeasonCrops((List<String>)FertilityConfig.summerCrops.get(), summerPlants, 2);
        initSeasonCrops((List<String>)FertilityConfig.autumnCrops.get(), autumnPlants, 4);
        initSeasonCrops((List<String>)FertilityConfig.winterCrops.get(), winterPlants, 8);
    }

    public static boolean isCropFertile(String cropName, World world, BlockPos pos)
    {
        //Get season
        Season season = SeasonHelper.getSeasonState(world).getSeason();
        Biome biome = world.getBiome(pos);

        if (FertilityConfig.undergroundFertilityLevel.get() > -1 && pos.getY() < FertilityConfig.undergroundFertilityLevel.get())
        {
            return true;
        }

        if (BiomeConfig.disablesCrops(biome))
        {
            return false;
        }
        else if (!FertilityConfig.seasonalCrops.get() || !BiomeConfig.enablesSeasonalEffects(biome) || !SeasonsConfig.isDimensionWhitelisted(world.getDimension().getType().getId()))
        {
            return true;
        }

        if (BiomeConfig.usesTropicalSeasons(biome))
        {
            if (summerPlants.contains(cropName) || !(allListedPlants.contains(cropName)))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            if (biome.getTemperature(pos) < 0.15F)
            {
                if (winterPlants.contains(cropName))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                if (season == Season.SPRING && springPlants.contains(cropName))
                {
                    return true;
                }
                else if (season == Season.SUMMER && summerPlants.contains(cropName))
                {
                    return true;
                }
                else if (season == Season.AUTUMN && autumnPlants.contains(cropName))
                {
                    return true;
                }
                else if (season == Season.WINTER && winterPlants.contains(cropName))
                {
                    return true;
                }

                //Check if unspecified crops are by default fertile in non-winter, and that it's not winter
                if (!allListedPlants.contains(cropName))
                {
                    if (season == Season.WINTER)
                    {
                        return (FertilityConfig.ignoreUnlistedCrops.get());
                    }
                    else
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Initializes the crops for a particular season. User's responsibility to match seeds and cropSet to be of the
     * same season (eg. String [] spring_seeds, HashSet springPlants)
     *
     * @param seeds   String array of seeds that are fertile during the chosen season
     * @param cropSet HashSet that will store the list of crops fertile during the chosen season
     */
    private static void initSeasonCrops(List<String> seeds, HashSet<String> cropSet, int bitmask)
    {
        for (String seed : seeds)
        {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(seed));
            BlockItem blockItem = (item instanceof BlockItem) ? (BlockItem)item : null;

            if (blockItem != null && blockItem.getBlock() instanceof IPlantable)
            {
                String plantName = blockItem.getBlock().getRegistryName().toString();
                cropSet.add(plantName);

                if (bitmask != 0)
                {
                    allListedPlants.add(plantName);
                }
                else
                {
                    continue;
                }

                //Add to seedSeasons
                if (seedSeasons.containsKey(seed))
                {
                    int seasons = seedSeasons.get(seed);
                    seedSeasons.put(seed, seasons | bitmask);
                }
                else
                {
                    seedSeasons.put(seed, bitmask);
                }
            }
            else // Not a BlockItem with an IPlantable block, but uses same registry key as seeds
            {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(seed));

                if (block != null && block != Blocks.AIR)
                {
                    String plantName = block.getRegistryName().toString();
                    cropSet.add(plantName);

                    if (bitmask != 0)
                    {
                        allListedPlants.add(plantName);
                    }
                    else
                    {
                        continue;
                    }

                    //Add to seedSeasons
                    if (seedSeasons.containsKey(seed))
                    {
                        int seasons = seedSeasons.get(seed);
                        seedSeasons.put(seed, seasons | bitmask);
                    }
                    else
                    {
                        seedSeasons.put(seed, bitmask);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void setupTooltips(ItemTooltipEvent event)
    {
        //Set up tooltips if enabled and on client side
        if (FertilityConfig.cropTooltips.get() && FertilityConfig.seasonalCrops.get())
        {
            String name = event.getItemStack().getItem().getRegistryName().toString();
            if (seedSeasons.containsKey(name))
            {
                int mask = seedSeasons.get(name);

                event.getToolTip().add(new TranslationTextComponent("desc.sereneseasons.fertile_seasons").append(":"));

                if ((mask & 1) != 0 && (mask & 2) != 0 && (mask & 4) != 0 && (mask & 8) != 0)
                {
                    event.getToolTip().add(new StringTextComponent(TextFormatting.LIGHT_PURPLE + " ").append(new TranslationTextComponent("desc.sereneseasons.year_round")));
                }
                else
                {
                    if ((mask & 1) != 0)
                    {
                        event.getToolTip().add(new StringTextComponent(TextFormatting.GREEN + " ").append(new TranslationTextComponent("desc.sereneseasons.spring")));
                    }
                    if ((mask & 2) != 0)
                    {
                        event.getToolTip().add(new StringTextComponent(TextFormatting.YELLOW + " ").append(new TranslationTextComponent("desc.sereneseasons.summer")));
                    }
                    if ((mask & 4) != 0)
                    {
                        event.getToolTip().add(new StringTextComponent(TextFormatting.GOLD + " ").append(new TranslationTextComponent("desc.sereneseasons.autumn")));
                    }
                    if ((mask & 8) != 0)
                    {
                        event.getToolTip().add(new StringTextComponent(TextFormatting.AQUA + " ").append(new TranslationTextComponent("desc.sereneseasons.winter")));
                    }
                }
            }
        }
    }
}
