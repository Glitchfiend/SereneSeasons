/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.mixin.client;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sereneseasons.season.SeasonHooks;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
    @Unique
    private Level renderSnowAndRain_level;
    @Unique
    private Holder<Biome> renderSnowAndRain_biome;
    @Unique
    private BlockPos renderSnowAndRain_pos;

    @Unique
    private Level tickRain_level;
    @Unique
    private Holder<Biome> tickRain_biome;

    /*
     * renderSnowAndRain
     */

    @Redirect(method="renderSnowAndRain", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"))
    public Holder<Biome> renderSnowAndRain_getBiome(Level level, BlockPos pos)
    {
        this.renderSnowAndRain_level = level;
        this.renderSnowAndRain_biome = level.getBiome(pos);
        this.renderSnowAndRain_pos = pos;
        return this.renderSnowAndRain_biome;
    }

    @Redirect(method="renderSnowAndRain", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    public Biome.Precipitation renderSnowAndRain_getPrecipitationAt(Biome biome, BlockPos pos)
    {
        return SeasonHooks.getPrecipitationAtLevelRendererHook(this.renderSnowAndRain_biome, pos);
    }

    @Redirect(method="renderSnowAndRain", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;hasPrecipitation()Z"))
    public boolean renderSnowAndRain_hasPrecipitation(Biome biome)
    {
        return SeasonHooks.hasPrecipitationSeasonal(this.renderSnowAndRain_level, this.renderSnowAndRain_biome);
    }

    /*
     * tickRain
     */

    @Redirect(method="tickRain", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"))
    public Holder<Biome> tickRain_getBiome(LevelReader level, BlockPos pos)
    {
        this.tickRain_level = (Level)level;
        this.tickRain_biome = level.getBiome(pos);
        return this.tickRain_biome;
    }

    @Redirect(method="tickRain", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    public Biome.Precipitation tickRain_getPrecipitationAt(Biome biome, BlockPos pos)
    {
        return SeasonHooks.getPrecipitationAtLevelRendererHook(this.tickRain_biome, pos);
    }
}
