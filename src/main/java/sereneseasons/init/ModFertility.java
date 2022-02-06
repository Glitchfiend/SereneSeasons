package sereneseasons.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.FertilityConfig;
import sereneseasons.config.SeasonsConfig;

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
        springPlants.clear();
        summerPlants.clear();
        autumnPlants.clear();
        winterPlants.clear();
        allListedPlants.clear();
        seedSeasons.clear();
        
        initSeasonCrops(ModTags.Blocks.spring_crops.getValues(), springPlants, 1);
        initSeasonCrops(ModTags.Blocks.summer_crops.getValues(), summerPlants, 2);
        initSeasonCrops(ModTags.Blocks.autumn_crops.getValues(), autumnPlants, 4);
        initSeasonCrops(ModTags.Blocks.winter_crops.getValues(), winterPlants, 8);

        initSeasonSeeds(ModTags.Items.spring_crops.getValues(), springPlants, 1);
        initSeasonSeeds(ModTags.Items.summer_crops.getValues(), summerPlants, 2);
        initSeasonSeeds(ModTags.Items.autumn_crops.getValues(), autumnPlants, 4);
        initSeasonSeeds(ModTags.Items.winter_crops.getValues(), winterPlants, 8);
    }

    public static boolean isCropFertile(String cropName, World world, BlockPos pos)
    {
        //Get season
        Season season = SeasonHelper.getSeasonState(world).getSeason();
        Biome biome = world.getBiome(pos);
        RegistryKey<Biome> biomeKey = world.getBiomeName(pos).orElse(null);

        if (FertilityConfig.undergroundFertilityLevel.get() > -1 && pos.getY() < FertilityConfig.undergroundFertilityLevel.get() && !world.canSeeSky(pos))
        {
            return true;
        }

        if (BiomeConfig.infertileBiome(biomeKey))
        {
            return false;
        }
        else if (!FertilityConfig.seasonalCrops.get() || !BiomeConfig.enablesSeasonalEffects(biomeKey) || !SeasonsConfig.isDimensionWhitelisted(world.dimension()))
        {
            return true;
        }

        if (BiomeConfig.usesTropicalSeasons(biomeKey))
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
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Initializes the crops for a particular season. User's responsibility to match seeds and cropSet to be of the
     * same season (eg. String [] spring_seeds, HashSet springPlants)
     *
     * @param crops   String array of seeds that are fertile during the chosen season
     * @param cropSet HashSet that will store the list of crops fertile during the chosen season
     */
    private static void initSeasonCrops(List<Block> crops, HashSet<String> cropSet, int bitmask)
    {
        for (Block crop : crops)
        {
            if (crop != null && crop != Blocks.AIR)
            {
                String plantName = crop.getRegistryName().toString();
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
                if (seedSeasons.containsKey(plantName))
                {
                    int seasons = seedSeasons.get(plantName);
                    seedSeasons.put(plantName, seasons | bitmask);
                }
                else
                {
                    seedSeasons.put(plantName, bitmask);
                }
            }
        }
    }

    private static void initSeasonSeeds(List<Item> seeds, HashSet<String> cropSet, int bitmask)
    {
        for (Item seed : seeds)
        {
            if (seed != null)
            {
                String plantName = seed.getRegistryName().toString();
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
                if (seedSeasons.containsKey(plantName))
                {
                    int seasons = seedSeasons.get(plantName);
                    seedSeasons.put(plantName, seasons | bitmask);
                }
                else
                {
                    seedSeasons.put(plantName, bitmask);
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
                    event.getToolTip().add(new StringTextComponent(" ").append((new TranslationTextComponent("desc.sereneseasons.year_round")).withStyle(TextFormatting.LIGHT_PURPLE)));
                }
                else
                {
                    if ((mask & 1) != 0)
                    {
                        event.getToolTip().add(new StringTextComponent(" ").append((new TranslationTextComponent("desc.sereneseasons.spring")).withStyle(TextFormatting.GREEN)));
                    }
                    if ((mask & 2) != 0)
                    {
                        event.getToolTip().add(new StringTextComponent(" ").append((new TranslationTextComponent("desc.sereneseasons.summer")).withStyle(TextFormatting.YELLOW)));
                    }
                    if ((mask & 4) != 0)
                    {
                        event.getToolTip().add(new StringTextComponent(" ").append((new TranslationTextComponent("desc.sereneseasons.autumn")).withStyle(TextFormatting.GOLD)));
                    }
                    if ((mask & 8) != 0)
                    {
                        event.getToolTip().add(new StringTextComponent(" ").append((new TranslationTextComponent("desc.sereneseasons.winter")).withStyle(TextFormatting.AQUA)));
                    }
                }
            }
        }
    }
}
