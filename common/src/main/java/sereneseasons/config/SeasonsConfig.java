/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.InMemoryFormat;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import glitchcore.util.Environment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.Season;
import sereneseasons.core.SereneSeasons;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SeasonsConfig extends glitchcore.config.Config
{
    // Weather settings
    public boolean generateSnowAndIce;
    public boolean changeWeatherFrequency;

    // Time settings
    public int dayDuration;
    public int subSeasonDuration;
    public int startingSubSeason;
    public boolean progressSeasonWhileOffline;

    // Aesthetic settings
    public boolean changeGrassColor;
    public boolean changeFoliageColor;
    public boolean changeBirchColor;

    // Dimension settings
    public static List<String> whitelistedDimensions;
    private static List<String> defaultWhitelistedDimensions = Lists.newArrayList(Level.OVERWORLD.location().toString());

    // Snow melting settings
    private static List<Config> meltChanceEntries;
    private static List<Config> defaultMeltChances = Lists.newArrayList(
            new MeltChanceInfo(Season.SubSeason.EARLY_WINTER, 0.0F, 0),
            new MeltChanceInfo(Season.SubSeason.MID_WINTER, 0.0F, 0),
            new MeltChanceInfo(Season.SubSeason.LATE_WINTER, 0.0F, 0),
            new MeltChanceInfo(Season.SubSeason.EARLY_SPRING, 6.25F, 1),
            new MeltChanceInfo(Season.SubSeason.MID_SPRING, 8.33F, 1),
            new MeltChanceInfo(Season.SubSeason.LATE_SPRING, 12.5F, 1),
            new MeltChanceInfo(Season.SubSeason.EARLY_SUMMER, 25.0F, 1),
            new MeltChanceInfo(Season.SubSeason.MID_SUMMER, 25.0F, 1),
            new MeltChanceInfo(Season.SubSeason.LATE_SUMMER, 25.0F, 1),
            new MeltChanceInfo(Season.SubSeason.EARLY_AUTUMN, 12.5F, 1),
            new MeltChanceInfo(Season.SubSeason.MID_AUTUMN, 8.33F, 1),
            new MeltChanceInfo(Season.SubSeason.LATE_AUTUMN, 6.25F, 1)
    ).stream().map(SeasonsConfig::meltChanceInfoToConfig).collect(Collectors.toList());

    private static final Predicate<List<String>> RESOURCE_LOCATION_VALIDATOR = (list) ->
    {
        for (String s : list)
        {
            try
            {
                new ResourceLocation(s);
            }
            catch (Exception e)
            {
                // Can't convert to a resource location, therefore this object is invalid
                return false;
            }
        }
        return true;
    };

    private static final Predicate<List<Config>> MELT_INFO_VALIDATOR = (configs) ->
    {
        for (Config config : configs)
        {
            // Ensure config contains required values
            if (!config.contains("season")) return false;
            if (!config.contains("melt_percent")) return false;
            if (!config.contains("rolls")) return false;

            try
            {
                // Validate season.
                config.getEnum("season", Season.SubSeason.class);

                // Validate melt chance is within range.
                float meltChance = config.<Number>get("melt_percent").floatValue();
                if(meltChance < 0.0F || meltChance > 100.0F) return false;
                // Validate rolls is positive.
                if(config.getInt("rolls") < 0) return false;
            }
            catch (Exception e)
            {
                return false;
            }
        }

        return true;
    };

    public SeasonsConfig()
    {
        super(Environment.getConfigPath().resolve(SereneSeasons.MOD_ID + "/seasons.toml"));
    }

    @Override
    public void load()
    {
        generateSnowAndIce = add("weather_settings.generate_snow_ice", true, "Generate snow and ice during the Winter season");
        changeWeatherFrequency = add("weather_settings.change_weather_frequency", true, "Change the frequency of rain/snow/storms based on the season");

        dayDuration = addNumber("time_settings.day_duration", 24000, 20, Integer.MAX_VALUE, "The duration of a Minecraft day in ticks.\nThis only adjusts the internal length of a day used by the season cycle.\nIt is intended to be used in conjunction with another mod which adjusts the actual length of a Minecraft day.");
        subSeasonDuration = addNumber("time_settings.sub_season_duration", 8, 1, Integer.MAX_VALUE, "The duration of a sub season in days.");
        startingSubSeason = addNumber("time_settings.starting_sub_season", 1, 0, 12, "The starting sub season for new worlds.\n0 = Random, 1 - 3 = Early/Mid/Late Spring\n4 - 6 = Early/Mid/Late Summer\n7 - 9 = Early/Mid/Late Autumn\n10 - 12 = Early/Mid/Late Winter");
        progressSeasonWhileOffline = add("time_settings.progress_season_while_offline", true, "If the season should progress on a server with no players online");

        changeGrassColor = add("aesthetic_settings.change_grass_color", true, "Change the grass color based on the current season");
        changeFoliageColor = add("aesthetic_settings.change_foliage_color", true, "Change the foliage colour based on the current season");
        changeBirchColor = add("aesthetic_settings.change_birch_color", true, "Change the birch colour based on the current season");

        whitelistedDimensions = add("dimension_settings.whitelisted_dimensions", defaultWhitelistedDimensions, "Seasons will only apply to dimensons listed here", RESOURCE_LOCATION_VALIDATOR);

        meltChanceEntries = add("melting_settings.season_melt_chances", defaultMeltChances, """
                The melting settings for snow and ice in each season. The game must be restarted for these to apply.
                melt_percent is the 0-1 percentage chance a snow or ice block will melt when chosen. (e.g. 100.0 = 100%, 50.0 = 50%)
                rolls is the number of blocks randomly picked in each chunk, each tick. (High number rolls is not recommended on servers)
                rolls should be 0 if blocks should not melt in that season.""", MELT_INFO_VALIDATOR);
    }

    public boolean isDimensionWhitelisted(ResourceKey<Level> dimension)
    {
        for (String whitelistedDimension : whitelistedDimensions)
        {
            if (dimension.location().toString().equals(whitelistedDimension))
            {
                return true;
            }
        }

        return false;
    }

    private static Config meltChanceInfoToConfig(MeltChanceInfo meltChanceInfo)
    {
        Config config = Config.of(LinkedHashMap::new, InMemoryFormat.withUniversalSupport());
        config.add("season", meltChanceInfo.getSubSeason().toString());
        config.add("melt_percent", meltChanceInfo.getMeltChance());
        config.add("rolls", meltChanceInfo.getRolls());
        return config;
    }

    private static ImmutableMap<Season.SubSeason, MeltChanceInfo> meltInfoCache;

    @Nullable
    public MeltChanceInfo getMeltInfo(Season.SubSeason season)
    {
        return getMeltInfos().get(season);
    }

    private ImmutableMap<Season.SubSeason, MeltChanceInfo> getMeltInfos()
    {
        if (meltInfoCache != null) return meltInfoCache;

        Map<Season.SubSeason, MeltChanceInfo> tmp = Maps.newHashMap();

        for (Config config : meltChanceEntries)
        {
            Season.SubSeason subSeason = config.getEnum("season", Season.SubSeason.class);
            float meltChance = config.<Number>get("melt_percent").floatValue();
            int rolls = config.getInt("rolls");

            tmp.put(subSeason, new MeltChanceInfo(subSeason, meltChance, rolls));
        }

        meltInfoCache = ImmutableMap.copyOf(tmp);
        return meltInfoCache;
    }

    public static class MeltChanceInfo
    {
        private final Season.SubSeason subSeason;
        private final float meltChance;
        private final int rolls;

        private MeltChanceInfo(Season.SubSeason subSeason, float meltChance, int rolls)
        {
            this.subSeason = subSeason;
            this.meltChance = meltChance;
            this.rolls = rolls;
        }

        public Season.SubSeason getSubSeason()
        {
            return subSeason;
        }

        public float getMeltChance()
        {
            return meltChance;
        }

        public int getRolls()
        {
            return rolls;
        }

    }
}