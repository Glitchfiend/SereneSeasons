/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.season;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.core.SereneSeasons;
import sereneseasons.util.biome.BiomeUtil;

public class SeasonHooks
{
    //
    // Hooks called by ASM
    //

    public static float getBiomeTemperatureHook(Biome biome, BlockPos pos, IWorldReader worldReader)
    {
        if (!(worldReader instanceof World))
        {
            return biome.getTemperature(pos);
        }

        return getBiomeTemperature((World)worldReader, biome, pos);
    }

    //
    // General utilities
    //

    public static float getBiomeTemperature(World world, Biome biome, BlockPos pos)
    {
        return getBiomeTemperature(world, biome, world.getBiomeName(pos).orElse(null), pos);
    }

    public static float getBiomeTemperature(World world, RegistryKey<Biome> key, BlockPos pos)
    {
        return getBiomeTemperature(world, world.getBiome(pos), key, pos);
    }

    public static float getBiomeTemperature(World world, Biome biome, RegistryKey<Biome> key, BlockPos pos)
    {
        if (!SeasonsConfig.isDimensionWhitelisted(world.dimension()))
        {
            return biome.getTemperature(pos);
        }

        return getBiomeTemperatureInSeason(new SeasonTime(SeasonHelper.getSeasonState(world).getSeasonCycleTicks()).getSubSeason(), biome, key, pos);
    }

    public static float getBiomeTemperatureInSeason(Season.SubSeason subSeason, Biome biome, RegistryKey<Biome> key, BlockPos pos)
    {
        boolean tropicalBiome = BiomeConfig.usesTropicalSeasons(key);
        float biomeTemp = biome.getTemperature(pos);
        if (!tropicalBiome && biome.getBaseTemperature() <= 0.8F && BiomeConfig.enablesSeasonalEffects(key))
        {
            switch (subSeason)
            {
                default:
                    break;

                case LATE_SPRING: case EARLY_AUTUMN:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.1F, -0.5F, 2.0F);
                break;

                case MID_SPRING: case MID_AUTUMN:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.2F, -0.5F, 2.0F);
                break;

                case EARLY_SPRING: case LATE_AUTUMN:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.4F, -0.5F, 2.0F);
                break;

                case EARLY_WINTER: case MID_WINTER: case LATE_WINTER:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.8F, -0.5F, 2.0F);
                break;
            }
        }

        return biomeTemp;
    }

    public static boolean shouldRainInBiomeInSeason(World world, RegistryKey<Biome> biomeKey)
    {
        Biome biome = BiomeUtil.getBiome(biomeKey);

        if (BiomeConfig.usesTropicalSeasons(biomeKey))
        {
            Season.TropicalSeason tropicalSeason = SeasonHelper.getSeasonState(world).getTropicalSeason();

            switch (tropicalSeason)
            {
                case MID_DRY:
                    return false;

                case MID_WET:
                    return true;
            }
        }

        return biome.getPrecipitation() == Biome.RainType.RAIN;
    }
}
