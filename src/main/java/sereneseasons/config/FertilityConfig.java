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
	public static ForgeConfigSpec.IntValue undergroundFertilityLevel;

	// Seasonal fertility congig options
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> springCrops;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> summerCrops;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> autumnCrops;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> winterCrops;

	private static List<String> defaultSpringCrops = Lists.newArrayList(
"minecraft:oak_sapling",
		"minecraft:birch_sapling",
		"minecraft:spruce_sapling",
		"minecraft:jungle_sapling",
		"minecraft:acacia_sapling",
		"minecraft:dark_oak_sapling",
		"minecraft:red_mushroom",
		"minecraft:brown_mushroom",
		"minecraft:nether_wart",
		"minecraft:seagrass",
		"minecraft:sea_pickle",
		"minecraft:sweet_berries",
		"minecraft:grass_block",
		"minecraft:grass",
		"minecraft:fern",
		"minecraft:bamboo",
		"minecraft:bamboo_sapling",
		"minecraft:carrot",
		"minecraft:potato",

		"biomesoplenty:origin_sapling", "biomesoplenty:flowering_oak_sapling", "biomesoplenty:rainbow_birch_sapling", "biomesoplenty:yellow_autumn_sapling", "biomesoplenty:orange_autumn_sapling", "biomesoplenty:maple_sapling",
		"biomesoplenty:fir_sapling", "biomesoplenty:redwood_sapling", "biomesoplenty:white_cherry_sapling", "biomesoplenty:pink_cherry_sapling", "biomesoplenty:mahogany_sapling",
		"biomesoplenty:jacaranda_sapling", "biomesoplenty:palm_sapling", "biomesoplenty:willow_sapling", "biomesoplenty:dead_sapling", "biomesoplenty:magic_sapling",
		"biomesoplenty:umbran_sapling", "biomesoplenty:hellbark_sapling",

		"pamhc2crops:asparagusitem",
		"pamhc2crops:asparagusseeditem",
		"pamhc2crops:barleyitem",
		"pamhc2crops:barleyseeditem",
		"pamhc2crops:beanitem",
		"pamhc2crops:beanseeditem",
		"pamhc2crops:broccoliitem",
		"pamhc2crops:broccoliseeditem",
		"pamhc2crops:brusselsproutitem",
		"pamhc2crops:brusselsproutseeditem",
		"pamhc2crops:cabbageitem",
		"pamhc2crops:cabbageseeditem",
		"pamhc2crops:caulifloweritem",
		"pamhc2crops:cauliflowerseeditem",
		"pamhc2crops:celeryitem",
		"pamhc2crops:celeryseeditem",
		"pamhc2crops:chickpeaitem",
		"pamhc2crops:chickpeaseeditem",
		"pamhc2crops:coffeebeanitem",
		"pamhc2crops:coffeebeanseeditem",
		"pamhc2crops:flaxitem",
		"pamhc2crops:flaxseeditem",
		"pamhc2crops:garlicitem",
		"pamhc2crops:garlicseeditem",
		"pamhc2crops:jicamaitem",
		"pamhc2crops:jicamaseeditem",
		"pamhc2crops:kaleitem",
		"pamhc2crops:kaleseeditem",
		"pamhc2crops:kohlrabiitem",
		"pamhc2crops:kohlrabiseeditem",
		"pamhc2crops:leekitem",
		"pamhc2crops:leekseeditem",
		"pamhc2crops:lentilitem",
		"pamhc2crops:lentilseeditem",
		"pamhc2crops:oatsitem",
		"pamhc2crops:oatsseeditem",
		"pamhc2crops:onionitem",
		"pamhc2crops:onionseeditem",
		"pamhc2crops:parsnipitem",
		"pamhc2crops:parsnipseeditem",
		"pamhc2crops:peasitem",
		"pamhc2crops:peasseeditem",
		"pamhc2crops:quinoaitem",
		"pamhc2crops:quinoaseeditem",
		"pamhc2crops:rhubarbitem",
		"pamhc2crops:rhubarbseeditem",
		"pamhc2crops:scallionitem",
		"pamhc2crops:scallionseeditem",
		"pamhc2crops:spinachitem",
		"pamhc2crops:spinachseeditem",
		"pamhc2crops:strawberryitem",
		"pamhc2crops:strawberryseeditem",
		"pamhc2crops:sweetpotatoitem",
		"pamhc2crops:sweetpotatoseeditem",
		"pamhc2crops:tealeafitem",
		"pamhc2crops:tealeafseeditem",
		"pamhc2crops:turnipitem",
		"pamhc2crops:turnipseeditem",
		"pamhc2crops:zucchiniitem",
		"pamhc2crops:zucchiniseeditem",

		"simplefarming:barley_seeds", "simplefarming:broccoli_seeds", "simplefarming:carrot_seeds", "simplefarming:honeydew_seeds",
		"simplefarming:oat_seeds", "simplefarming:onion_seeds", "simplefarming:pea_seeds", "simplefarming:potato_seeds",
		"simplefarming:quinoa_seeds", "simplefarming:sorghum_seeds", "simplefarming:spinach_seeds", "simplefarming:sweet_potato_seeds",
		"simplefarming:turnip_seeds", "simplefarming:zucchini_seeds", "simplefarming:apricot_sapling", "simplefarming:cherry_sapling",
		"simplefarming:blueberry_bush", "simplefarming:strawberry_bush",

		"xlfoodmod:onion", "xlfoodmod:strawberry_seeds");

	private static List<String> defaultSummerCrops = Lists.newArrayList(
"minecraft:oak_sapling",
		"minecraft:birch_sapling",
		"minecraft:spruce_sapling",
		"minecraft:jungle_sapling",
		"minecraft:acacia_sapling",
		"minecraft:dark_oak_sapling",
		"minecraft:red_mushroom",
		"minecraft:brown_mushroom",
		"minecraft:nether_wart",
		"minecraft:seagrass",
		"minecraft:sea_pickle",
		"minecraft:sweet_berries",
		"minecraft:grass_block",
		"minecraft:grass",
		"minecraft:fern",
		"minecraft:bamboo",
		"minecraft:bamboo_sapling",
		"minecraft:cactus",
		"minecraft:sugar_cane",
		"minecraft:cocoa",
		"minecraft:wheat_seeds",
		"minecraft:melon_seeds",
		"minecraft:cocoa_beans",

		"biomesoplenty:origin_sapling", "biomesoplenty:flowering_oak_sapling", "biomesoplenty:rainbow_birch_sapling", "biomesoplenty:yellow_autumn_sapling", "biomesoplenty:orange_autumn_sapling", "biomesoplenty:maple_sapling",
		"biomesoplenty:fir_sapling", "biomesoplenty:redwood_sapling", "biomesoplenty:white_cherry_sapling", "biomesoplenty:pink_cherry_sapling", "biomesoplenty:mahogany_sapling",
		"biomesoplenty:jacaranda_sapling", "biomesoplenty:palm_sapling", "biomesoplenty:willow_sapling", "biomesoplenty:dead_sapling", "biomesoplenty:magic_sapling",
		"biomesoplenty:umbran_sapling", "biomesoplenty:hellbark_sapling",

		"pamhc2crops:agaveitem",
		"pamhc2crops:agaveseeditem",
		"pamhc2crops:amaranthitem",
		"pamhc2crops:amaranthseeditem",
		"pamhc2crops:barleyitem",
		"pamhc2crops:barleyseeditem",
		"pamhc2crops:beanitem",
		"pamhc2crops:beanseeditem",
		"pamhc2crops:bellpepperitem",
		"pamhc2crops:bellpepperseeditem",
		"pamhc2crops:blueberryitem",
		"pamhc2crops:blueberryseeditem",
		"pamhc2crops:cactusfruititem",
		"pamhc2crops:cactusfruitseeditem",
		"pamhc2crops:candleberryitem",
		"pamhc2crops:candleberryseeditem",
		"pamhc2crops:cantaloupeitem",
		"pamhc2crops:cantaloupeseeditem",
		"pamhc2crops:cassavaitem",
		"pamhc2crops:cassavaseeditem",
		"pamhc2crops:chilipepperitem",
		"pamhc2crops:chilipepperseeditem",
		"pamhc2crops:coffeebeanitem",
		"pamhc2crops:coffeebeanseeditem",
		"pamhc2crops:cornitem",
		"pamhc2crops:cornseeditem",
		"pamhc2crops:cottonitem",
		"pamhc2crops:cottonseeditem",
		"pamhc2crops:cucumberitem",
		"pamhc2crops:cucumberseeditem",
		"pamhc2crops:gingeritem",
		"pamhc2crops:gingerseeditem",
		"pamhc2crops:grapeitem",
		"pamhc2crops:grapeseeditem",
		"pamhc2crops:greengrapeitem",
		"pamhc2crops:greengrapeseeditem",
		"pamhc2crops:juteitem",
		"pamhc2crops:juteseeditem",
		"pamhc2crops:kenafitem",
		"pamhc2crops:kenafseeditem",
		"pamhc2crops:kiwiitem",
		"pamhc2crops:kiwiseeditem",
		"pamhc2crops:kohlrabiitem",
		"pamhc2crops:kohlrabiseeditem",
		"pamhc2crops:lettuceitem",
		"pamhc2crops:lettuceseeditem",
		"pamhc2crops:milletitem",
		"pamhc2crops:milletseeditem",
		"pamhc2crops:mulberryitem",
		"pamhc2crops:mulberryseeditem",
		"pamhc2crops:mustardseedsitem",
		"pamhc2crops:mustardseedsseeditem",
		"pamhc2crops:okraitem",
		"pamhc2crops:okraseeditem",
		"pamhc2crops:onionitem",
		"pamhc2crops:onionseeditem",
		"pamhc2crops:peanutitem",
		"pamhc2crops:peanutseeditem",
		"pamhc2crops:peasitem",
		"pamhc2crops:peasseeditem",
		"pamhc2crops:pineappleitem",
		"pamhc2crops:pineappleseeditem",
		"pamhc2crops:radishitem",
		"pamhc2crops:radishseeditem",
		"pamhc2crops:raspberryitem",
		"pamhc2crops:raspberryseeditem",
		"pamhc2crops:riceitem",
		"pamhc2crops:riceseeditem",
		"pamhc2crops:sesameseedsitem",
		"pamhc2crops:sesameseedsseeditem",
		"pamhc2crops:sisalitem",
		"pamhc2crops:sisalseeditem",
		"pamhc2crops:soybeanitem",
		"pamhc2crops:soybeanseeditem",
		"pamhc2crops:spiceleafitem",
		"pamhc2crops:spiceleafseeditem",
		"pamhc2crops:sweetpotatoitem",
		"pamhc2crops:sweetpotatoseeditem",
		"pamhc2crops:taroitem",
		"pamhc2crops:taroseeditem",
		"pamhc2crops:tealeafitem",
		"pamhc2crops:tealeafseeditem",
		"pamhc2crops:tomatilloitem",
		"pamhc2crops:tomatilloseeditem",
		"pamhc2crops:tomatoitem",
		"pamhc2crops:tomatoseeditem",
		"pamhc2crops:waterchestnutitem",
		"pamhc2crops:waterchestnutseeditem",

		"simplecorn:kernels",

		"simplefarming:barley_seeds", "simplefarming:cantaloupe_seeds", "simplefarming:cassava_seeds", "simplefarming:corn_seeds",
		"simplefarming:cotton_seeds", "simplefarming:cucumber_seeds", "simplefarming:cumin_seeds", "simplefarming:ginger_seeds",
		"simplefarming:grape_seeds", "simplefarming:honeydew_seeds", "simplefarming:kenaf_seeds", "simplefarming:lettuce_seeds",
		"simplefarming:onion_seeds", "simplefarming:pea_seeds", "simplefarming:peanut_seeds", "simplefarming:pepper_seeds",
		"simplefarming:radish_seeds", "simplefarming:rice_seeds", "simplefarming:sorghum_seeds", "simplefarming:soybean_seeds",
		"simplefarming:sunflower_seeds", "simplefarming:sweet_potato_seeds", "simplefarming:tomato_seeds",
		"simplefarming:apple_sapling", "simplefarming:apricot_sapling", "simplefarming:banana_sapling", "simplefarming:cherry_sapling",
		"simplefarming:mango_sapling", "simplefarming:olive_sapling", "simplefarming:orange_sapling", "simplefarming:plum_sapling",
		"simplefarming:blueberry_bush", "simplefarming:raspberry_bush",

		"xlfoodmod:onion", "xlfoodmod:tomato_seeds", "xlfoodmod:lettuce_seeds", "xlfoodmod:cucumber_seeds", "xlfoodmod:pepper_seeds", "xlfoodmod:corn_seeds",
		"xlfoodmod:rice");

	private static List<String> defaultAutumnCrops = Lists.newArrayList(
		"minecraft:oak_sapling", "minecraft:birch_sapling", "minecraft:spruce_sapling", "minecraft:jungle_sapling", "minecraft:acacia_sapling", "minecraft:dark_oak_sapling",
		"minecraft:red_mushroom", "minecraft:brown_mushroom", "minecraft:nether_wart", "minecraft:seagrass", "minecraft:sea_pickle", "minecraft:sweet_berries",
		"minecraft:grass_block", "minecraft:grass", "minecraft:fern",
		"minecraft:wheat_seeds", "minecraft:pumpkin_seeds", "minecraft:beetroot_seeds", "minecraft:carrot",

		"biomesoplenty:origin_sapling", "biomesoplenty:flowering_oak_sapling", "biomesoplenty:rainbow_birch_sapling", "biomesoplenty:yellow_autumn_sapling", "biomesoplenty:orange_autumn_sapling", "biomesoplenty:maple_sapling",
		"biomesoplenty:fir_sapling", "biomesoplenty:redwood_sapling", "biomesoplenty:white_cherry_sapling", "biomesoplenty:pink_cherry_sapling", "biomesoplenty:mahogany_sapling",
		"biomesoplenty:jacaranda_sapling", "biomesoplenty:palm_sapling", "biomesoplenty:willow_sapling", "biomesoplenty:dead_sapling", "biomesoplenty:magic_sapling",
		"biomesoplenty:umbran_sapling", "biomesoplenty:hellbark_sapling",

		"pamhc2crops:amaranthitem",
		"pamhc2crops:amaranthseeditem",
		"pamhc2crops:arrowrootitem",
		"pamhc2crops:arrowrootseeditem",
		"pamhc2crops:artichokeitem",
		"pamhc2crops:artichokeseeditem",
		"pamhc2crops:blackberryitem",
		"pamhc2crops:blackberryseeditem",
		"pamhc2crops:cornitem",
		"pamhc2crops:cornseeditem",
		"pamhc2crops:cranberryitem",
		"pamhc2crops:cranberryseeditem",
		"pamhc2crops:eggplantitem",
		"pamhc2crops:eggplantseeditem",
		"pamhc2crops:elderberryitem",
		"pamhc2crops:elderberryseeditem",
		"pamhc2crops:gingeritem",
		"pamhc2crops:gingerseeditem",
		"pamhc2crops:grapeitem",
		"pamhc2crops:grapeseeditem",
		"pamhc2crops:greengrapeitem",
		"pamhc2crops:greengrapeseeditem",
		"pamhc2crops:huckleberryitem",
		"pamhc2crops:huckleberryseeditem",
		"pamhc2crops:jicamaitem",
		"pamhc2crops:jicamaseeditem",
		"pamhc2crops:juniperberryitem",
		"pamhc2crops:juniperberryseeditem",
		"pamhc2crops:kohlrabiitem",
		"pamhc2crops:kohlrabiseeditem",
		"pamhc2crops:oatsitem",
		"pamhc2crops:oatsseeditem",
		"pamhc2crops:onionitem",
		"pamhc2crops:onionseeditem",
		"pamhc2crops:peasitem",
		"pamhc2crops:peasseeditem",
		"pamhc2crops:quinoaitem",
		"pamhc2crops:quinoaseeditem",
		"pamhc2crops:rutabagaitem",
		"pamhc2crops:rutabagaseeditem",
		"pamhc2crops:ryeitem",
		"pamhc2crops:ryeseeditem",
		"pamhc2crops:spinachitem",
		"pamhc2crops:spinachseeditem",
		"pamhc2crops:sweetpotatoitem",
		"pamhc2crops:sweetpotatoseeditem",
		"pamhc2crops:tealeafitem",
		"pamhc2crops:tealeafseeditem",
		"pamhc2crops:tomatilloitem",
		"pamhc2crops:tomatilloseeditem",
		"pamhc2crops:whitemushroomitem",
		"pamhc2crops:whitemushroomseeditem",
		"pamhc2crops:wintersquashitem",
		"pamhc2crops:wintersquashseeditem",

		"simplecorn:kernels",

		"simplefarming:carrot_seeds", "simplefarming:corn_seeds", "simplefarming:eggplant_seeds", "simplefarming:ginger_seeds", "simplefarming:grape_seeds",
		"simplefarming:oat_seeds", "simplefarming:onion_seeds", "simplefarming:pea_seeds", "simplefarming:quinoa_seeds", "simplefarming:rye_seeds",
		"simplefarming:spinach_seeds", "simplefarming:squash_seeds", "simplefarming:sunflower_seeds", "simplefarming:sweet_potato_seeds", "simplefarming:yam_seeds",
		"simplefarming:apple_sapling", "simplefarming:pear_sapling", "simplefarming:plum_sapling", "simplefarming:blackberry_bush",

		"xlfoodmod:onion", "xlfoodmod:corn_seeds");

	private static List<String> defaultWinterCrops = Lists.newArrayList(
"minecraft:oak_sapling",
		"minecraft:birch_sapling",
		"minecraft:spruce_sapling",
		"minecraft:jungle_sapling",
		"minecraft:acacia_sapling",
		"minecraft:dark_oak_sapling",
		"minecraft:red_mushroom",
		"minecraft:brown_mushroom",
		"minecraft:nether_wart",
		"minecraft:seagrass",
		"minecraft:sea_pickle",
		"minecraft:sweet_berries",

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
