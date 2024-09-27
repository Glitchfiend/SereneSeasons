package sereneseasons.config;

import static net.minecraft.server.level.ServerLevel.RAIN_DELAY;

import blue.endless.jankson.Comment;
import com.google.common.collect.Lists;
import io.wispforest.owo.config.Option.SyncMode;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.PredicateConstraint;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.Season;

@Sync(SyncMode.OVERRIDE_CLIENT)
@Config(name = "SereneSeasons_seasons", wrapperName = "SeasonsConfig")
public class SeasonsConfigModel {

  // INTERNAL STUFF
  private static sereneseasons.config.SeasonsConfig INSTANCE;
  public static void setInstance(sereneseasons.config.SeasonsConfig value) {
    INSTANCE = value;
  }

  // From ServerLevel
  private static final IntProvider THUNDER_DELAY = UniformInt.of(12000, 180000);

  public static boolean isDimensionWhitelisted(ResourceKey<Level> dimension)
  {
    for (String whitelistedDimension : INSTANCE.whitelistedDimensions())
    {
      if (dimension.location().toString().equals(whitelistedDimension))
      {
        return true;
      }
    }

    return false;
  }

  @Nullable
  public static sereneseasons.config.SeasonsConfig.SeasonProperties getSeasonProperties(Season.SubSeason season)
  {
    switch (season) {
      case EARLY_WINTER -> {
        return INSTANCE.earlyWinterProperties;
      }
      case MID_WINTER -> {
        return INSTANCE.midWinterProperties;
      }
      case LATE_WINTER -> {
        return INSTANCE.lateWinterProperties;
      }
      case EARLY_SPRING -> {
        return INSTANCE.earlySpringProperties;
      }
      case MID_SPRING -> {
        return INSTANCE.midSpringProperties;
      }
      case LATE_SPRING -> {
        return INSTANCE.lateSpringProperties;
      }
      case EARLY_SUMMER -> {
        return INSTANCE.earlySummerProperties;
      }
      case MID_SUMMER -> {
        return INSTANCE.midSummerProperties;
      }
      case LATE_SUMMER -> {
        return INSTANCE.lateSummerProperties;
      }
      case EARLY_AUTUMN -> {
        return INSTANCE.earlyAutumnProperties;
      }
      case MID_AUTUMN -> {
        return INSTANCE.midAutumnProperties;
      }
      case LATE_AUTUMN -> {
        return INSTANCE.lateAutumnProperties;
      }
    }
    return null;
  }

  public static boolean canRain(sereneseasons.config.SeasonsConfig.SeasonProperties seasonProperties)
  {
    return seasonProperties.minRainTime() != -1 && seasonProperties.maxRainTime() != -1;
  }
  public static boolean canThunder(sereneseasons.config.SeasonsConfig.SeasonProperties seasonProperties)
  {
    return seasonProperties.minThunderTime() != -1 && seasonProperties.maxThunderTime() != -1;
  }

  @Nest public SeasonProperties earlyWinterProperties = new SeasonProperties(0.0F, 0, -0.8F, 12000, 36000, -1, -1);
  @Nest public SeasonProperties midWinterProperties = new SeasonProperties(0.0F, 0, -0.8F, 12000, 36000, -1, -1);
  @Nest public SeasonProperties lateWinterProperties = new SeasonProperties(0.0F, 0, -0.8F, 12000, 36000, -1, -1);
  @Nest public SeasonProperties earlySpringProperties = new SeasonProperties(6.25F, 1, -0.25F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties midSpringProperties = new SeasonProperties(8.33F, 1, 0.0F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties lateSpringProperties = new SeasonProperties(12.5F, 1, 0.0F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties earlySummerProperties = new SeasonProperties(25.0F, 1, 0.0F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties midSummerProperties = new SeasonProperties(25.0F, 1, 0.0F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties lateSummerProperties = new SeasonProperties(25.0F, 1, 0.0F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties earlyAutumnProperties = new SeasonProperties(12.5F, 1, 0.0F, RAIN_DELAY.getMinValue(), RAIN_DELAY.getMaxValue(), THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties midAutumnProperties = new SeasonProperties(8.33F, 1, 0.0F, RAIN_DELAY.getMinValue(), RAIN_DELAY.getMaxValue(), THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties lateAutumnProperties = new SeasonProperties(6.25F, 1, -0.25F, RAIN_DELAY.getMinValue(), RAIN_DELAY.getMaxValue(), THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());

  public static class SeasonProperties {
    @Comment("meltChance is the 0-1 percentage chance a snow or ice block will melt when chosen. (e.g. 100.0 = 100%, 50.0 = 50%)")
    public float meltChance;
    @Comment("meltRolls is the number of blocks randomly picked in each chunk, each tick. (High number rolls is not recommended on servers)\nmeltRolls should be 0 if blocks should not melt in that season.")
    public int meltRolls;
    @Comment("biomeTempAdjustments is the amount to adjust the biome temperature by from -10.0 to 10.0.")
    public float biomeTempAdjustments;
    @Comment("minRainTime is the minimum time interval between rain events in ticks. Set to -1 to disable rain.")
    public int minRainTime;
    @Comment("maxRainTime is the maximum time interval between rain events in ticks. Set to -1 to disable rain.")
    public int maxRainTime;
    @Comment("minThunderTime is the minimum time interval between thunder events in ticks. Set to -1 to disable thunder.")
    public int minThunderTime;
    @Comment("max_thunder_time is the maximum time interval between thunder events in ticks. Set to -1 to disable thunder.")
    public int maxThunderTime;
    SeasonProperties(float meltChance, int meltRolls,
        float biomeTempAdjustment, int minRainTime, int maxRainTime, int minThunderTime, int maxThunderTime) {
      this.meltChance = meltChance;
      this.meltRolls = meltRolls;
      this.biomeTempAdjustments = biomeTempAdjustment;
      this.minRainTime = minRainTime;
      this.maxRainTime = maxRainTime;
      this.minThunderTime = minThunderTime;
      this.maxThunderTime = maxThunderTime;
    }
  }

  // SETTINGS
  // Weather settings
  @Comment("Generate snow and ice during the Winter season")
  public boolean generateSnowAndIce = true;
  @Comment("Change the frequency of rain/snow/storms based on the season")
  public boolean changeWeatherFrequency = true;

  // Time settings
  @Comment("The duration of a Minecraft day in ticks.\nThis only adjusts the internal length of a day used by the season cycle.\nIt is intended to be used in conjunction with another mod which adjusts the actual length of a Minecraft day.")
  @RangeConstraint(min = 20, max = Integer.MAX_VALUE)
  public int dayDuration = 24000;
  @Comment("The duration of a sub season in days.")
  @RangeConstraint(min = 1, max = Integer.MAX_VALUE)
  public int subSeasonDuration = 8;
  @Comment("The starting sub season for new worlds.\n0 = Random, 1 - 3 = Early/Mid/Late Spring\n4 - 6 = Early/Mid/Late Summer\n7 - 9 = Early/Mid/Late Autumn\n10 - 12 = Early/Mid/Late Winter")
  @RangeConstraint(min = 0, max = 12)
  public int startingSubSeason = 1;
  @Comment("If the season should progress on a server with no players online")
  public boolean progressSeasonWhileOffline = true;

  // Aesthetic settings
  @Comment("Change the grass color based on the current season")
  public boolean changeGrassColor = true;
  @Comment("Change the foliage color based on the current season")
  public boolean changeFoliageColor = true;
  @Comment("Change the birch color based on the current season")
  public boolean changeBirchColor = true;

  // Dimension settings
  @Comment("Seasons will only apply to dimensons listed here")
  @PredicateConstraint("resourceLocationValidate")
  public List<String> whitelistedDimensions = Lists.newArrayList(Level.OVERWORLD.location().toString());

  public static boolean resourceLocationValidate(List<String> list) {
    for (String s : list) {
      try {
        new ResourceLocation(s);
      } catch (Exception e) {
        return false; // Can't convert to a resource location, therefore this object is invalid
      }
    }
    return true;
  }
}
