package sereneseasons.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class FertilityConfig
{
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	// General config options
	public static ForgeConfigSpec.BooleanValue seasonalCrops;
	public static ForgeConfigSpec.BooleanValue cropTooltips;
	public static ForgeConfigSpec.IntValue outOfSeasonCropBehavior;
	public static ForgeConfigSpec.IntValue undergroundFertilityLevel;

	static
	{
		BUILDER.push("general");
		seasonalCrops = BUILDER.comment("Whether crops are affected by seasons.").define("seasonal_crops", true);
		cropTooltips = BUILDER.comment("Whether to include tooltips on crops listing which seasons they're fertile in. Note: This only applies to listed crops.").define("crop_tooltips", true);
		outOfSeasonCropBehavior = BUILDER.comment("How crops behave when out of season.\n0 = Grow slowly\n1 = Can't grow\n2 = Break when trying to grow").defineInRange("out_of_season_crop_behavior", 0, 0, 2);
		undergroundFertilityLevel = BUILDER.comment("Maximum underground Y level out of season crops can be grown below.  Set to -1 to disable feature").defineInRange("underground_fertility_level", 48, -1, Integer.MAX_VALUE);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}
}
