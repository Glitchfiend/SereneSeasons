/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.season.SeasonHooks;

@Mixin(Biome.class)
public class MixinBiomeClient
{
    @Inject(method="getPrecipitationAt", at=@At("HEAD"), cancellable = true)
    public void onGetPrecipitationAt(BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;

        if (level != null)
        {
            cir.setReturnValue(SeasonHooks.getPrecipitationAtSeasonal(level, level.getBiome(pos), pos));
        }
    }
}
