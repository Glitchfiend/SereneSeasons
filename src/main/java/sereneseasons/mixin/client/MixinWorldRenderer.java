/*******************************************************************************
 * Copyright 2014-2020, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonHooks;
import sereneseasons.util.biome.BiomeUtil;

@Mixin(LevelRenderer.class)
public abstract class MixinWorldRenderer implements ResourceManagerReloadListener, AutoCloseable
{
    @Redirect(method={"renderSnowAndRain", "renderRainSnow"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    public Biome.Precipitation renderSnowAndRain_getPrecipitation(Biome biome)
    {
        return getSeasonalPrecipitation(biome);
    }

    @Redirect(method={"tickRain", "addRainParticles"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    public Biome.Precipitation tickRain_getPrecipitation(Biome biome)
    {
        return getSeasonalPrecipitation(biome);
    }

    private static Biome.Precipitation getSeasonalPrecipitation(Biome biome)
    {
        ResourceKey<Biome> biomeKey = BiomeUtil.getBiomeKey(biome);
        Biome.Precipitation rainType = biome.getPrecipitation();
        Level world = Minecraft.getInstance().level;

        if (SeasonsConfig.isDimensionWhitelisted(world.dimension()) && BiomeConfig.enablesSeasonalEffects(biomeKey) && (rainType == Biome.Precipitation.RAIN || rainType == Biome.Precipitation.NONE))
        {
            if (SeasonHooks.shouldRainInBiomeInSeason(world, biomeKey))
                return Biome.Precipitation.RAIN;
            else
                return Biome.Precipitation.NONE;
        }

        return rainType;
    }
}
