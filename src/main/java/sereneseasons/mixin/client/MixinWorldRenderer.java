/*******************************************************************************
 * Copyright 2014-2020, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonHooks;
import sereneseasons.util.biome.BiomeUtil;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IResourceManagerReloadListener, AutoCloseable
{
    @Redirect(method={"renderSnowAndRain", "renderRainSnow"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    public Biome.RainType renderSnowAndRain_getPrecipitation(Biome biome)
    {
        return getSeasonalPrecipitation(biome);
    }

    @Redirect(method={"tickRain", "addRainParticles"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    public Biome.RainType tickRain_getPrecipitation(Biome biome)
    {
        return getSeasonalPrecipitation(biome);
    }

    private static Biome.RainType getSeasonalPrecipitation(Biome biome)
    {
        RegistryKey<Biome> biomeKey = BiomeUtil.getBiomeKey(biome);
        Biome.RainType rainType = biome.getPrecipitation();
        World world = Minecraft.getInstance().level;

        if (SeasonsConfig.isDimensionWhitelisted(world.dimension()) && BiomeConfig.enablesSeasonalEffects(biomeKey) && (rainType == Biome.RainType.RAIN || rainType == Biome.RainType.NONE))
        {
            if (SeasonHooks.shouldRainInBiomeInSeason(world, biomeKey))
                return Biome.RainType.RAIN;
            else
                return Biome.RainType.NONE;
        }

        return rainType;
    }
}
