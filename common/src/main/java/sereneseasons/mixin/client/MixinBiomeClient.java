/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.season.SeasonHooks;

import java.util.Optional;

@Mixin(Biome.class)
public class MixinBiomeClient
{
    @Inject(method="hasPrecipitation", at=@At("HEAD"), cancellable = true, remap = false)
    public void onHasPrecipitation(CallbackInfoReturnable<Boolean> cir)
    {
        @Nullable Level level = Minecraft.getInstance().level;

        if (level == null)
            return;

        Holder<Biome> holder = level.registryAccess()
                .lookupOrThrow(Registries.BIOME)
                .wrapAsHolder((Biome) (Object) this);

        Optional<Boolean> override = SeasonHooks.precipitationOverrideSeasonal(level, holder);
        override.ifPresent(cir::setReturnValue);
    }

    @Inject(method="getPrecipitationAt", at=@At("HEAD"), cancellable = true, remap = false)
    public void onGetPrecipitationAt(BlockPos pos, int seaLevel, CallbackInfoReturnable<Biome.Precipitation> cir)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;

        if (level != null)
        {
            cir.setReturnValue(SeasonHooks.getPrecipitationAtSeasonal(level, level.getBiome(pos), pos, seaLevel));
        }
    }
}
