package sereneseasons.handler.season;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sereneseasons.config.FertilityConfig;
import sereneseasons.init.ModFertility;
import sereneseasons.init.ModTags;

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
		BlockState plant = event.getState();
		Block plantBlock = plant.getBlock();
		Level world = (Level)event.getWorld();
		boolean isFertile = ModFertility.isCropFertile(plantBlock.getRegistryName().toString(), world, event.getPos());
		
		if (FertilityConfig.seasonalCrops.get() && !isFertile && !isGlassAboveBlock(world, event.getPos()))
		{
			if (FertilityConfig.outOfSeasonCropBehavior.get() == 0)
			{
				if (world.getRandom().nextInt(6) != 0)
				{
					event.setResult(Event.Result.DENY);
				}
			}
		    if (FertilityConfig.outOfSeasonCropBehavior.get() == 1)
            {
                event.setResult(Event.Result.DENY);
            }
		    if (FertilityConfig.outOfSeasonCropBehavior.get() == 2)
            {
                if (!plant.is(ModTags.Blocks.unbreakable_infertile_crops))
                {
					event.setResult(Event.Result.DENY);
                    event.getWorld().destroyBlock(event.getPos(), false);
                }
                else
                {
                    event.setResult(Event.Result.DENY);
                }
            }
		}
	}

	@SubscribeEvent
	public void onApplyBonemeal(BonemealEvent event)
	{
		BlockState plant = event.getBlock();
		Block plantBlock = plant.getBlock();
		boolean isFertile = ModFertility.isCropFertile(plantBlock.getRegistryName().toString(), event.getWorld(), event.getPos());
		
		if (FertilityConfig.seasonalCrops.get() && !isFertile && !isGlassAboveBlock(event.getWorld(), event.getPos()))
		{
			if (FertilityConfig.outOfSeasonCropBehavior.get() == 0)
			{
				if (event.getWorld().getRandom().nextInt(6) != 0)
				{
					event.setResult(Event.Result.DEFAULT);
				}
			}
            if (FertilityConfig.outOfSeasonCropBehavior.get() == 1)
            {
                event.setCanceled(true);
            }
            if (FertilityConfig.outOfSeasonCropBehavior.get() == 2)
            {
                if (!plant.is(ModTags.Blocks.unbreakable_infertile_crops))
                {
					event.setCanceled(true);
                    event.getWorld().destroyBlock(event.getPos(), false);
                }
                else
                {
                    event.setCanceled(true);
                }
            }
		}
	}

	private boolean isGlassAboveBlock(Level world, BlockPos cropPos)
	{
		for (int i = 0; i < 16; i++)
		{
			if (world.getBlockState(cropPos.offset(0, i + 1, 0)).is(ModTags.Blocks.greenhouse_glass))
			{
				return true;
			}
		}
		
		return false;
	}
}
