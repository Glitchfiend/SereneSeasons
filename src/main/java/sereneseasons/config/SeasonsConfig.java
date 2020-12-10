/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.config;

import com.google.common.collect.Lists;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.Predicate;

public class SeasonsConfig
{
    public static final String TIME_SETTINGS = "Time Settings";
    public static final String WEATHER_SETTINGS = "Weather Settings";
    public static final String AESTHETIC_SETTINGS = "Aesthetic Settings";
    public static final String DIMENSION_SETTINGS = "Dimension Settings";

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Time settings
    public static ForgeConfigSpec.IntValue dayDuration;
    public static ForgeConfigSpec.IntValue subSeasonDuration;
    public static ForgeConfigSpec.IntValue startingSubSeason;
    public static ForgeConfigSpec.BooleanValue progressSeasonWhileOffline;

    // Weather settings
    public static ForgeConfigSpec.BooleanValue generateSnowAndIce;
    public static ForgeConfigSpec.BooleanValue changeWeatherFrequency;

    // Aesthetic settings
    public static ForgeConfigSpec.BooleanValue changeGrassColor;
    public static ForgeConfigSpec.BooleanValue changeFoliageColor;
    public static ForgeConfigSpec.BooleanValue changeBirchColor;

    // Dimension settings
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistedDimensions;

    private static List<String> defaultWhitelistedDimensions = Lists.newArrayList(World.OVERWORLD.location().toString());
    private static final Predicate<Object> DIMENSION_VALIDATOR = (obj) ->
    {
        if (!(obj instanceof String))
            return false;

        try
        {
            new ResourceLocation((String)obj);
        }
        catch (Exception e)
        {
            // Can't convert to a resource location, therefore this object is invalid
            return false;
        }

        return true;
    };

    static
    {
        BUILDER.push("time_settings");
        dayDuration = BUILDER.comment("The duration of a Minecraft day in ticks.\nThis only adjusts the internal length of a day used by the season cycle.\nIt is intended to be used in conjunction with another mod which adjusts the actual length of a Minecraft day.").defineInRange("day_duration", 24000, 20, Integer.MAX_VALUE);
        subSeasonDuration = BUILDER.comment("The duration of a sub season in days").defineInRange("sub_season_duration", 8, 1, Integer.MAX_VALUE);
        startingSubSeason = BUILDER.comment("The starting sub season for new worlds.\n0 = Random, 1 - 3 = Early/Mid/Late Spring\n4 - 6 = Early/Mid/Late Summer\n7 - 9 = Early/Mid/Late Autumn\n10 - 12 = Early/Mid/Late Winter").defineInRange("starting_sub_season", 1, 0, 12);
        progressSeasonWhileOffline = BUILDER.comment("If the season should progress on a server with no players online").define("progress_season_while_offline", true);
        BUILDER.pop();

        BUILDER.push("weather_settings");
        generateSnowAndIce = BUILDER.comment("Generate snow and ice during the Winter season").define("generate_snow_ice", true);
        changeWeatherFrequency = BUILDER.comment("Change the frequency of rain/snow/storms based on the season").define("change_weather_frequency", true);
        BUILDER.pop();

        BUILDER.push("aesthetic_settings");
        changeGrassColor = BUILDER.comment("Change the grass color based on the current season").define("change_grass_color", true);
        changeFoliageColor = BUILDER.comment("Change the foliage colour based on the current season").define("change_foliage_color", true);
        changeBirchColor = BUILDER.comment("Change the birch colour based on the current season").define("change_birch_color", true);
        BUILDER.pop();

        BUILDER.push("time_settings");
        whitelistedDimensions = BUILDER.comment("Seasons will only apply to dimensons listed here").defineList("whitelisted_dimensions", defaultWhitelistedDimensions, DIMENSION_VALIDATOR);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
    
    public static boolean isDimensionWhitelisted(RegistryKey<World> dimension)
    {
    	for (String whitelistedDimension : whitelistedDimensions.get())
		{
    		if (dimension.location().toString().equals(whitelistedDimension))
			{
    			return true;
			}
		}
    	
    	return false;
    }
}
