/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.init.ModConfig;
import sereneseasons.season.SeasonHooks;

@Mixin(Biome.class)
public abstract class MixinBiome
{
    @Shadow public abstract boolean warmEnoughToRain(BlockPos $$0, int $$1);

    @Inject(method="shouldSnow", at=@At("HEAD"), cancellable = true)
    public void onShouldSnow(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (!ModConfig.seasons.generateSnowAndIce && this.warmEnoughToRain(pos, level.getSeaLevel()))
        {
            SeasonHooks.shouldSnowHook = false;
            cir.setReturnValue(false);
        }
        else {
            if (level.isInsideBuildHeight(pos.getY()) && level.getBrightness(LightLayer.BLOCK, pos) < 10) {
                BlockState blockstate = level.getBlockState(pos);
                if (blockstate.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(level, pos)) {
                    SeasonHooks.shouldSnowHook = true;
                    cir.setReturnValue(true);
                }
            } else {
                SeasonHooks.shouldSnowHook = false;
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(
            method = "shouldSnow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
            ), cancellable = true)
    public void onShouldSnowBlock(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if ((ModConfig.seasons.generateSnowAndIce && SeasonHooks.warmEnoughToRainSeasonal(level, pos, level.getSeaLevel()))) {
            SeasonHooks.shouldSnowHook = false;
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at=@At(value = "INVOKE", target = "net/minecraft/world/level/biome/Biome.warmEnoughToRain(Lnet/minecraft/core/BlockPos;I)Z"))
    public boolean onShouldFreeze_warmEnoughToRain(Biome biome, BlockPos pos, int seaLevel, LevelReader level)
    {
        return SeasonHooks.shouldFreezeWarmEnoughToRainHook(biome, pos, seaLevel, level);
    }
}
