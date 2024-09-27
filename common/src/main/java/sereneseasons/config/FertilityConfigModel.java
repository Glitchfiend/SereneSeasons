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
  @Comment("Whether crops are affected by seasons.")
  public boolean seasonalCrops = true;
  @Comment("Whether to include tooltips on crops listing which seasons they're fertile in. Note: This only applies to listed crops.")
  public boolean cropTooltips = true;
  @Comment("How crops behave when out of season.\n0 = Grow slowly\n1 = Can't grow\n2 = Break when trying to grow")
  @RangeConstraint(min = 0, max = 2)
  public int outOfSeasonCropBehavior = 0;
  @Comment("Maximum height level for out of season crops to have fertility underground.")
  @PredicateConstraint("undergroundFertilityLevelValidate")
  public int undergroundFertilityLevel = 48;
  public static boolean undergroundFertilityLevelValidate(int undergroundFertilityLevel) {
    return DimensionType.MIN_Y <= undergroundFertilityLevel;
  }
}
