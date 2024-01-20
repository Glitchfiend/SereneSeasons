/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sereneseasons.season.SeasonalCropGrowthHandler;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class MixinBlockStateBase
{
    @Inject(method="randomTick", at=@At("HEAD"), cancellable = true)
    public void onRandomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci)
    {
        if ((Object)this instanceof BlockState)
        {
            SeasonalCropGrowthHandler.onCropGrowth(level, pos, (BlockState)(Object)this, ci);
        }
    }
}
