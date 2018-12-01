/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.season;

import jline.internal.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.init.ModConfig;

public class SeasonASMHelper
{
    ///////////////////
    // World methods //
    ///////////////////
    
    public static boolean canSnowAtInSeason(World world, BlockPos pos, boolean checkLight, @Nullable ISeasonState seasonState)
    {
    	return SeasonHelper.canSnowAtInSeason(world, pos, checkLight, false, seasonState);
    }
    
    public static boolean canBlockFreezeInSeason(World world, BlockPos pos, boolean noWaterAdj, @Nullable ISeasonState seasonState)
    {
    	return SeasonHelper.canBlockFreezeInSeason(world, pos, noWaterAdj, false, seasonState);
    }
    
    public static boolean isRainingAtInSeason(World world, BlockPos pos, ISeasonState seasonState)
    {
        Biome biome = world.getBiome(pos);

        if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome))
        {
            Season.TropicalSeason tropicalSeason = seasonState.getTropicalSeason();

            switch ((Season.TropicalSeason) tropicalSeason)
            {
	            case MID_DRY:
	            	return false;
	            	
	            case MID_WET:
	            	return true;
	            	
	            default:
	            	return biome.canRain();
            }
        }

        if (biome.getEnableSnow() || (world.canSnowAt(pos, false)))
        {
            return false;
        }

        return biome.canRain();
    }
    
    ///////////////////
    // Biome methods //
    ///////////////////
    
    public static float getFloatTemperature(World world, Biome biome, BlockPos pos)
    {
        return SeasonHelper.getFloatTemperature(world, biome, pos);
    }
    
    public static float getFloatTemperature(SubSeason subSeason, Biome biome, BlockPos pos)
    {
    	return SeasonHelper.getSeasonFloatTemperature(biome, pos, subSeason);
    }

    ////////////////////////////
    // EntityRenderer methods //
    ////////////////////////////

    public static boolean shouldRenderRainSnow(World world, Biome biome)
    {
    	if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome))
        {
            Season.TropicalSeason tropicalSeason = SeasonHelper.getSeasonState(world).getTropicalSeason();

            switch ((Season.TropicalSeason) tropicalSeason)
            {
	            case MID_DRY:
	            	return false;
	            	
	            case MID_WET:
	            	return true;
	            	
	            default:
	            	return biome.canRain() || biome.getEnableSnow();
            }
        }

        return biome.canRain() || biome.getEnableSnow();
    }

    public static boolean shouldAddRainParticles(World world, Biome biome)
    {
    	if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome))
        {
            Season.TropicalSeason tropicalSeason = SeasonHelper.getSeasonState(world).getTropicalSeason();

            switch ((Season.TropicalSeason) tropicalSeason)
            {
	            case MID_DRY:
	            	return false;
	            	
	            case MID_WET:
	            	return true;
	            	
	            default:
	            	return biome.canRain();
            }
        }

        return biome.canRain();
    }
}
