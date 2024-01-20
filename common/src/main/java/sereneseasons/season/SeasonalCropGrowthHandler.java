package sereneseasons.season;

import glitchcore.event.TagsUpdatedEvent;
import glitchcore.event.player.PlayerInteractEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModFertility;
import sereneseasons.init.ModTags;

public class SeasonalCropGrowthHandler
{
	public static void onTagsUpdated(TagsUpdatedEvent event)
	{
		ModFertility.populate();
	}

	public static void onCropGrowth(Level level, BlockPos pos, BlockState state, CallbackInfo ci)
	{
		if (!ModConfig.fertility.seasonalCrops || !ModFertility.isCrop(state))
			return;

		Registry<Block> blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);
		boolean isFertile = ModFertility.isCropFertile(blockRegistry.getKey(state.getBlock()).toString(), level, pos);

		if (!isFertile && !isGlassAboveBlock(level, pos))
		{
			if (ModConfig.fertility.outOfSeasonCropBehavior == 0)
			{
				if (level.getRandom().nextInt(6) != 0)
				{
					ci.cancel();
				}
			}
		    else if (ModConfig.fertility.outOfSeasonCropBehavior == 1)
            {
                ci.cancel();
            }
		    else if (ModConfig.fertility.outOfSeasonCropBehavior == 2)
            {
                if (!state.is(ModTags.Blocks.UNBREAKABLE_INFERTILE_CROPS))
                {
                    level.destroyBlock(pos, false);
                }

				ci.cancel();
            }
		}
	}

	public static void applyBonemeal(PlayerInteractEvent.UseBlock event)
	{
		ItemStack stack = event.getItemStack();

		if (stack.getItem() != Items.BONE_MEAL)
			return;

		Player player = event.getPlayer();
		InteractionHand hand = event.getHand();
		Level level = player.level();
		BlockHitResult hitResult = event.getHitResult();
		BlockPos pos = hitResult.getBlockPos();
		BlockState plant = level.getBlockState(pos);
		Block plantBlock = plant.getBlock();
		Registry<Block> blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);

		if (!ModConfig.fertility.seasonalCrops || !ModFertility.isCrop(plant))
			return;

		boolean isFertile = ModFertility.isCropFertile(blockRegistry.getKey(plantBlock).toString(), level, pos);

		if (!isFertile && !isGlassAboveBlock(level, pos))
		{
			if (ModConfig.fertility.outOfSeasonCropBehavior == 0)
			{
				if (level.getRandom().nextInt(6) != 0)
				{
					event.setCancelled(true);
					event.setCancelResult(InteractionResultHolder.success(stack));
				}
			}
            else if (ModConfig.fertility.outOfSeasonCropBehavior == 1)
            {
				event.setCancelled(true);
				event.setCancelResult(InteractionResultHolder.fail(stack));
            }
            else if (ModConfig.fertility.outOfSeasonCropBehavior == 2)
            {
                if (!plant.is(ModTags.Blocks.UNBREAKABLE_INFERTILE_CROPS))
                {
                    level.destroyBlock(pos, false);
					event.setCancelled(true);
					event.setCancelResult(InteractionResultHolder.success(stack));
                }
                else
                {
                    event.setCancelled(true);
                }
            }
		}

		if (event.isCancelled() && !level.isClientSide())
		{
			if (!player.isCreative())
				stack.shrink(1);

			if (stack.isEmpty())
				player.setItemInHand(hand, ItemStack.EMPTY);
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
