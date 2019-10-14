/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.config;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.Season;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModConfig;

public class SeasonsConfig extends ConfigHandler
{
    public static final String TIME_SETTINGS = "Time Settings";
    public static final String WEATHER_SETTINGS = "Weather Settings";
    public static final String AESTHETIC_SETTINGS = "Aesthetic Settings";
    public static final String DIMENSION_SETTINGS = "Dimension Settings";

    public boolean generateSnowAndIce;
    public boolean changeWeatherFrequency;
    
    public boolean changeGrassColour;
    public boolean changeFoliageColour;
    public boolean changeBirchColour;
    
    public Set<Integer> whitelistedDimensions;
    public Map<Integer, Integer> lockedDimensions;

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
            changeBirchColour = config.getBoolean("Change Birch Colour Seasonally", AESTHETIC_SETTINGS, true, "Change the birch colour based on the current season");
            
            whitelistedDimensions = new HashSet<>();
            lockedDimensions = new HashMap<>();
            
            String[] dimensionList = config.getStringList("Whitelisted Dimensions", DIMENSION_SETTINGS, new String[] { "0" }, "Seasons will only apply to dimensons listed here");
            for (String dimensionString : dimensionList)
            {
            	whitelistedDimensions.add(Integer.valueOf(dimensionString));
            }
            
            String[] dimensionLockList = config.getStringList("Locked Dimensions", DIMENSION_SETTINGS, new String[] {""}, "Lock specific dimensions at a given time of the season cycle. The dimensions will be whitelisted automatically. Format: dimensionID:time. time must be between 0 and day_duration * sub_season_duration * 12. If not, it will be considered being 0.");
            for (String dimensionLockString : dimensionLockList)
            {
            	int dimension = Integer.valueOf(dimensionLockString.split(":")[0]);
            	int timelock = Integer.valueOf(dimensionLockString.split(":")[1]);
            	
            	whitelistedDimensions.add(dimension);
            	if (timelock > Season.SubSeason.VALUES.length * SyncedConfig.getIntValue(SeasonsOption.SUB_SEASON_DURATION) * SyncedConfig.getIntValue(SeasonsOption.DAY_DURATION))
            	{
            		SereneSeasons.logger.warn(String.format("Dimension %d has been locked to a time beyond the season cycle, treating as 0"));
            		timelock = 0;
            	}
            	lockedDimensions.put(dimension, timelock);
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
    	return ModConfig.seasons.whitelistedDimensions.contains(dimension);
    }
    
   public static boolean isDimensionLocked(int dimension)
   {
	   return ModConfig.seasons.lockedDimensions.containsKey(dimension);
   }
   
   public static int getDimensionTimelock(int dimension)
   {
	   return ModConfig.seasons.lockedDimensions.get(dimension);
   }  
   
}
