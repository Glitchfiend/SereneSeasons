package sereneseasons.config;

public class FertilityConfig
{
	public static General general_category = new General();
	public static SeasonFertility seasonal_fertility = new SeasonFertility();

	public static class General
	{
		public boolean seasonal_crops = true;
		
		public boolean crops_break = false;
		
		public boolean ignore_unlisted_crops = false;
		
		public boolean crop_tooltips = true;
		
		public int greenhouse_glass_max_height = 7;
	}

	public static class SeasonFertility
	{
		public String [] spring_crops = new String[]
		{
			"minecraft:potato", "minecraft:carrot", "minecraft:sapling", "minecraft:nether_wart", "minecraft:tallgrass", "minecraft:grass",
			"minecraft:red_mushroom", "minecraft:brown_mushroom",
			
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
			
			"growthcraft_rice:rice", "growthcraft_rice:riceCrop"
		};
		
		public String [] summer_crops = new String[]
		{
			"minecraft:melon_seeds", "minecraft:wheat_seeds", "minecraft:reeds", "minecraft:cocoa", "minecraft:cactus", "minecraft:sapling",
			"minecraft:nether_wart", "minecraft:tallgrass", "minecraft:grass", "minecraft:red_mushroom", "minecraft:brown_mushroom",
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
			
			"growthcraft_apples:apple_crop", "growthcraft_apples:apple_sapling", "growthcraft_apples:apple_leaves", "growthcraft_hops:hop_seeds",
			"growthcraft_hops:hops", "growthcraft_milk:thistle_seed", "growthcraft_milk:thistle", "growthcraft_rice:rice", "growthcraft_rice:riceCrop"
		};
		
		public String [] autumn_crops = new String[]
		{
			"minecraft:carrot", "minecraft:pumpkin_seeds", "minecraft:wheat_seeds", "minecraft:beetroot_seeds", "minecraft:sapling",
			"minecraft:nether_wart", "minecraft:grass", "minecraft:red_mushroom", "minecraft:brown_mushroom",
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
			
			"growthcraft_apples:apple_crop", "growthcraft_apples:apple_sapling", "growthcraft_apples:apple_leaves", "growthcraft_grapes:grape_seed",
			"growthcraft_grapes:native_grape_vine0", "growthcraft_grapes:native_grape_vine1", "growthcraft_grapes:native_grape_vine_leaves",
			"growthcraft_hops:hop_seeds", "growthcraft_hops:hops", "growthcraft_milk:thistle_seed", "growthcraft_milk:thistle", "growthcraft_rice:rice",
			"growthcraft_rice:riceCrop"
		};
		
		public String [] winter_crops = new String[]
		{
	        "minecraft:sapling", "minecraft:nether_wart", "minecraft:red_mushroom", "minecraft:brown_mushroom"
		};
	}
}
