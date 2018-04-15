/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.season;

import jline.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.handler.season.SeasonHandler;

public class SeasonASMHelper
{
    ///////////////////
    // World methods //
    ///////////////////
    
    public static boolean canSnowAtInSeason(World world, BlockPos pos, boolean checkLight, @Nullable ISeasonState seasonState)
    {
        Season season = seasonState == null ? null : seasonState.getSeason();
        Biome biome = world.getBiome(pos);
        float temperature = biome.getTemperature(pos);

        if (BiomeConfig.usesTropicalSeasons(biome))
            return false;

        //If we're in winter, the temperature can be anything equal to or below 0.7
        if (!SeasonHelper.canSnowAtTempInSeason(season, temperature))
        {
            return false;
        }
        else if (biome == Biomes.RIVER || biome == Biomes.OCEAN || biome == Biomes.DEEP_OCEAN)
        {
            return false;
        }
        else if (checkLight)
        {
            if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10)
            {
                IBlockState state = world.getBlockState(pos);

                if (state.getBlock().isAir(state, world, pos) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos))
                {
                    return true;
                }
            }

            return false;
        }
        
        return true;
    }
    
    public static boolean canBlockFreezeInSeason(World world, BlockPos pos, boolean noWaterAdj, @Nullable ISeasonState seasonState)
    {
        Season season = seasonState == null ? null : seasonState.getSeason();
        Biome biome = world.getBiome(pos);
        float temperature = biome.getTemperature(pos);

        if (BiomeConfig.usesTropicalSeasons(biome))
            return false;

        //If we're in winter, the temperature can be anything equal to or below 0.7
        if (!SeasonHelper.canSnowAtTempInSeason(season, temperature))
        {
            return false;
        }
        else if (biome == Biomes.RIVER || biome == Biomes.OCEAN || biome == Biomes.DEEP_OCEAN)
        {
            return false;
        }
        else
        {
            if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10)
            {
                IBlockState iblockstate = world.getBlockState(pos);
                Block block = iblockstate.getBlock();

                if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                {
                    if (!noWaterAdj)
                    {
                        return true;
                    }

                    boolean flag = world.isWater(pos.west()) && world.isWater(pos.east()) && world.isWater(pos.north()) && world.isWater(pos.south());

                    if (!flag)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }
    
    public static boolean isRainingAtInSeason(World world, BlockPos pos, ISeasonState seasonState)
    {
        Biome biome = world.getBiome(pos);

        if (BiomeConfig.usesTropicalSeasons(biome))
        {
            Season.TropicalSeason tropicalSeason = seasonState.getTropicalSeason();

            if (tropicalSeason == Season.TropicalSeason.MID_DRY)
                return false;
            else if (tropicalSeason == Season.TropicalSeason.MID_WET)
                return true;
        }

        if (( biome.getEnableSnow() && seasonState.getSeason() != Season.WINTER) || (world.canSnowAt(pos, false)))
            return false;

        return biome.canRain();
    }
    
    ///////////////////
    // Biome methods //
    ///////////////////
    
    public static float getFloatTemperature(Biome biome, BlockPos pos)
    {
        Season season = new SeasonTime(SeasonHandler.clientSeasonCycleTicks).getSubSeason().getSeason();
        boolean tropicalBiome = BiomeConfig.usesTropicalSeasons(biome);

        if (!tropicalBiome && biome.getDefaultTemperature() <= 0.8F && season == Season.WINTER)
        {
            return 0.0F;
        }
        else
        {
            float biomeTemp = biome.getTemperature(pos);

            if (!tropicalBiome && (season == Season.AUTUMN || season == Season.SPRING))
                biomeTemp -= 0.25;

            return biomeTemp;
        }
    }

    ////////////////////////////
    // EntityRenderer methods //
    ////////////////////////////

    public static boolean shouldRenderRainSnow(World world, Biome biome)
    {
        if (BiomeConfig.usesTropicalSeasons(biome))
        {
            Season.TropicalSeason tropicalSeason = SeasonHelper.getSeasonState(world).getTropicalSeason();

            if (tropicalSeason == Season.TropicalSeason.MID_DRY)
                return false;
            else if (tropicalSeason == Season.TropicalSeason.MID_WET)
                return true;
        }

        return biome.canRain() || biome.getEnableSnow();
    }

    public static boolean shouldAddRainParticles(World world, Biome biome)
    {
        if (BiomeConfig.usesTropicalSeasons(biome))
        {
            Season.TropicalSeason tropicalSeason = SeasonHelper.getSeasonState(world).getTropicalSeason();

            if (tropicalSeason == Season.TropicalSeason.MID_DRY)
                return false;
            else if (tropicalSeason == Season.TropicalSeason.MID_WET)
                return true;
        }

        return biome.canRain();
    }
}
