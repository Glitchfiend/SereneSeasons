/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.season;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.init.ModConfig;
import sereneseasons.util.ISeasonsColorResolver;

public class SeasonASMHelper
{
    ///////////////////
    // World methods //
    ///////////////////

    // Legacy
    public static boolean canSnowAtInSeason(World world, BlockPos pos, boolean checkLight, @Nullable ISeasonState seasonState)
    {
        return canSnowAtInSeason(world, pos, checkLight, seasonState, false);
    }

    public static boolean canSnowAtInSeason(World world, BlockPos pos, boolean checkLight, @Nullable ISeasonState seasonState, boolean useUnmodifiedTemperature)
    {
        Biome biome = world.getBiome(pos);
        float temperature = biome.getTemperature(pos);

        if (BiomeConfig.enablesSeasonalEffects(biome) && !useUnmodifiedTemperature && SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension()))
        {
            if (BiomeConfig.usesTropicalSeasons(biome))
            {
                return false;
            }

            temperature = getFloatTemperature(world, biome, pos);
        }

        if (temperature >= 0.15F)
        {
            return false;
        }
        else if (biome.getDefaultTemperature() >= 0.15F && !ModConfig.seasons.generateSnowAndIce)
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
        return canBlockFreezeInSeason(world, pos, noWaterAdj, seasonState, false);
    }
    
    public static boolean canBlockFreezeInSeason(World world, BlockPos pos, boolean noWaterAdj, @Nullable ISeasonState seasonState, boolean useUnmodifiedTemperature)
    {
        Biome biome = world.getBiome(pos);
        float temperature = biome.getTemperature(pos);

        if (BiomeConfig.enablesSeasonalEffects(biome) && !useUnmodifiedTemperature && SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension()))
        {
            if (BiomeConfig.usesTropicalSeasons(biome))
            {
                return false;
            }

            temperature = getFloatTemperature(world, biome, pos);
        }

        if (temperature >= 0.15F)
        {
            return false;
        }
        else if (biome.getDefaultTemperature() >= 0.15F && !ModConfig.seasons.generateSnowAndIce)
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
//        if( !SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension()) )
//        	return isRainingAtVanilla(world, pos);
        
        Biome biome = world.getBiome(pos);

        if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension()))
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
    
/*    private static boolean isRainingAtVanilla(World world, BlockPos position)
    {
        if (!world.isRaining())
        {
            return false;
        }
        else if (!world.canSeeSky(position))
        {
            return false;
        }
        else if (world.getPrecipitationHeight(position).getY() > position.getY())
        {
            return false;
        }
        else
        {
            Biome biome = world.getBiome(position);

            if (biome.getEnableSnow())
            {
                return false;
            }
            else
            {
                return world.canSnowAt(position, false) ? false : biome.canRain();
            }
        }
    } */
    
    ///////////////////
    // Biome methods //
    ///////////////////
    
    public static float getFloatTemperature(World world, Biome biome, BlockPos pos)
    {
    	if (!SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension()))
    	{
    		return biome.getTemperature(pos);
    	}
    	
        return getFloatTemperature(new SeasonTime(SeasonHelper.getSeasonState(world).getSeasonCycleTicks()).getSubSeason(), biome, pos);
    }

    public static float getFloatTemperature(SubSeason subSeason, Biome biome, BlockPos pos)
    {
    	float biomeTemp = biome.getTemperature(pos);
        boolean tropicalBiome = BiomeConfig.usesTropicalSeasons(biome);

        if (!tropicalBiome && biome.getDefaultTemperature() <= 0.8F && BiomeConfig.enablesSeasonalEffects(biome))
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

    ////////////////////////////
    // EntityRenderer methods //
    ////////////////////////////

    public static boolean shouldRenderRainSnow(World world, Biome biome)
    {
        if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension()))
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
        if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension()))
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
    
    //////////////////////////////
    // BiomeColorHelper methods //
    //////////////////////////////
    
    public static int getColorAtPosExtended(IBlockAccess blockAccess, BlockPos pos, ISeasonsColorResolver colorResolver) {
        int i = 0;
        int j = 0;
        int k = 0;

        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1)))
        {
            int l = colorResolver.getColorAtPos(blockAccess, blockAccess.getBiome(blockpos$mutableblockpos), blockpos$mutableblockpos);
            i += (l & 16711680) >> 16;
            j += (l & 65280) >> 8;
            k += l & 255;
        }

        return (i / 9 & 255) << 16 | (j / 9 & 255) << 8 | k / 9 & 255;
    }
}
