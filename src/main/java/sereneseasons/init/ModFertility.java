package sereneseasons.init;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.util.control.Exception;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.FertilityConfig;
import sereneseasons.handler.season.SeasonHandler;

import java.util.*;

/**
 * Constructs efficient data structures to process, store, and give access to data from the FertilityConfig file
 */
public class ModFertility
{

	private static HashSet<String> springPlants = new HashSet<String>();
	private static HashSet<String> summerPlants = new HashSet<String>();
	private static HashSet<String> fallPlants = new HashSet<String>();
	private static HashSet<String> winterPlants = new HashSet<String>();
	private static HashSet<String> allListedPlants = new HashSet<String>();
	private static HashSet<String> oppositeBreak = new HashSet<String>();

	//Maps seed name to all fertile seasons via byte
	private static HashMap<String, Integer> seedSeasons= new HashMap<String, Integer>();

	public static void init()
	{
		//Store crops in hash sets for quick and easy retrieval
		initSeasonCrops(FertilityConfig.seasonal_fertility.spring_seeds, springPlants, 1);
		initSeasonCrops(FertilityConfig.seasonal_fertility.summer_seeds, summerPlants, 2);
		initSeasonCrops(FertilityConfig.seasonal_fertility.fall_seeds, fallPlants, 4);
		initSeasonCrops(FertilityConfig.seasonal_fertility.winter_seeds, winterPlants, 8);

		initSeasonCrops(FertilityConfig.seasonal_fertility.crops_break_opposite, oppositeBreak, 0);
	}

	public static boolean isCropFertile(String cropName, World world)
	{
		//Get season
		String sname = SeasonHelper.getSeasonState(world).getSeason().name();
		//Check if crop's fertility is specified
		if(sname.equals("SPRING") && springPlants.contains(cropName))
			return true;
		else if(sname.equals("SUMMER") && summerPlants.contains(cropName))
			return true;
		else if(sname.equals("AUTUMN") && fallPlants.contains(cropName))
			return true;
		else if(sname.equals("WINTER") && winterPlants.contains(cropName))
			return true;

		//Check if unspecified crops are by default fertile in non-winter, and that it's not winter
		if(!allListedPlants.contains(cropName) && FertilityConfig.general_category.ignore_unlisted_crops && !sname.equals("WINTER"))
			return true;

		return false;
	}

	public static boolean shouldBreakCrop(String cropName)
	{
		boolean flag = false;
		if(oppositeBreak.contains(cropName))
			flag = true;
		//Returns opposite of config if oppositeBreak is true
		return flag ^ FertilityConfig.general_category.crops_break;
	}

	/**
	 * Initializes the crops for a particular season. User's responsibility to match seeds and cropSet to be of the
	 * same season (eg. String [] spring_seeds, HashSet springPlants)
	 * @param seeds String array of seeds that are fertile during the chosen season
	 * @param cropSet HashSet that will store the list of crops fertile during the chosen season
	 */
	private static void initSeasonCrops(String [] seeds, HashSet<String> cropSet, int bitmask)
	{
		for(String seed : seeds)
		{
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(seed));
			if(item instanceof IPlantable)
			{
				String plantName = ((IPlantable) item).getPlant(null, null).getBlock().getRegistryName().toString();
				cropSet.add(plantName);
				if(bitmask != 0)
					allListedPlants.add(plantName);
				else
					continue;

				//Add to seedSeasons
				if(seedSeasons.containsKey(seed))
				{
					int seasons = seedSeasons.get(seed);
					seedSeasons.put(seed, seasons | bitmask);
				}
				else
				{
					seedSeasons.put(seed, bitmask);
				}
			}
		}
	}

	public static void setupTooltips(ItemTooltipEvent event)
	{
		//Set up tooltips if enabled and on client side
		if(FertilityConfig.general_category.seed_tooltips)
		{
			String name = event.getItemStack().getItem().getRegistryName().toString();
			if(seedSeasons.containsKey(name))
			{
				event.getToolTip().addAll(getFormattedSeasonNames(seedSeasons.get(name)));
			}
		}
	}

	/**
	 * Returns a list of formatted strings to add to a seed based on the parameter bitmask
	 * @param mask Bitmask containing all selected seasons.
	 * @return A list containing tooltips to add
	 */
	private static List<String> getFormattedSeasonNames(int mask)
	{
		LinkedList<String> toret = new LinkedList<String>();
		String toadd = "";
		if((mask & 1) != 0)
			toadd += TextFormatting.GREEN + "Sp ";
			//toret.add(TextFormatting.GREEN + "Spring");
		if((mask & 2) != 0)
			toadd += TextFormatting.YELLOW + "Su ";
			//toret.add(TextFormatting.YELLOW + "Summer");
		if((mask & 4) != 0)
			toadd += TextFormatting.GOLD + "Fa ";
			//toret.add(TextFormatting.GOLD + "Fall");
		if((mask & 8) != 0)
			toadd += TextFormatting.BLUE + "Wi";
			//toret.add(TextFormatting.BLUE + "Winter");

		toret.add(toadd);
		return toret;
	}

}
