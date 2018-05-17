package sereneseasons.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sereneseasons.core.SereneSeasons;

@Config(modid = SereneSeasons.MOD_ID, name = SereneSeasons.MOD_ID+"/cropgrowth", category = "")
@Mod.EventBusSubscriber
public class CropConfig {

	public static General general_category = new General();
	public static SeasonFertility seasonal_fertility = new SeasonFertility();

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event){
		if(event.getModID().equals(SereneSeasons.MOD_ID)){
			ConfigManager.sync(event.getModID(), Config.Type.INSTANCE); //resync config
		}
	}

	public static class General{
		@Config.Comment({"Whether crops break if out of season. If false, they simply don't grow"})
		public boolean crops_break = true;
		@Config.Comment({"Whether unlisted seeds are fertile every season. False means they're infertile every season"})
		public boolean ignore_unlisted_crops = true;
		@Config.Comment({"Whether to include tooltips on crops listing which seasons they're fertile in. " +
				"Note: only applies to listed seeds"})
		public boolean seed_tooltips = true;
	}

	public static class SeasonFertility{
		@Config.Comment({"Crops that do the opposite of what cropsBreak is set to"})
		public String [] crops_break_opposite = new String[]{};
		@Config.Comment({"Crops growable in the spring"})
		public String [] spring_seeds = new String[]{"minecraft:potato"};
		@Config.Comment({"Crops growable in the summer"})
		public String [] summer_seeds = new String[]{"minecraft:potato", "minecraft:carrot"};
		@Config.Comment({"Crops growable in the fall"})
		public String [] fall_seeds = new String[]{"minecraft:potato", "minecraft:wheat_seeds"};
		@Config.Comment({"Crops growable in the winter"})
		public String [] winter_seeds = new String[]{"minecraft:potato"};
	}
}
