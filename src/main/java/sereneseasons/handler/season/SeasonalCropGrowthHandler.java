package sereneseasons.handler.season;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sereneseasons.config.FertilityConfig;
import sereneseasons.init.ModFertility;
import sereneseasons.init.ModTags;

@Mod.EventBusSubscriber
public class SeasonalCropGrowthHandler
{
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onItemTooltipAdded(ItemTooltipEvent event)
	{
		ModFertility.setupTooltips(event);
	}

	@SubscribeEvent
	public static void onTagsUpdated(TagsUpdatedEvent event)
	{
		ModFertility.populate();
	}

	@SubscribeEvent
	public static void onCropGrowth(BlockEvent.CropGrowEvent event)
	{
		BlockState plant = event.getState();
		Block plantBlock = plant.getBlock();
		Level level = (Level)event.getLevel();
		Registry<Block> blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);
		boolean isFertile = ModFertility.isCropFertile(blockRegistry.getKey(plantBlock).toString(), level, event.getPos());
		
		if (FertilityConfig.seasonalCrops.get() && !isFertile && !isGlassAboveBlock(level, event.getPos()))
		{
			if (FertilityConfig.outOfSeasonCropBehavior.get() == 0)
			{
				if (level.getRandom().nextInt(6) != 0)
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
                if (!plant.is(ModTags.Blocks.UNBREAKABLE_INFERTILE_CROPS))
                {
					event.setResult(Event.Result.DENY);
                    event.getLevel().destroyBlock(event.getPos(), false);
                }
                else
                {
                    event.setResult(Event.Result.DENY);
                }
            }
		}
	}

	@SubscribeEvent
	public static void onApplyBonemeal(BonemealEvent event)
	{
		BlockState plant = event.getBlock();
		Block plantBlock = plant.getBlock();
		Level level = event.getLevel();
		Registry<Block> blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);
		boolean isFertile = ModFertility.isCropFertile(blockRegistry.getKey(plantBlock).toString(), level, event.getPos());
		
		if (FertilityConfig.seasonalCrops.get() && !isFertile && !isGlassAboveBlock(level, event.getPos()))
		{
			if (FertilityConfig.outOfSeasonCropBehavior.get() == 0)
			{
				if (level.getRandom().nextInt(6) != 0)
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
                if (!plant.is(ModTags.Blocks.UNBREAKABLE_INFERTILE_CROPS))
                {
					event.setCanceled(true);
                    level.destroyBlock(event.getPos(), false);
                }
                else
                {
                    event.setCanceled(true);
                }
            }
		}
	}

	private static boolean isGlassAboveBlock(Level world, BlockPos cropPos)
	{
		for (int i = 0; i < 16; i++)
		{
			if (world.getBlockState(cropPos.offset(0, i + 1, 0)).is(ModTags.Blocks.GREENHOUSE_GLASS))
			{
				return true;
			}
		}
		
		return false;
	}
}
