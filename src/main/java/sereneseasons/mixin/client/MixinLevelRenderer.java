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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sereneseasons.season.SeasonHooks;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
    private Level renderSnowAndRain_level;
    private Holder<Biome> renderSnowAndRain_biome;

    private Level tickRain_level;
    private Holder<Biome> tickRain_biome;

    /*
     * renderSnowAndRain
     */

    @Redirect(method="renderSnowAndRain", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"))
    public Holder<Biome> renderSnowAndRain_getBiome(Level level, BlockPos pos)
    {
        this.renderSnowAndRain_level = level;
        this.renderSnowAndRain_biome = level.getBiome(pos);
        return this.renderSnowAndRain_biome;
    }

    @Redirect(method="renderSnowAndRain", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitation()Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    public Biome.Precipitation renderSnowAndRain_getPrecipitation(Biome biome)
    {
        return SeasonHooks.getLevelRendererPrecipitation(this.renderSnowAndRain_biome);
    }

    @Redirect(method="renderSnowAndRain", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
    public boolean renderSnowAndRain_warmEnoughToRain(Biome biome, BlockPos pos)
    {
        return SeasonHooks.warmEnoughToRainHook(this.renderSnowAndRain_biome, pos, this.renderSnowAndRain_level);
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

    @Redirect(method="tickRain", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitation()Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    public Biome.Precipitation tickRain_getPrecipitation(Biome biome)
    {
        return SeasonHooks.getLevelRendererPrecipitation(this.tickRain_biome);
    }

    @Redirect(method="tickRain", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
    public boolean tickRain_warmEnoughToRain(Biome biome, BlockPos pos)
    {
        return SeasonHooks.warmEnoughToRainHook(this.tickRain_biome, pos, this.tickRain_level);
    }
}
