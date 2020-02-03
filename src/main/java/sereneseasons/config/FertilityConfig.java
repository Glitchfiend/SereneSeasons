package sereneseasons.config;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.Predicate;

public class FertilityConfig
{
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	// General config options
	public static ForgeConfigSpec.BooleanValue seasonalCrops;
	public static ForgeConfigSpec.BooleanValue ignoreUnlistedCrops;
	public static ForgeConfigSpec.BooleanValue cropTooltips;
	public static ForgeConfigSpec.IntValue outOfSeasonCropBehavior;
	public static ForgeConfigSpec.IntValue greenhouseGlassMaxHeight;
	public static ForgeConfigSpec.IntValue undergroundFertilityLevel;

	// Seasonal fertility congig options
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> springCrops;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> summerCrops;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> autumnCrops;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> winterCrops;

	private static List<String> defaultSpringCrops = Lists.newArrayList(
		"minecraft:oak_sapling", "minecraft:birch_sapling", "minecraft:spruce_sapling", "minecraft:jungle_sapling", "minecraft:acacia_sapling", "minecraft:dark_oak_sapling",
		"minecraft:red_mushroom", "minecraft:brown_mushroom", "minecraft:nether_wart", "minecraft:seagrass", "minecraft:sea_pickle", "minecraft:sweet_berries",
		"minecraft:grass_block", "minecraft:grass", "minecraft:fern", "minecraft:bamboo", "minecraft:bamboo_sapling",
		"minecraft:carrot", "minecraft:potato",

		"biomesoplenty:origin_sapling", "biomesoplenty:flowering_oak_sapling", "biomesoplenty:rainbow_birch_sapling", "biomesoplenty:yellow_autumn_sapling", "biomesoplenty:orange_autumn_sapling", "biomesoplenty:maple_sapling",
		"biomesoplenty:fir_sapling", "biomesoplenty:redwood_sapling", "biomesoplenty:white_cherry_sapling", "biomesoplenty:pink_cherry_sapling", "biomesoplenty:mahogany_sapling",
		"biomesoplenty:jacaranda_sapling", "biomesoplenty:palm_sapling", "biomesoplenty:willow_sapling", "biomesoplenty:dead_sapling", "biomesoplenty:magic_sapling",
		"biomesoplenty:umbran_sapling", "biomesoplenty:hellbark_sapling",

		"xlfoodmod:onion", "xlfoodmod:strawberry_seeds",

		"harvestcraft:caulifloweritem", "harvestcraft:coffeebeanitem", "harvestcraft:garlicitem", "harvestcraft:beanitem", "harvestcraft:rhubarbitem",
		"harvestcraft:strawberryitem", "harvestcraft:oatsitem", "harvestcraft:celeryitem", "harvestcraft:peasitem", "harvestcraft:broccoliitem",
		"harvestcraft:cabbageitem", "harvestcraft:spinachitem", "harvestcraft:zucchiniitem", "harvestcraft:tealeafitem", "harvestcraft:sweetpotatoitem",
		"harvestcraft:turnipitem", "harvestcraft:leekitem", "harvestcraft:brusselsproutitem", "harvestcraft:asparagusitem", "harvestcraft:barleyitem",
		"harvestcraft:onionitem", "harvestcraft:parsnipitem", "harvestcraft:cauliflowerseeditem", "harvestcraft:coffeeseeditem",
		"harvestcraft:garlicseeditem", "harvestcraft:beanseeditem", "harvestcraft:rhubarbseeditem", "harvestcraft:strawberryseeditem",
		"harvestcraft:oatsseeditem", "harvestcraft:celeryseeditem", "harvestcraft:peasseeditem", "harvestcraft:broccoliseeditem",
		"harvestcraft:cabbageseeditem", "harvestcraft:spinachseeditem", "harvestcraft:zucchiniseeditem", "harvestcraft:teaseeditem",
		"harvestcraft:sweetpotatoseeditem", "harvestcraft:turnipseeditem", "harvestcraft:leekseeditem", "harvestcraft:brusselsproutseeditem",
		"harvestcraft:asparagusseeditem", "harvestcraft:barleyseeditem", "harvestcraft:onionseeditem", "harvestcraft:parsnipseeditem",
		"harvestcraft:scallionitem", "harvestcraft:scallionseeditem", "harvestcraft:kaleitem", "harvestcraft:kaleseeditem", "harvestcraft:chickpeaitem",
		"harvestcraft:flaxitem", "harvestcraft:jicamaitem", "harvestcraft:kohlrabiitem", "harvestcraft:lentilitem", "harvestcraft:quinoaitem",
		"harvestcraft:chickpeaseeditem", "harvestcraft:flaxseeditem", "harvestcraft:jicamaseeditem", "harvestcraft:kohlrabiseeditem",
		"harvestcraft:lentilseeditem", "harvestcraft:quinoaseeditem",

		"simplefarming:barley_seeds", "simplefarming:broccoli_seeds", "simplefarming:carrot_seeds", "simplefarming:honeydew_seeds",
		"simplefarming:oat_seeds", "simplefarming:onion_seeds", "simplefarming:pea_seeds", "simplefarming:potato_seeds",
		"simplefarming:quinoa_seeds", "simplefarming:sorghum_seeds", "simplefarming:spinach_seeds", "simplefarming:sweet_potato_seeds",
		"simplefarming:turnip_seeds", "simplefarming:zucchini_seeds", "simplefarming:apricot_sapling", "simplefarming:cherry_sapling",
		"simplefarming:blueberry_bush", "simplefarming:strawberry_bush");

	private static List<String> defaultSummerCrops = Lists.newArrayList(
		"minecraft:oak_sapling", "minecraft:birch_sapling", "minecraft:spruce_sapling", "minecraft:jungle_sapling", "minecraft:acacia_sapling", "minecraft:dark_oak_sapling",
		"minecraft:red_mushroom", "minecraft:brown_mushroom", "minecraft:nether_wart", "minecraft:seagrass", "minecraft:sea_pickle", "minecraft:sweet_berries",
		"minecraft:grass_block", "minecraft:grass", "minecraft:fern", "minecraft:bamboo", "minecraft:bamboo_sapling", "minecraft:cactus", "minecraft:sugar_cane", "minecraft:cocoa",
		"minecraft:wheat_seeds", "minecraft:melon_seeds", "minecraft:cocoa_beans",

		"biomesoplenty:origin_sapling", "biomesoplenty:flowering_oak_sapling", "biomesoplenty:rainbow_birch_sapling", "biomesoplenty:yellow_autumn_sapling", "biomesoplenty:orange_autumn_sapling", "biomesoplenty:maple_sapling",
		"biomesoplenty:fir_sapling", "biomesoplenty:redwood_sapling", "biomesoplenty:white_cherry_sapling", "biomesoplenty:pink_cherry_sapling", "biomesoplenty:mahogany_sapling",
		"biomesoplenty:jacaranda_sapling", "biomesoplenty:palm_sapling", "biomesoplenty:willow_sapling", "biomesoplenty:dead_sapling", "biomesoplenty:magic_sapling",
		"biomesoplenty:umbran_sapling", "biomesoplenty:hellbark_sapling",

		"xlfoodmod:onion", "xlfoodmod:tomato_seeds", "xlfoodmod:lettuce_seeds", "xlfoodmod:cucumber_seeds", "xlfoodmod:pepper_seeds", "xlfoodmod:corn_seeds",
		"xlfoodmod:rice",

		"simplecorn:kernels",

		"harvestcraft:coffeebeanitem", "harvestcraft:beanitem", "harvestcraft:blueberryitem", "harvestcraft:cornitem", "harvestcraft:chilipepperitem",
		"harvestcraft:radishitem", "harvestcraft:tomatoitem", "harvestcraft:grapeitem", "harvestcraft:raspberryitem", "harvestcraft:peasitem",
		"harvestcraft:cottonitem", "harvestcraft:tealeafitem", "harvestcraft:sweetpotatoitem", "harvestcraft:spiceleafitem", "harvestcraft:riceitem",
		"harvestcraft:seaweeditem", "harvestcraft:waterchestnutitem", "harvestcraft:okraitem", "harvestcraft:pineappleitem", "harvestcraft:kiwiitem",
		"harvestcraft:sesameseedsitem", "harvestcraft:curryleafitem", "harvestcraft:bambooshootitem", "harvestcraft:cantaloupeitem",
		"harvestcraft:gingeritem", "harvestcraft:soybeanitem", "harvestcraft:barleyitem", "harvestcraft:cucumberitem", "harvestcraft:mustardseedsitem",
		"harvestcraft:onionitem", "harvestcraft:peanutitem", "harvestcraft:bellpepperitem", "harvestcraft:lettuceitem", "harvestcraft:coffeeseeditem",
		"harvestcraft:beanseeditem", "harvestcraft:blueberryseeditem", "harvestcraft:cornseeditem", "harvestcraft:chilipepperseeditem",
		"harvestcraft:radishseeditem", "harvestcraft:tomatoseeditem", "harvestcraft:grapeseeditem", "harvestcraft:raspberryseeditem",
		"harvestcraft:peasseeditem", "harvestcraft:cottonseeditem", "harvestcraft:teaseeditem", "harvestcraft:sweetpotatoseeditem",
		"harvestcraft:spiceleafseeditem", "harvestcraft:riceseeditem", "harvestcraft:seaweedseeditem", "harvestcraft:waterchestnutseeditem",
		"harvestcraft:okraseeditem", "harvestcraft:pineappleseeditem", "harvestcraft:kiwiseeditem", "harvestcraft:sesameseedsseeditem",
		"harvestcraft:curryleafseeditem", "harvestcraft:bambooshootseeditem", "harvestcraft:cantaloupeseeditem", "harvestcraft:gingerseeditem",
		"harvestcraft:soybeanseeditem", "harvestcraft:barleyseeditem", "harvestcraft:cucumberseeditem", "harvestcraft:mustardseeditem",
		"harvestcraft:onionseeditem", "harvestcraft:peanutseeditem", "harvestcraft:bellpepperseeditem", "harvestcraft:lettuceseeditem",
		"harvestcraft:cactusfruititem", "harvestcraft:cactusfruitseeditem", "harvestcraft:candleberryitem", "harvestcraft:candleberryseeditem",
		"harvestcraft:gigapickleitem", "harvestcraft:gigapickleseeditem", "harvestcraft:agaveitem", "harvestcraft:amaranthitem",
		"harvestcraft:cassavaitem", "harvestcraft:greengrapeitem", "harvestcraft:juteitem", "harvestcraft:kenafitem", "harvestcraft:kohlrabiitem",
		"harvestcraft:milletitem", "harvestcraft:mulberryitem", "harvestcraft:sisalitem", "harvestcraft:taroitem", "harvestcraft:agaveseeditem",
		"harvestcraft:amaranthseeditem", "harvestcraft:cassavaseeditem", "harvestcraft:greengrapeseeditem", "harvestcraft:juteseeditem",
		"harvestcraft:kenafseeditem", "harvestcraft:kohlrabiseeditem", "harvestcraft:milletseeditem", "harvestcraft:mulberryseeditem",
		"harvestcraft:sisalseeditem", "harvestcraft:taroseeditem",

		"simplefarming:barley_seeds", "simplefarming:cantaloupe_seeds", "simplefarming:cassava_seeds", "simplefarming:corn_seeds",
		"simplefarming:cotton_seeds", "simplefarming:cucumber_seeds", "simplefarming:cumin_seeds", "simplefarming:ginger_seeds",
		"simplefarming:grape_seeds", "simplefarming:honeydew_seeds", "simplefarming:kenaf_seeds", "simplefarming:lettuce_seeds",
		"simplefarming:onion_seeds", "simplefarming:pea_seeds", "simplefarming:peanut_seeds", "simplefarming:pepper_seeds",
		"simplefarming:radish_seeds", "simplefarming:rice_seeds", "simplefarming:sorghum_seeds", "simplefarming:soybean_seeds",
		"simplefarming:sunflower_seeds", "simplefarming:sweet_potato_seeds", "simplefarming:tomato_seeds",
		"simplefarming:apple_sapling", "simplefarming:apricot_sapling", "simplefarming:banana_sapling", "simplefarming:cherry_sapling",
		"simplefarming:mango_sapling", "simplefarming:olive_sapling", "simplefarming:orange_sapling", "simplefarming:plum_sapling",
		"simplefarming:blueberry_bush", "simplefarming:raspberry_bush");

	private static List<String> defaultAutumnCrops = Lists.newArrayList(
		"minecraft:oak_sapling", "minecraft:birch_sapling", "minecraft:spruce_sapling", "minecraft:jungle_sapling", "minecraft:acacia_sapling", "minecraft:dark_oak_sapling",
		"minecraft:red_mushroom", "minecraft:brown_mushroom", "minecraft:nether_wart", "minecraft:seagrass", "minecraft:sea_pickle", "minecraft:sweet_berries",
		"minecraft:grass_block", "minecraft:grass", "minecraft:fern",
		"minecraft:wheat_seeds", "minecraft:pumpkin_seeds", "minecraft:beetroot_seeds", "minecraft:carrot",

		"biomesoplenty:origin_sapling", "biomesoplenty:flowering_oak_sapling", "biomesoplenty:rainbow_birch_sapling", "biomesoplenty:yellow_autumn_sapling", "biomesoplenty:orange_autumn_sapling", "biomesoplenty:maple_sapling",
		"biomesoplenty:fir_sapling", "biomesoplenty:redwood_sapling", "biomesoplenty:white_cherry_sapling", "biomesoplenty:pink_cherry_sapling", "biomesoplenty:mahogany_sapling",
		"biomesoplenty:jacaranda_sapling", "biomesoplenty:palm_sapling", "biomesoplenty:willow_sapling", "biomesoplenty:dead_sapling", "biomesoplenty:magic_sapling",
		"biomesoplenty:umbran_sapling", "biomesoplenty:hellbark_sapling",

		"xlfoodmod:onion", "xlfoodmod:corn_seeds",

		"simplecorn:kernels",

		"harvestcraft:cornitem", "harvestcraft:artichokeitem", "harvestcraft:beetitem", "harvestcraft:cranberryitem", "harvestcraft:eggplantitem",
		"harvestcraft:grapeitem", "harvestcraft:whitemushroomitem", "harvestcraft:blackberryitem", "harvestcraft:oatsitem", "harvestcraft:ryeitem",
		"harvestcraft:peasitem", "harvestcraft:spinachitem", "harvestcraft:tealeafitem", "harvestcraft:sweetpotatoitem", "harvestcraft:gingeritem",
		"harvestcraft:wintersquashitem", "harvestcraft:onionitem", "harvestcraft:cornseeditem", "harvestcraft:artichokeseeditem",
		"harvestcraft:beetseeditem", "harvestcraft:cranberryseeditem", "harvestcraft:eggplantseeditem", "harvestcraft:grapeseeditem",
		"harvestcraft:whitemushroomseeditem", "harvestcraft:blackberryseeditem", "harvestcraft:oatsseeditem", "harvestcraft:ryeseeditem",
		"harvestcraft:peasseeditem", "harvestcraft:spinachseeditem", "harvestcraft:teaseeditem", "harvestcraft:sweetpotatoseeditem",
		"harvestcraft:gingerseeditem", "harvestcraft:wintersquashseeditem", "harvestcraft:onionseeditem", "harvestcraft:rutabagaitem",
		"harvestcraft:rutabagaseeditem", "harvestcraft:amaranthitem", "harvestcraft:arrowrootitem", "harvestcraft:elderberryitem",
		"harvestcraft:greengrapeitem", "harvestcraft:huckleberryitem", "harvestcraft:jicamaitem", "harvestcraft:kohlrabiitem",
		"harvestcraft:quinoaitem", "harvestcraft:amaranthseeditem", "harvestcraft:arrowrootseeditem", "harvestcraft:elderberryseeditem",
		"harvestcraft:greengrapeseeditem", "harvestcraft:huckleberryseeditem", "harvestcraft:jicamaseeditem", "harvestcraft:kohlrabiseeditem",
		"harvestcraft:quinoaseeditem",

		"simplefarming:carrot_seeds", "simplefarming:corn_seeds", "simplefarming:eggplant_seeds", "simplefarming:ginger_seeds", "simplefarming:grape_seeds",
		"simplefarming:oat_seeds", "simplefarming:onion_seeds", "simplefarming:pea_seeds", "simplefarming:quinoa_seeds", "simplefarming:rye_seeds",
		"simplefarming:spinach_seeds", "simplefarming:squash_seeds", "simplefarming:sunflower_seeds", "simplefarming:sweet_potato_seeds", "simplefarming:yam_seeds",
		"simplefarming:apple_sapling", "simplefarming:pear_sapling", "simplefarming:plum_sapling", "simplefarming:blackberry_bush");

	private static List<String> defaultWinterCrops = Lists.newArrayList(
		"minecraft:oak_sapling", "minecraft:birch_sapling", "minecraft:spruce_sapling", "minecraft:jungle_sapling", "minecraft:acacia_sapling", "minecraft:dark_oak_sapling",
		"minecraft:red_mushroom", "minecraft:brown_mushroom", "minecraft:nether_wart", "minecraft:seagrass", "minecraft:sea_pickle", "minecraft:sweet_berries",

		"biomesoplenty:origin_sapling", "biomesoplenty:flowering_oak_sapling", "biomesoplenty:rainbow_birch_sapling", "biomesoplenty:yellow_autumn_sapling", "biomesoplenty:orange_autumn_sapling", "biomesoplenty:maple_sapling",
		"biomesoplenty:fir_sapling", "biomesoplenty:redwood_sapling", "biomesoplenty:white_cherry_sapling", "biomesoplenty:pink_cherry_sapling", "biomesoplenty:mahogany_sapling",
		"biomesoplenty:jacaranda_sapling", "biomesoplenty:palm_sapling", "biomesoplenty:willow_sapling", "biomesoplenty:dead_sapling", "biomesoplenty:magic_sapling",
		"biomesoplenty:umbran_sapling", "biomesoplenty:hellbark_sapling");

	private static final Predicate<Object> CROP_VALIDATOR = (obj) -> ResourceLocation.tryParse(obj.toString()) != null;

	static
	{
		BUILDER.push("general");
		seasonalCrops = BUILDER.comment("Whether crops are affected by seasons.").define("seasonal_crops", true);
		ignoreUnlistedCrops = BUILDER.comment("Whether unlisted seeds are fertile every season. False means they're fertile every season except Winter").define("ignore_unlisted_crops", false);
		cropTooltips = BUILDER.comment("Whether to include tooltips on crops listing which seasons they're fertile in. Note: This only applies to listed crops.").define("crop_tooltips", true);
		outOfSeasonCropBehavior = BUILDER.comment("How crops behave when out of season.  0 = Can't grow, 1 = Grow slowly, 2 = Break when trying to grow").defineInRange("out_of_season_crop_behavior", 0, 0, 2);
		greenhouseGlassMaxHeight = BUILDER.comment("Maximum height greenhouse glass can be above a crop for it to be fertile out of season").defineInRange("greenhouse_glass_max_height", 16, 1, Integer.MAX_VALUE);
		undergroundFertilityLevel = BUILDER.comment("Maximum underground Y level out of season crops can be grown below.  Set to -1 to disable feature").defineInRange("underground_fertility_level", 48, -1, Integer.MAX_VALUE);
		BUILDER.pop();

		BUILDER.push("seasonal_fertility");
		springCrops = BUILDER.comment("Crops growable in Spring (List either the seed item for the crop, or the crop block itself)").defineList("spring_crops", defaultSpringCrops, CROP_VALIDATOR);
		summerCrops = BUILDER.comment("Crops growable in Summer (List either the seed item for the crop, or the crop block itself)").defineList("summer_crops", defaultSummerCrops, CROP_VALIDATOR);
		autumnCrops = BUILDER.comment("Crops growable in Autumn (List either the seed item for the crop, or the crop block itself)").defineList("autumn_crops", defaultAutumnCrops, CROP_VALIDATOR);
		winterCrops = BUILDER.comment("Crops growable in Winter (List either the seed item for the crop, or the crop block itself)").defineList("winter_crops", defaultWinterCrops, CROP_VALIDATOR);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}
}
