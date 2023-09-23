/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sereneseasons.season.SeasonHooks;

@Mixin(ServerLevel.class)
public class MixinServerLevel
{
    @Redirect(method="tickIceAndSnow", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    public Biome.Precipitation tickIceAndSnow_getPrecipitationAt(Biome biome, BlockPos pos)
    {
        return SeasonHooks.getPrecipitationAtTickIceAndSnowHook((LevelReader)this, biome, pos);
    }
}
