package sereneseasons.config;

import glitchcore.config.Config;
import glitchcore.util.Environment;
import net.minecraft.world.level.dimension.DimensionType;
import sereneseasons.core.SereneSeasons;

public class FertilityConfig extends Config
{
	// General config options
	public boolean seasonalCrops;
	public boolean cropTooltips;
	public int outOfSeasonCropBehavior;
	public int undergroundFertilityLevel;

	public FertilityConfig()
	{
		super(Environment.getConfigPath().resolve(SereneSeasons.MOD_ID + "/fertility.toml"));
	}

	@Override
	public void load()
	{
		seasonalCrops = add("general.seasonal_crops", true, "Whether crops are affected by seasons.");
		cropTooltips = add("general.crop_tooltips", true, "Whether to include tooltips on crops listing which seasons they're fertile in. Note: This only applies to listed crops.");
		outOfSeasonCropBehavior = addNumber("general.out_of_season_crop_behavior", 0, 0, 2, "How crops behave when out of season.\n0 = Grow slowly\n1 = Can't grow\n2 = Break when trying to grow");
		undergroundFertilityLevel = addNumber("general.underground_fertility_level", 48, DimensionType.MIN_Y, Integer.MAX_VALUE, "Maximum height level for out of season crops to have fertility underground.");
	}
}
