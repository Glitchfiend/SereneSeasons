package sereneseasons.init;

import java.util.HashMap;
import java.util.HashSet;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.asm.IBiomeMixin;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.FertilityConfig;
import sereneseasons.config.SeasonsConfig;

/**
 * Constructs efficient data structures to process, store, and give access to
 * data from the FertilityConfig file
 */
public class ModFertility
{

    private static HashSet<String> springPlants = new HashSet<String>();
    private static HashSet<String> summerPlants = new HashSet<String>();
    private static HashSet<String> autumnPlants = new HashSet<String>();
    private static HashSet<String> winterPlants = new HashSet<String>();
    private static HashSet<String> allListedPlants = new HashSet<String>();

    // Maps seed name to all fertile seasons via byte
    private static HashMap<String, Integer> seedSeasons = new HashMap<String, Integer>();

    public static void init()
    {
        // Store crops in hash sets for quick and easy retrieval
        initSeasonCrops(FertilityConfig.seasonal_fertility.spring_crops, springPlants, 1);
        initSeasonCrops(FertilityConfig.seasonal_fertility.summer_crops, summerPlants, 2);
        initSeasonCrops(FertilityConfig.seasonal_fertility.autumn_crops, autumnPlants, 4);
        initSeasonCrops(FertilityConfig.seasonal_fertility.winter_crops, winterPlants, 8);
    }

    public static boolean isCropFertile(String cropName, World world, int x, int y, int z)
    {
        // Get season
        Season season = SeasonHelper.getSeasonState(world).getSeason();
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        IBiomeMixin biomeMixin = (IBiomeMixin) biome;

        if (BiomeConfig.disablesCrops(biome))
        {
            return false;
        }
        else if (!FertilityConfig.general_category.seasonal_crops || !BiomeConfig.enablesSeasonalEffects(biome) || !SeasonsConfig.isDimensionWhitelisted(world.provider.dimensionId))
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
            if (biomeMixin.getFloatTemperatureOld(x, y, z) < 0.15F)
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

                // Check if unspecified crops are by default fertile in non-winter, and that
                // it's not winter
                if (!allListedPlants.contains(cropName))
                {
                    if (season == Season.WINTER)
                    {
                        return (FertilityConfig.general_category.ignore_unlisted_crops);
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
     * Initializes the crops for a particular season. User's responsibility to match
     * seeds and cropSet to be of the same season (eg. String [] spring_seeds,
     * HashSet springPlants)
     *
     * @param seeds   String array of seeds that are fertile during the chosen
     *                season
     * @param cropSet HashSet that will store the list of crops fertile during the
     *                chosen season
     */
    private static void initSeasonCrops(String[] seeds, HashSet<String> cropSet, int bitmask)
    {
        for (String seed : seeds)
        {
            ResourceLocation loc = new ResourceLocation(seed);
            Item item = GameRegistry.findItem(loc.getResourceDomain(), loc.getResourcePath());

            if (item instanceof IPlantable)
            {
                Block plantBlock = ((IPlantable) item).getPlant(null, 0, 0, 0);
                String plantName = GameRegistry.findUniqueIdentifierFor(plantBlock).toString();
                cropSet.add(plantName);

                if (bitmask != 0)
                {
                    allListedPlants.add(plantName);
                }
                else
                {
                    continue;
                }

                // Add to seedSeasons
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
            else
            {
                Block block = GameRegistry.findBlock(loc.getResourceDomain(), loc.getResourcePath());

                if (block != null && block != Blocks.air)
                {
                    String plantName = GameRegistry.findUniqueIdentifierFor(block).toString();
                    cropSet.add(plantName);

                    if (bitmask != 0)
                    {
                        allListedPlants.add(plantName);
                    }
                    else
                    {
                        continue;
                    }

                    // Add to seedSeasons
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

    @SideOnly(Side.CLIENT)
    public static void setupTooltips(ItemTooltipEvent event)
    {
        // Set up tooltips if enabled and on client side
        if (FertilityConfig.general_category.crop_tooltips && FertilityConfig.general_category.seasonal_crops)
        {
            String name;
            try
            {
                // Some mods register items with null as name. This crashes inside findUniqueIdentifierFor, so we need to catch that...
                name = GameRegistry.findUniqueIdentifierFor(event.itemStack.getItem()).toString();
            }
            catch (Exception ex)
            {
                return;
            }
            if (seedSeasons.containsKey(name))
            {
                int mask = seedSeasons.get(name);

                event.toolTip.add("Fertile Seasons:");

                if ((mask & 1) != 0 && (mask & 2) != 0 && (mask & 4) != 0 && (mask & 8) != 0)
                {
                    event.toolTip.add(EnumChatFormatting.LIGHT_PURPLE + " Year-Round");
                }
                else
                {
                    if ((mask & 1) != 0)
                    {
                        event.toolTip.add(EnumChatFormatting.GREEN + " Spring");
                    }
                    if ((mask & 2) != 0)
                    {
                        event.toolTip.add(EnumChatFormatting.YELLOW + " Summer");
                    }
                    if ((mask & 4) != 0)
                    {
                        event.toolTip.add(EnumChatFormatting.GOLD + " Autumn");
                    }
                    if ((mask & 8) != 0)
                    {
                        event.toolTip.add(EnumChatFormatting.AQUA + " Winter");
                    }
                }
            }
        }
    }
}
