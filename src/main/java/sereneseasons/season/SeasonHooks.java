/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.season;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.config.ServerConfig;
import sereneseasons.util.biome.BiomeUtil;

public class SeasonHooks
{
    //
    // Hooks called by ASM
    //
    public static boolean shouldSnowHook(Biome biome, LevelReader levelReader, BlockPos pos)
    {
        if ((SeasonsConfig.generateSnowAndIce.get() && warmEnoughToRainHook(biome, pos, levelReader)) || (!SeasonsConfig.generateSnowAndIce.get() && biome.warmEnoughToRain(pos)))
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

    public static boolean coldEnoughToSnowHook(Biome biome, BlockPos pos, LevelReader levelReader)
    {
        return !warmEnoughToRainHook(biome, pos, levelReader);
    }

    public static boolean tickChunkColdEnoughToSnowHook(Biome biome, BlockPos pos, LevelReader levelReader)
    {
        return (SeasonsConfig.generateSnowAndIce.get() && coldEnoughToSnowHook(biome, pos, levelReader)) || (!SeasonsConfig.generateSnowAndIce.get() && biome.coldEnoughToSnow(pos));
    }

    public static boolean warmEnoughToRainHook(Biome biome, BlockPos pos, LevelReader levelReader)
    {
        return getBiomeTemperature(levelReader, biome, pos) >= 0.15F;
    }

    public static boolean shouldFreezeWarmEnoughToRainHook(Biome biome, BlockPos pos, LevelReader levelReader)
    {
        return (SeasonsConfig.generateSnowAndIce.get() && warmEnoughToRainHook(biome, pos, levelReader)) || (!SeasonsConfig.generateSnowAndIce.get() && biome.warmEnoughToRain(pos));
    }

    public static boolean shouldSnowGolemBurnHook(Biome biome, BlockPos pos, LevelReader levelReader)
    {
        return getBiomeTemperature(levelReader, biome, pos) > 1.0F;
    }

    public static boolean isRainingAtHook(Level level, BlockPos position)
    {
        if (!level.isRaining()) return false;
        else if (!level.canSeeSky(position)) return false;
        else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, position).getY() > position.getY()) return false;
        else
        {
            Biome biome = level.getBiome(position);
            ResourceKey<Biome> biomeKey = level.getBiomeName(position).orElse(null);

            if (ServerConfig.isDimensionWhitelisted(level.dimension()) && BiomeConfig.enablesSeasonalEffects(biomeKey))
            {
                if (SeasonHooks.shouldRainInBiomeInSeason(level, biomeKey))
                {
                    if (BiomeConfig.usesTropicalSeasons(biomeKey)) return true;
                    else return SeasonHooks.getBiomeTemperature(level, biome, position) >= 0.15F;
                }
                else return false;
            }
            else
            {
                return biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.getTemperature(position) >= 0.15F;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static Biome.Precipitation getLevelRendererPrecipitation(Biome biome)
    {
        ResourceKey<Biome> biomeKey = BiomeUtil.getBiomeKey(biome);
        Biome.Precipitation rainType = biome.getPrecipitation();
        Level world = Minecraft.getInstance().level;

        if (ServerConfig.isDimensionWhitelisted(world.dimension()) && BiomeConfig.enablesSeasonalEffects(biomeKey) && (rainType == Biome.Precipitation.RAIN || rainType == Biome.Precipitation.NONE))
        {
            if (SeasonHooks.shouldRainInBiomeInSeason(world, biomeKey))
                return Biome.Precipitation.RAIN;
            else
                return Biome.Precipitation.NONE;
        }

        return rainType;
    }

    //
    // General utilities
    //
    public static float getBiomeTemperature(LevelReader levelReader, Biome biome, BlockPos pos)
    {
        if (!(levelReader instanceof Level))
        {
            return biome.getTemperature(pos);
        }

        return getBiomeTemperature((Level)levelReader, biome, pos);
    }

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
        if (!ServerConfig.isDimensionWhitelisted(world.dimension()))
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
