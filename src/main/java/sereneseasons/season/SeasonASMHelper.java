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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.Season.SubSeason;
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

        if (( biome.getEnableSnow() && seasonState.getSeason() != Season.WINTER) || (world.canSnowAt(pos, false)))
        {
            return false;
        }

        return biome.canRain();
    }
    
    ///////////////////
    // Biome methods //
    ///////////////////
    
    public static float getFloatTemperature(Biome biome, BlockPos pos)
    {
        SubSeason subSeason = new SeasonTime(SeasonHandler.clientSeasonCycleTicks).getSubSeason();
        boolean tropicalBiome = BiomeConfig.usesTropicalSeasons(biome);
        float biomeTemp = biome.getTemperature(pos);

        if (!tropicalBiome && biome.getDefaultTemperature() <= 0.8F && biome.getDefaultTemperature() > 0.0F)
        {
	        switch ((SubSeason) subSeason)
	        {
	        	default:
	        		break;
	        
		        case LATE_SPRING: case EARLY_AUTUMN:
		    		biomeTemp = MathHelper.clamp(biomeTemp - 0.1F, -0.25F, 2.0F);
		    		break;
	        
		        case MID_SPRING: case MID_AUTUMN:
		    		biomeTemp = MathHelper.clamp(biomeTemp - 0.2F, -0.25F, 2.0F);
		    		break;
	        
	        	case EARLY_SPRING: case LATE_AUTUMN:
		    		biomeTemp = MathHelper.clamp(biomeTemp - 0.4F, -0.25F, 2.0F);
		    		break;
	    		
	        	case EARLY_WINTER: case MID_WINTER: case LATE_WINTER:
	        		biomeTemp = 0.0F;
	        		break;
	        }
        }
        
        return biomeTemp;
    }

    ////////////////////////////
    // EntityRenderer methods //
    ////////////////////////////

    public static boolean shouldRenderRainSnow(World world, Biome biome)
    {
        if (BiomeConfig.usesTropicalSeasons(biome))
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
        if (BiomeConfig.usesTropicalSeasons(biome))
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
