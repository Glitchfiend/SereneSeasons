package sereneseasons.config;

import static net.minecraft.server.level.ServerLevel.RAIN_DELAY;

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
import sereneseasons.config.SeasonsConfig.SeasonProperties;

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

  @Nest public SeasonProperties earlyWinterProperties = new SeasonProperties(Season.SubSeason.EARLY_WINTER, 0.0F, 0, -0.8F, 12000, 36000, -1, -1);
  @Nest public SeasonProperties midWinterProperties = new SeasonProperties(Season.SubSeason.MID_WINTER, 0.0F, 0, -0.8F, 12000, 36000, -1, -1);
  @Nest public SeasonProperties lateWinterProperties = new SeasonProperties(Season.SubSeason.LATE_WINTER, 0.0F, 0, -0.8F, 12000, 36000, -1, -1);
  @Nest public SeasonProperties earlySpringProperties = new SeasonProperties(Season.SubSeason.EARLY_SPRING, 6.25F, 1, -0.25F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties midSpringProperties = new SeasonProperties(Season.SubSeason.MID_SPRING, 8.33F, 1, 0.0F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties lateSpringProperties = new SeasonProperties(Season.SubSeason.LATE_SPRING, 12.5F, 1, 0.0F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties earlySummerProperties = new SeasonProperties(Season.SubSeason.EARLY_SUMMER, 25.0F, 1, 0.0F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties midSummerProperties = new SeasonProperties(Season.SubSeason.MID_SUMMER, 25.0F, 1, 0.0F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties lateSummerProperties = new SeasonProperties(Season.SubSeason.LATE_SUMMER, 25.0F, 1, 0.0F, 12000, 96000, THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties earlyAutumnProperties = new SeasonProperties(Season.SubSeason.EARLY_AUTUMN, 12.5F, 1, 0.0F, RAIN_DELAY.getMinValue(), RAIN_DELAY.getMaxValue(), THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties midAutumnProperties = new SeasonProperties(Season.SubSeason.MID_AUTUMN, 8.33F, 1, 0.0F, RAIN_DELAY.getMinValue(), RAIN_DELAY.getMaxValue(), THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());
  @Nest public SeasonProperties lateAutumnProperties = new SeasonProperties(Season.SubSeason.LATE_AUTUMN, 6.25F, 1, -0.25F, RAIN_DELAY.getMinValue(), RAIN_DELAY.getMaxValue(), THUNDER_DELAY.getMinValue(), THUNDER_DELAY.getMaxValue());

  public static class SeasonProperties {
    public Season.SubSeason subSeason;
    public float meltChance;
    public int meltRolls;
    public float biomeTempAdjustments;
    public int minRainTime;
    public int maxRainTime;
    public int minThunderTime;
    public int maxThunderTime;
    SeasonProperties(Season.SubSeason subSeason, float meltChance, int meltRolls,
        float biomeTempAdjustment, int minRainTime, int maxRainTime, int minThunderTime, int maxThunderTime) {
      this.subSeason = subSeason;
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
  public boolean generateSnowAndIce = true;
  public boolean changeWeatherFrequency = true;

  // Time settings
  @RangeConstraint(min = 20, max = Integer.MAX_VALUE)
  public int dayDuration = 24000;
  @RangeConstraint(min = 1, max = Integer.MAX_VALUE)
  public int subSeasonDuration = 8;
  @RangeConstraint(min = 0, max = 12)
  public int startingSubSeason = 1;
  public boolean progressSeasonWhileOffline = true;

  // Aesthetic settings
  public boolean changeGrassColor = true;
  public boolean changeFoliageColor = true;
  public boolean changeBirchColor = true;

  // Dimension settings
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
