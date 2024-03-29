/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.season;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;

public class SeasonHooks
{
    //
    // Hooks called by ASM
    //

    public static boolean shouldSnowHook(Biome biome, LevelReader levelReader, BlockPos pos)
    {
        if ((ModConfig.seasons.generateSnowAndIce && warmEnoughToRainSeasonal(levelReader, pos)) || (!ModConfig.seasons.generateSnowAndIce && biome.warmEnoughToRain(pos)))
        {
            return false;
        }
        else
        {
            if (pos.getY() >= levelReader.getMinBuildHeight() && pos.getY() < levelReader.getMaxBuildHeight() && levelReader.getBrightness(LightLayer.BLOCK, pos) < 10)
            {
                BlockState blockstate = levelReader.getBlockState(pos);
                if (blockstate.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(levelReader, pos))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean shouldFreezeWarmEnoughToRainHook(Biome biome, BlockPos pos, LevelReader levelReader)
    {
        return (ModConfig.seasons.generateSnowAndIce && warmEnoughToRainSeasonal(levelReader, pos)) || (!ModConfig.seasons.generateSnowAndIce && biome.warmEnoughToRain(pos));
    }

    public static boolean isRainingAtHook(Level level, BlockPos position)
    {
        if (!level.isRaining()) return false;
        else if (!level.canSeeSky(position)) return false;
        else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, position).getY() > position.getY()) return false;
        else
        {
            Holder<Biome> biome = level.getBiome(position);

            if (ModConfig.seasons.isDimensionWhitelisted(level.dimension()) && !biome.is(ModTags.Biomes.BLACKLISTED_BIOMES))
            {
                return getPrecipitationAtSeasonal(level, biome, position) == Biome.Precipitation.RAIN && warmEnoughToRainSeasonal(level, biome, position);
            }
            else
            {
                return biome.value().getPrecipitationAt(position) == Biome.Precipitation.RAIN && biome.value().getTemperature(position) >= 0.15F;
            }
        }
    }

    //
    // Hooks for different calls to getPrecipitationAt in Biome
    //

    public static Biome.Precipitation getPrecipitationAtTickIceAndSnowHook(LevelReader level, Biome biome, BlockPos pos)
    {
        if (!biome.hasPrecipitation())
        {
            return Biome.Precipitation.NONE;
        }
        else
        {
            boolean shouldSnow = (ModConfig.seasons.generateSnowAndIce && coldEnoughToSnowSeasonal(level, pos)) || (!ModConfig.seasons.generateSnowAndIce && biome.coldEnoughToSnow(pos));
            return shouldSnow ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
        }
    }

    //
    // General utilities
    //
    public static boolean coldEnoughToSnowSeasonal(LevelReader level, BlockPos pos)
    {
        return coldEnoughToSnowSeasonal(level, level.getBiome(pos), pos);
    }

    public static boolean coldEnoughToSnowSeasonal(LevelReader level, Holder<Biome> biome, BlockPos pos)
    {
        return !warmEnoughToRainSeasonal(level, biome, pos);
    }

    public static boolean warmEnoughToRainSeasonal(LevelReader level, BlockPos pos)
    {
        return warmEnoughToRainSeasonal(level, level.getBiome(pos), pos);
    }

    public static boolean warmEnoughToRainSeasonal(LevelReader level, Holder<Biome> biome, BlockPos pos)
    {
        return getBiomeTemperature(level, biome, pos) >= 0.15F;
    }

    public static float getBiomeTemperature(LevelReader level, Holder<Biome> biome, BlockPos pos)
    {
        if (!(level instanceof Level))
        {
            return biome.value().getTemperature(pos);
        }

        return getBiomeTemperature((Level)level, biome, pos);
    }

    public static float getBiomeTemperature(Level level, Holder<Biome> biome, BlockPos pos)
    {
        if (!ModConfig.seasons.isDimensionWhitelisted(level.dimension()) || biome.is(ModTags.Biomes.BLACKLISTED_BIOMES))
        {
            return biome.value().getTemperature(pos);
        }

        return getBiomeTemperatureInSeason(new SeasonTime(SeasonHelper.getSeasonState(level).getSeasonCycleTicks()).getSubSeason(), biome, pos);
    }

    public static float getBiomeTemperatureInSeason(Season.SubSeason subSeason, Holder<Biome> biome, BlockPos pos)
    {
        boolean tropicalBiome = biome.is(ModTags.Biomes.TROPICAL_BIOMES);
        float biomeTemp = biome.value().getTemperature(pos);
        if (!tropicalBiome && biome.value().getBaseTemperature() <= 0.8F && !biome.is(ModTags.Biomes.BLACKLISTED_BIOMES))
        {
            biomeTemp = Mth.clamp(biomeTemp + ModConfig.seasons.getSeasonProperties(subSeason).biomeTempAdjustment(), -0.5F, 2.0F);
        }

        return biomeTemp;
    }

    public static boolean hasPrecipitationSeasonal(Level level, Holder<Biome> biome)
    {
        if (biome.is(ModTags.Biomes.TROPICAL_BIOMES))
        {
            Season.TropicalSeason tropicalSeason = SeasonHelper.getSeasonState(level).getTropicalSeason();

            switch (tropicalSeason)
            {
                case MID_DRY:
                    return false;

                case MID_WET:
                    return true;

                default:
                    break;
            }
        }

        return biome.value().hasPrecipitation();
    }

    public static Biome.Precipitation getPrecipitationAtSeasonal(Level level, Holder<Biome> biome, BlockPos pos)
    {
        if (!hasPrecipitationSeasonal(level, biome))
        {
            return Biome.Precipitation.NONE;
        }
        else
        {
            return coldEnoughToSnowSeasonal(level, biome, pos) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
        }
    }
}
