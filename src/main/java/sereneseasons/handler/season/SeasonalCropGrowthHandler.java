package sereneseasons.handler.season;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockReed;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import sereneseasons.api.SSBlocks;
import sereneseasons.config.FertilityConfig;
import sereneseasons.init.ModFertility;
import squeek.applecore.api.plants.PlantGrowthEvent;

public class SeasonalCropGrowthHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onItemTooltipAdded(ItemTooltipEvent event)
	{
		ModFertility.setupTooltips(event);
	}

	@SubscribeEvent
	public void onCropGrowth(PlantGrowthEvent.AllowGrowthTick event)
	{
		Block plant = event.block;
		String name = GameRegistry.findUniqueIdentifierFor(plant).toString();
		boolean isFertile = ModFertility.isCropFertile(name, event.world, event.x, event.y, event.z);
		
		if (FertilityConfig.general_category.seasonal_crops && !isFertile && !isGreenhouseGlassAboveBlock(event.world, event.x, event.y, event.z))
		{
			if (FertilityConfig.general_category.crops_break && !(plant instanceof BlockGrass) && !(plant instanceof BlockReed))
			{
				event.world.func_147480_a(event.x, event.y, event.z, true);
			}
			else
			{
				event.setResult(Event.Result.DENY);
			}
		}
	}

	@SubscribeEvent
	public void onApplyBonemeal(BonemealEvent event)
	{
		Block plant = event.block;
		String plantName = GameRegistry.findUniqueIdentifierFor(plant).toString();
		boolean isFertile = ModFertility.isCropFertile(plantName, event.world, event.x, event.y, event.z);
		
		if (FertilityConfig.general_category.seasonal_crops && !isFertile && !isGreenhouseGlassAboveBlock(event.world, event.x, event.y, event.z))
		{
			if (FertilityConfig.general_category.crops_break && !(plant instanceof BlockGrass) && !(plant instanceof BlockReed))
			{
				event.world.func_147480_a(event.x, event.y, event.z, true);
			}
			
			event.setCanceled(true);
		}
	}

	private boolean isGreenhouseGlassAboveBlock(World world, int x, int y, int z)
	{
		for (int i = 0; i < FertilityConfig.general_category.greenhouse_glass_max_height; i++)
		{
			if (world.getBlock(x,  y + i + 1,  z).equals(SSBlocks.greenhouse_glass))
			{
				return true;
			}
		}
		
		return false;
	}
}
