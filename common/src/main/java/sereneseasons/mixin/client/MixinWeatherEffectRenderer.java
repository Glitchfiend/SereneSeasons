/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.mixin.client;

import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.core.SereneSeasons;
import sereneseasons.season.SeasonHooks;

@Mixin(WeatherEffectRenderer.class)
public class MixinWeatherEffectRenderer
{
    @Inject(method="getPrecipitationAt", at=@At(value = "HEAD"), cancellable = true, remap = false)
    public void onGetPrecipitationAt(Level level, BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir)
    {
        cir.setReturnValue(SeasonHooks.getPrecipitationAtSeasonal(level, level.getBiome(pos), pos, level.getSeaLevel()));
    }
}
