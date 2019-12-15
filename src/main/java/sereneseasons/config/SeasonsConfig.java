/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.config;

import java.io.File;

import sereneseasons.api.config.SeasonsOption;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModConfig;

public class SeasonsConfig extends ConfigHandler
{
    public static final String TIME_SETTINGS = "Time Settings";
    public static final String WEATHER_SETTINGS = "Weather Settings";
    public static final String AESTHETIC_SETTINGS = "Aesthetic Settings";
    public static final String DIMENSION_SETTINGS = "Dimension Settings";
    public static final String CROP_FERTILITY_GENERAL = "Crop Fertility General Settings";
    public static final String CROP_FERTILITY_SEASONAL = "Crop Fertility Seasonal Settings";

    public boolean generateSnowAndIce;
    public boolean changeWeatherFrequency;
    
    public boolean changeGrassColour;
    public boolean changeFoliageColour;
    
    public String[] whitelistedDimensions;

    public SeasonsConfig(File configFile)
    {
        super(configFile, "Seasons Settings");
    }

    @Override
    protected void loadConfiguration()
    {
        try
        {
            addSyncedValue(SeasonsOption.DAY_DURATION, 24000, TIME_SETTINGS, "The duration of a Minecraft day in ticks", 20, Integer.MAX_VALUE);
            addSyncedValue(SeasonsOption.SUB_SEASON_DURATION, 7, TIME_SETTINGS, "The duration of a sub season in days", 1, Integer.MAX_VALUE);
            addSyncedValue(SeasonsOption.STARTING_SUB_SEASON, 5, TIME_SETTINGS, "The starting sub season for new worlds.  0 = Random, 1 - 3 = Early/Mid/Late Spring, 4 - 6 = Early/Mid/Late Summer, 7 - 9 = Early/Mid/Late Autumn, 10 - 12 = Early/Mid/Late Winter", 0, 12);
            addSyncedValue(SeasonsOption.PROGRESS_SEASON_WHILE_OFFLINE, true, TIME_SETTINGS, "If the season should progress on a server with no players online");

            generateSnowAndIce = config.getBoolean("Generate Snow and Ice", WEATHER_SETTINGS, true, "Generate snow and ice during the Winter season");
            changeWeatherFrequency = config.getBoolean("Change Weather Frequency", WEATHER_SETTINGS, true, "Change the frequency of rain/snow/storms based on the season");
            
            // Client-only. The server shouldn't get to decide these.
            changeGrassColour = config.getBoolean("Change Grass Colour Seasonally", AESTHETIC_SETTINGS, true, "Change the grass colour based on the current season");
            changeFoliageColour = config.getBoolean("Change Foliage Colour Seasonally", AESTHETIC_SETTINGS, true, "Change the foliage colour based on the current season");

            whitelistedDimensions = config.getStringList("Whitelisted Dimensions", DIMENSION_SETTINGS, new String[] { "0" }, "Seasons will only apply to dimensons listed here");

            loadFertilityConfig();
        }
        catch (Exception e)
        {
            SereneSeasons.logger.error("Serene Seasons has encountered a problem loading seasons.cfg", e);
        }
        finally
        {
            if (config.hasChanged()) config.save();
        }
    }
    
    private void loadFertilityConfig()
    {
        FertilityConfig.general_category.seasonal_crops = config.getBoolean("seasonal_crops", CROP_FERTILITY_GENERAL, FertilityConfig.general_category.seasonal_crops, "Whether crops are affected by seasons.");
        FertilityConfig.general_category.crops_break = config.getBoolean("crops_break", CROP_FERTILITY_GENERAL, FertilityConfig.general_category.crops_break, "Whether crops break if out of season. If false, they simply don't grow");
        FertilityConfig.general_category.ignore_unlisted_crops = config.getBoolean("ignore_unlisted_crops", CROP_FERTILITY_GENERAL, FertilityConfig.general_category.ignore_unlisted_crops, "Whether unlisted seeds are fertile every season. False means they're fertile every season except Winter");
        FertilityConfig.general_category.crop_tooltips = config.getBoolean("crop_tooltips", CROP_FERTILITY_GENERAL, FertilityConfig.general_category.crop_tooltips, "Whether to include tooltips on crops listing which seasons they're fertile in. Note: This only applies to listed crops.");
        FertilityConfig.general_category.greenhouse_glass_max_height = config.getInt("greenhouse_glass_max_height", CROP_FERTILITY_GENERAL, FertilityConfig.general_category.greenhouse_glass_max_height, 1, 255, "Maximum height greenhouse glass can be above a crop for it to be fertile out of season");

        FertilityConfig.seasonal_fertility.spring_crops = config.getStringList("spring_crops", CROP_FERTILITY_SEASONAL, FertilityConfig.seasonal_fertility.spring_crops, "Crops growable in Spring (List either the seed item for the crop, or the crop block itself)");
        FertilityConfig.seasonal_fertility.summer_crops = config.getStringList("summer_crops", CROP_FERTILITY_SEASONAL, FertilityConfig.seasonal_fertility.summer_crops, "Crops growable in Summer (List either the seed item for the crop, or the crop block itself)");
        FertilityConfig.seasonal_fertility.autumn_crops = config.getStringList("autumn_crops", CROP_FERTILITY_SEASONAL, FertilityConfig.seasonal_fertility.autumn_crops, "Crops growable in Autumn (List either the seed item for the crop, or the crop block itself)");
        FertilityConfig.seasonal_fertility.winter_crops = config.getStringList("winter_crops", CROP_FERTILITY_SEASONAL, FertilityConfig.seasonal_fertility.winter_crops, "Crops growable in Winter (List either the seed item for the crop, or the crop block itself)");
    }

    public static boolean isDimensionWhitelisted(int dimension)
    {
    	for (String dimensions : ModConfig.seasons.whitelistedDimensions)
		{
    		if (dimension == Integer.valueOf(dimensions))
			{
    			return true;
			}
		}
    	
    	return false;
    }
}
