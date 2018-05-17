package sereneseasons.handler.season;

import net.minecraft.block.*;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sereneseasons.config.FertilityConfig;
import sereneseasons.init.ModFertility;

public class SeasonalCropGrowthHandler {

	@SubscribeEvent
	public void onCropGrowth(BlockEvent.CropGrowEvent event){
		Block plant = event.getState().getBlock();
		boolean isFertile = ModFertility.isCropFertile(plant.getRegistryName().toString(), event.getWorld());
		if(!isFertile && isFertilityApplicable(plant)){
			if(ModFertility.shouldBreakCrop(plant.getRegistryName().toString()))
				event.getWorld().destroyBlock(event.getPos(), true);
			else
				event.setResult(Event.Result.DENY);
		}
	}

	@SubscribeEvent
	public void onBonemeal(BonemealEvent event){
		Block plant = event.getBlock().getBlock();
		boolean isFertile = ModFertility.isCropFertile(plant.getRegistryName().toString(), event.getWorld());
		if(!isFertile && isFertilityApplicable(plant)){
			if(ModFertility.shouldBreakCrop(plant.getRegistryName().toString()))
				event.getWorld().destroyBlock(event.getPos(), true);
			event.setCanceled(true);
		}
	}

	private boolean isFertilityApplicable(Block block){
		if(!(block instanceof IGrowable))
			return false;
		if(((block instanceof BlockTallGrass || block instanceof BlockDoublePlant || block instanceof BlockGrass)
				&& FertilityConfig.general_category.ignore_tall_grass) ||
				(block instanceof BlockSapling && FertilityConfig.general_category.ignore_saplings))
			return false;
		else
			return true;
	}
}
