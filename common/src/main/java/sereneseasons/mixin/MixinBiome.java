/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.season.SeasonHooks;

@Mixin(Biome.class)
public class MixinBiome
{
    @Inject(method="shouldSnow", at=@At("HEAD"), cancellable = true)
    public void onShouldSnow(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(SeasonHooks.shouldSnowHook((Biome)(Object)this, level, pos));
    }

    @Redirect(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at=@At(value = "INVOKE", target = "net/minecraft/world/level/biome/Biome.warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
    public boolean onShouldFreeze_warmEnoughToRain(Biome biome, BlockPos pos, LevelReader level)
    {
        return SeasonHooks.shouldFreezeWarmEnoughToRainHook(biome, pos, level);
    }
}
