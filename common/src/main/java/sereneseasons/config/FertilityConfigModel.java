package sereneseasons.config;

import blue.endless.jankson.Comment;
import io.wispforest.owo.config.Option.SyncMode;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.PredicateConstraint;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;
import net.minecraft.world.level.dimension.DimensionType;

@Sync(SyncMode.OVERRIDE_CLIENT)
@Config(name = "SereneSeasons_fertility", wrapperName = "FertilityConfig")
public class FertilityConfigModel {
  // General config options
  @Comment("")
  public boolean seasonalCrops = true;
  public boolean cropTooltips = true;
  @RangeConstraint(min = 0, max = 2)
  public int outOfSeasonCropBehavior = 0;
  @PredicateConstraint("undergroundFertilityLevelValidate")
  public int undergroundFertilityLevel = 48;
  public static boolean undergroundFertilityLevelValidate(int undergroundFertilityLevel) {
    return DimensionType.MIN_Y <= undergroundFertilityLevel;
  }
}
