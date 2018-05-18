package sereneseasons.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sereneseasons.core.SereneSeasons;

@Config(modid = SereneSeasons.MOD_ID, name = SereneSeasons.MOD_ID+"/cropfertility", category = "")
@Mod.EventBusSubscriber
public class FertilityConfig
{
	public static General general_category = new General();
	public static SeasonFertility seasonal_fertility = new SeasonFertility();

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(SereneSeasons.MOD_ID))
		{
			ConfigManager.sync(event.getModID(), Config.Type.INSTANCE); //resync config
		}
	}

	public static class General
	{
		@Config.Comment({"Whether crops break if out of season. If false, they simply don't grow"})
		public boolean crops_break = false;
		
		@Config.Comment({"Whether unlisted seeds are fertile every season. False means they're fertile every season except Winter"})
		public boolean ignore_unlisted_crops = false;
		
		@Config.Comment({"Whether to include tooltips on crops listing which seasons they're fertile in. Note: This only applies to listed seeds."})
		public boolean seed_tooltips = true;
		
		@Config.Comment({"Maximum height greenhouse glass can be above a crop for it to be fertile out of season"})
		public int greenhouse_glass_max_height = 7;
	}

	public static class SeasonFertility
	{
		@Config.Comment({"Seeds growable in Spring"})
		public String [] spring_seeds = new String[]
		{
			"minecraft:potato", "minecraft:carrot"
		};
		
		@Config.Comment({"Seeds growable in Summer"})
		public String [] summer_seeds = new String[]
		{
			"minecraft:melon_seeds", "minecraft:wheat_seeds",
			"simplecorn:kernels"
		};
		
		@Config.Comment({"Seeds growable in Autumn"})
		public String [] autumn_seeds = new String[]
		{
			"minecraft:carrot", "minecraft:pumpkin_seeds", "minecraft:wheat_seeds", "minecraft:beetroot_seeds",
			"simplecorn:kernels"
		};
		
		@Config.Comment({"Seeds growable in Winter"})
		public String [] winter_seeds = new String[]
		{
			"minecraft:potato"
		};
	}
}
