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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.api.season.Season.SubSeason;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ServerConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Time settings
    public static ForgeConfigSpec.IntValue dayDuration;
    public static ForgeConfigSpec.IntValue subSeasonDuration;
    public static ForgeConfigSpec.IntValue startingSubSeason;
    public static ForgeConfigSpec.BooleanValue progressSeasonWhileOffline;

    // Aesthetic settings
    public static ForgeConfigSpec.BooleanValue changeGrassColor;
    public static ForgeConfigSpec.BooleanValue changeFoliageColor;
    public static ForgeConfigSpec.BooleanValue changeBirchColor;

    // Dimension settings
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistedDimensions;
    private static List<String> defaultWhitelistedDimensions = Lists.newArrayList(Level.OVERWORLD.location().toString());

    // Snow melting settings
    private static ForgeConfigSpec.ConfigValue<List<Config>> meltChanceEntries;
    private static List<Config> defaultMeltChances = Lists.newArrayList(
            new MeltChanceInfo(SubSeason.EARLY_WINTER, 0, 1),
            new MeltChanceInfo(SubSeason.MID_WINTER, 0, 1),
            new MeltChanceInfo(SubSeason.LATE_WINTER, 0, 1),
            new MeltChanceInfo(SubSeason.EARLY_SPRING, 16, 1),
            new MeltChanceInfo(SubSeason.MID_SPRING, 12, 1),
            new MeltChanceInfo(SubSeason.LATE_SPRING, 8, 1),
            new MeltChanceInfo(SubSeason.EARLY_SUMMER, 4, 1),
            new MeltChanceInfo(SubSeason.MID_SUMMER, 4, 1),
            new MeltChanceInfo(SubSeason.LATE_SUMMER, 4, 1),
            new MeltChanceInfo(SubSeason.EARLY_AUTUMN, 8, 1),
            new MeltChanceInfo(SubSeason.MID_AUTUMN, 12, 1),
            new MeltChanceInfo(SubSeason.LATE_AUTUMN, 16, 1)
    ).stream().map(ServerConfig::meltChanceInfoToConfig).collect(Collectors.toList());

    private static final Predicate<Object> RESOURCE_LOCATION_VALIDATOR = (obj) ->
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

    private static final Predicate<Object> MELT_INFO_VALIDATOR = (obj) ->
    {
        if (!(obj instanceof List)) return false;

        for (Object i : (List)obj)
        {
            if (!(i instanceof Config config)) return false;

            // Ensure config contains required values
            if (!config.contains("season")) return false;
            if (!config.contains("melt_chance")) return false;
            if (!config.contains("rolls")) return false;

            try
            {
                // Validate season.
                config.getEnum("season", SubSeason.class);
            }
            catch (Exception e)
            {
                return false;
            }
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

        BUILDER.push("aesthetic_settings");
        changeGrassColor = BUILDER.comment("Change the grass color based on the current season").define("change_grass_color", true);
        changeFoliageColor = BUILDER.comment("Change the foliage colour based on the current season").define("change_foliage_color", true);
        changeBirchColor = BUILDER.comment("Change the birch colour based on the current season").define("change_birch_color", true);
        BUILDER.pop();

        BUILDER.push("dimension_settings");
        whitelistedDimensions = BUILDER.comment("Seasons will only apply to dimensons listed here").defineList("whitelisted_dimensions", defaultWhitelistedDimensions, RESOURCE_LOCATION_VALIDATOR);
        BUILDER.pop();

        BUILDER.push("melting_settings");
        meltChanceEntries = BUILDER.comment("The chance for a snow or ice block to melt per-tick in each season. The game must be restarted for these to apply.\nThe chance for a block to melt is is 1/melt_chance.\nIf melt_chance <= 0 or rolls <= 0, blocks will not melt during that subseason.").define("season_melt_chances", defaultMeltChances, MELT_INFO_VALIDATOR);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static boolean isDimensionWhitelisted(ResourceKey<Level> dimension)
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

    private static Config meltChanceInfoToConfig(MeltChanceInfo meltChanceInfo)
    {
        Config config = Config.of(LinkedHashMap::new, InMemoryFormat.withUniversalSupport());
        config.add("season", meltChanceInfo.getSubSeason().toString());
        config.add("melt_chance", meltChanceInfo.getMeltChance());
        return config;
    }

    private static ImmutableMap<SubSeason, MeltChanceInfo> meltInfoCache;

    @Nullable
    public static MeltChanceInfo getMeltInfo(SubSeason season)
    {
        return getMeltInfos().get(season);
    }

    private static ImmutableMap<SubSeason, MeltChanceInfo> getMeltInfos()
    {
        if (meltInfoCache != null) return meltInfoCache;

        Map<SubSeason, MeltChanceInfo> tmp = Maps.newHashMap();

        for (Config config : meltChanceEntries.get())
        {
            SubSeason subSeason = config.getEnum("season", SubSeason.class);
            int meltChance = config.getInt("melt_chance");
            int rolls = config.getInt("rolls");

            tmp.put(subSeason, new MeltChanceInfo(subSeason, meltChance, rolls));
        }

        meltInfoCache = ImmutableMap.copyOf(tmp);
        return meltInfoCache;
    }

    public static class MeltChanceInfo
    {
        private SubSeason subSeason;
        private int meltChance;
        private int rolls;

        private MeltChanceInfo(SubSeason subSeason, int meltChance, int rolls)
        {
            this.subSeason = subSeason;
            this.meltChance = meltChance;
            this.rolls = rolls;
        }

        public SubSeason getSubSeason()
        {
            return subSeason;
        }

        public int getMeltChance()
        {
            return meltChance;
        }

        public int getRolls()
        {
            return rolls;
        }

    }
}
