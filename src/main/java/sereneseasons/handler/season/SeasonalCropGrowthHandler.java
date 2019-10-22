package sereneseasons.handler.season;

import javafx.geometry.Side;
import net.minecraft.block.Block;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sereneseasons.api.SSBlocks;
import sereneseasons.config.FertilityConfig;
import sereneseasons.init.ModFertility;

@Mod.EventBusSubscriber
public class SeasonalCropGrowthHandler
{
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onItemTooltipAdded(ItemTooltipEvent event)
	{
		ModFertility.setupTooltips(event);
	}

	@SubscribeEvent
	public void onCropGrowth(BlockEvent.CropGrowEvent event)
	{
		Block plant = event.getState().getBlock();
		World world = (World)event.getWorld();
		boolean isFertile = ModFertility.isCropFertile(plant.getRegistryName().toString(), world, event.getPos());
		
		if (FertilityConfig.general_category.seasonal_crops && !isFertile && !isGreenhouseGlassAboveBlock(world, event.getPos()))
		{
			if (FertilityConfig.general_category.crops_break && !(plant instanceof GrassBlock) && !(plant instanceof SugarCaneBlock))
			{
				event.getWorld().destroyBlock(event.getPos(), true);
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
		Block plant = event.getBlock().getBlock();
		boolean isFertile = ModFertility.isCropFertile(plant.getRegistryName().toString(), event.getWorld(), event.getPos());
		
		if (FertilityConfig.general_category.seasonal_crops && !isFertile && !isGreenhouseGlassAboveBlock(event.getWorld(), event.getPos()))
		{
			if (FertilityConfig.general_category.crops_break && !(plant instanceof GrassBlock) && !(plant instanceof SugarCaneBlock))
			{
				event.getWorld().destroyBlock(event.getPos(), true);
			}
			
			event.setCanceled(true);
		}
	}

	private boolean isGreenhouseGlassAboveBlock(World world, BlockPos cropPos)
	{
		for (int i = 0; i < FertilityConfig.general_category.greenhouse_glass_max_height; i++)
		{
			if (world.getBlockState(cropPos.add(0, i + 1, 0)).getBlock().equals(SSBlocks.greenhouse_glass))
			{
				return true;
			}
		}
		
		return false;
	}
}
