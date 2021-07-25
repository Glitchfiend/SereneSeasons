/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.season;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.util.biome.BiomeUtil;

public class SeasonHooks
{
    //
    // Hooks called by ASM
    //

    public static float getBiomeTemperatureHook(Biome biome, BlockPos pos, LevelReader worldReader)
    {
        if (!(worldReader instanceof Level))
        {
            return biome.getTemperature(pos);
        }

        return getBiomeTemperature((Level)worldReader, biome, pos);
    }

    //
    // General utilities
    //

    public static float getBiomeTemperature(Level world, Biome biome, BlockPos pos)
    {
        return getBiomeTemperature(world, biome, world.getBiomeName(pos).orElse(null), pos);
    }

    public static float getBiomeTemperature(Level world, ResourceKey<Biome> key, BlockPos pos)
    {
        return getBiomeTemperature(world, world.getBiome(pos), key, pos);
    }

    public static float getBiomeTemperature(Level world, Biome biome, ResourceKey<Biome> key, BlockPos pos)
    {
        if (!SeasonsConfig.isDimensionWhitelisted(world.dimension()))
        {
            return biome.getTemperature(pos);
        }

        return getBiomeTemperatureInSeason(new SeasonTime(SeasonHelper.getSeasonState(world).getSeasonCycleTicks()).getSubSeason(), biome, key, pos);
    }

    public static float getBiomeTemperatureInSeason(Season.SubSeason subSeason, Biome biome, ResourceKey<Biome> key, BlockPos pos)
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
                biomeTemp = Mth.clamp(biomeTemp - 0.1F, -0.5F, 2.0F);
                break;

                case MID_SPRING: case MID_AUTUMN:
                biomeTemp = Mth.clamp(biomeTemp - 0.2F, -0.5F, 2.0F);
                break;

                case EARLY_SPRING: case LATE_AUTUMN:
                biomeTemp = Mth.clamp(biomeTemp - 0.4F, -0.5F, 2.0F);
                break;

                case EARLY_WINTER: case MID_WINTER: case LATE_WINTER:
                biomeTemp = Mth.clamp(biomeTemp - 0.8F, -0.5F, 2.0F);
                break;
            }
        }

        return biomeTemp;
    }

    public static boolean shouldRainInBiomeInSeason(Level world, ResourceKey<Biome> biomeKey)
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

        return biome.getPrecipitation() == Biome.Precipitation.RAIN;
    }
}
