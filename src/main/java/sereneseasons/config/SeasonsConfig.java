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
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModConfig;

public class SeasonsConfig extends ConfigHandler
{
    public static final String TIME_SETTINGS = "Time Settings";
    public static final String WEATHER_SETTINGS = "Weather Settings";
    public static final String AESTHETIC_SETTINGS = "Aesthetic Settings";
    public static final String DIMENSION_SETTINGS = "Dimension Settings";
    public static final String PERFORMANCE_SETTINGS = "Performance Settings";
    public static final String ALPHA_FEATURE_SETTINGS = "Alpha Features Settings";
    
    public boolean generateSnowAndIce;
    public boolean changeWeatherFrequency;
    
    public boolean changeGrassColour;
    public boolean changeFoliageColour;
    public boolean changeBirchColour;

    public String[] whitelistedDimensions;

    public SeasonsConfig(File configFile)
    {
        super(configFile, "Seasons Settings");
    }

    @Override
    protected void loadConfiguration()
    {
    	// SYNC: Keep it up-to-date if sereneseasons.handler.season.SeasonChunkPatchingHandler.isEnabled() changes.
    	
        try
        {
            addSyncedValue(SeasonsOption.DAY_DURATION, 24000, TIME_SETTINGS, "The duration of a Minecraft day in ticks", 20, Integer.MAX_VALUE);
            addSyncedValue(SeasonsOption.SUB_SEASON_DURATION, 7, TIME_SETTINGS, "The duration of a sub season in days", 1, Integer.MAX_VALUE);
            addSyncedValue(SeasonsOption.NUM_PATCHES_PER_TICK, 20, PERFORMANCE_SETTINGS, "The amount of chunk patches per server tick. Lower number increases server performance, but increases popping artifacts.", 1, Integer.MAX_VALUE);
            addSyncedValue(SeasonsOption.PATCH_TICK_DISTANCE, 20 * 5, PERFORMANCE_SETTINGS, "The amount of ticks to keep between patching a chunk.", 0, Integer.MAX_VALUE);
            addSyncedValue(SeasonsOption.ENABLE_GLOBAL_FROST, false, ALPHA_FEATURE_SETTINGS, "If enabled then weather effects like freezing/snowing are applied globally.");
            addSyncedValue(SeasonsOption.STARTING_SUB_SEASON, 5, TIME_SETTINGS, "The starting sub season for new worlds.  0 = Random, 1 - 3 = Early/Mid/Late Spring, 4 - 6 = Early/Mid/Late Summer, 7 - 9 = Early/Mid/Late Autumn, 10 - 12 = Early/Mid/Late Winter", 0, 12);
            
            generateSnowAndIce = config.getBoolean("Generate Snow and Ice", WEATHER_SETTINGS, true, "Generate snow and ice during the Winter season");
            changeWeatherFrequency = config.getBoolean("Change Weather Frequency", WEATHER_SETTINGS, true, "Change the frequency of rain/snow/storms based on the season");
            
            // Client-only. The server shouldn't get to decide these.
            changeGrassColour = config.getBoolean("Change Grass Colour Seasonally", AESTHETIC_SETTINGS, true, "Change the grass colour based on the current season");
            changeFoliageColour = config.getBoolean("Change Foliage Colour Seasonally", AESTHETIC_SETTINGS, true, "Change the foliage colour based on the current season");
            changeBirchColour = config.getBoolean("Change Birch Colour Seasonally", AESTHETIC_SETTINGS, true, "Change the birch colour based on the current season");
        
            whitelistedDimensions = config.getStringList("Whitelisted Dimensions", DIMENSION_SETTINGS, new String[] { "0" }, "Seasons will only apply to dimensons listed here");
            
            // Print some warnings for incompatible configs
            if( !generateSnowAndIce && SyncedConfig.getBooleanValue(SeasonsOption.ENABLE_GLOBAL_FROST) ) {
            	SereneSeasons.logger.warn("Configuration field \"Enable Global Frost\" is not compatible with disabled \"Generate Snow and Ice\". It will be ignored.");
            }
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
