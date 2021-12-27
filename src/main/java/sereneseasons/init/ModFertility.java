package sereneseasons.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.FertilityConfig;
import sereneseasons.config.ServerConfig;

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

    public static void populate()
    {
        // Clear sets
        springPlants.clear();
        summerPlants.clear();
        autumnPlants.clear();
        winterPlants.clear();
        allListedPlants.clear();
        seedSeasons.clear();

        //Store crops in hash sets for quick and easy retrieval
        populateSeasonCrops(ModTags.Blocks.spring_crops.getValues(), springPlants, 1);
        populateSeasonCrops(ModTags.Blocks.summer_crops.getValues(), summerPlants, 2);
        populateSeasonCrops(ModTags.Blocks.autumn_crops.getValues(), autumnPlants, 4);
        populateSeasonCrops(ModTags.Blocks.winter_crops.getValues(), winterPlants, 8);

        populateSeasonSeeds(ModTags.Items.spring_crops.getValues(), springPlants, 1);
        populateSeasonSeeds(ModTags.Items.summer_crops.getValues(), summerPlants, 2);
        populateSeasonSeeds(ModTags.Items.autumn_crops.getValues(), autumnPlants, 4);
        populateSeasonSeeds(ModTags.Items.winter_crops.getValues(), winterPlants, 8);
    }

    public static boolean isCropFertile(String cropName, Level world, BlockPos pos)
    {
        //Get season
        Season season = SeasonHelper.getSeasonState(world).getSeason();
        Biome biome = world.getBiome(pos);
        ResourceKey<Biome> biomeKey = world.getBiomeName(pos).orElse(null);

        if (FertilityConfig.undergroundFertilityLevel.get() > -1 && pos.getY() < FertilityConfig.undergroundFertilityLevel.get() && !world.canSeeSky(pos))
        {
            return true;
        }

        if (BiomeConfig.infertileBiome(biomeKey))
        {
            return false;
        }
        else if (!FertilityConfig.seasonalCrops.get() || !BiomeConfig.enablesSeasonalEffects(biomeKey) || !ServerConfig.isDimensionWhitelisted(world.dimension()))
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
            if (!biome.warmEnoughToRain(pos))
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
    private static void populateSeasonCrops(List<Block> crops, HashSet<String> cropSet, int bitmask)
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

    private static void populateSeasonSeeds(List<Item> seeds, HashSet<String> cropSet, int bitmask)
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

    public static void setupTooltips(ItemTooltipEvent event)
    {
        //Set up tooltips if enabled and on client side
        if (FertilityConfig.cropTooltips.get() && FertilityConfig.seasonalCrops.get())
        {
            String name = event.getItemStack().getItem().getRegistryName().toString();
            if (seedSeasons.containsKey(name))
            {
                int mask = seedSeasons.get(name);

                event.getToolTip().add(new TranslatableComponent("desc.sereneseasons.fertile_seasons").append(":"));

                if ((mask & 1) != 0 && (mask & 2) != 0 && (mask & 4) != 0 && (mask & 8) != 0)
                {
                    event.getToolTip().add(new TextComponent(" ").append((new TranslatableComponent("desc.sereneseasons.year_round")).withStyle(ChatFormatting.LIGHT_PURPLE)));
                }
                else
                {
                    if ((mask & 1) != 0)
                    {
                        event.getToolTip().add(new TextComponent(" ").append((new TranslatableComponent("desc.sereneseasons.spring")).withStyle(ChatFormatting.GREEN)));
                    }
                    if ((mask & 2) != 0)
                    {
                        event.getToolTip().add(new TextComponent(" ").append((new TranslatableComponent("desc.sereneseasons.summer")).withStyle(ChatFormatting.YELLOW)));
                    }
                    if ((mask & 4) != 0)
                    {
                        event.getToolTip().add(new TextComponent(" ").append((new TranslatableComponent("desc.sereneseasons.autumn")).withStyle(ChatFormatting.GOLD)));
                    }
                    if ((mask & 8) != 0)
                    {
                        event.getToolTip().add(new TextComponent(" ").append((new TranslatableComponent("desc.sereneseasons.winter")).withStyle(ChatFormatting.AQUA)));
                    }
                }
            }
        }
    }
}
