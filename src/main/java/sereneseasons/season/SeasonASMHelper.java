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
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.asm.IBiomeMixin;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;

public class SeasonASMHelper
{
    ///////////////////
    // World methods //
    ///////////////////

    // Legacy
    public static boolean canSnowAtInSeason(World world, int x, int y, int z, boolean checkLight, @Nullable ISeasonState seasonState)
    {
        return canSnowAtInSeason(world, x, y, z, checkLight, seasonState, false);
    }

    public static boolean canSnowAtInSeason(World world, int x, int y, int z, boolean checkLight, @Nullable ISeasonState seasonState, boolean useUnmodifiedTemperature)
    {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        IBiomeMixin biomeMixin = (IBiomeMixin) biome;
        float temperature = biomeMixin.getFloatTemperatureOld(x,  y,  z);

        if (BiomeConfig.enablesSeasonalEffects(biome) && !useUnmodifiedTemperature && SeasonsConfig.isDimensionWhitelisted(world.provider.dimensionId))
        {
            if (BiomeConfig.usesTropicalSeasons(biome))
            {
                return false;
            }

            temperature = getFloatTemperature(world, biome, x, y, z);
        }

        if (temperature >= 0.15F)
        {
            return false;
        }
        else if (biome.temperature >= 0.15F && !ModConfig.seasons.generateSnowAndIce)
        {
        	return false;
        }
        else if (checkLight)
        {
            if (y >= 0 && y < 256 && world.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z) < 10)
            {
                Block block = world.getBlock(x, y, z);

                if (block.isAir(world, x, y, z) && Blocks.snow_layer.canPlaceBlockAt(world, x, y, z))
                {
                    return true;
                }
            }

            return false;
        }
        
        return true;
    }
    
    public static boolean canBlockFreezeInSeason(World world, int x, int y, int z, boolean noWaterAdj, @Nullable ISeasonState seasonState)
    {
        return canBlockFreezeInSeason(world, x, y, z, noWaterAdj, seasonState, false);
    }
    
    public static boolean canBlockFreezeInSeason(World world, int x, int y, int z, boolean noWaterAdj, @Nullable ISeasonState seasonState, boolean useUnmodifiedTemperature)
    {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        IBiomeMixin biomeMixin = (IBiomeMixin) biome;
        float temperature = biomeMixin.getFloatTemperatureOld(x, y, z);

        if (BiomeConfig.enablesSeasonalEffects(biome) && !useUnmodifiedTemperature && SeasonsConfig.isDimensionWhitelisted(world.provider.dimensionId))
        {
            if (BiomeConfig.usesTropicalSeasons(biome))
            {
                return false;
            }

            temperature = getFloatTemperature(world, biome, x, y, z);
        }

        if (temperature >= 0.15F)
        {
            return false;
        }
        else if (biome.temperature >= 0.15F && !ModConfig.seasons.generateSnowAndIce)
        {
        	return false;
        }
        else
        {
            if (y>= 0 && y < 256 && world.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z) < 10)
            {
                Block block = world.getBlock(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);

                if ((block == Blocks.water || block == Blocks.flowing_water) && meta == 0)
                {
                    if (!noWaterAdj)
                    {
                        return true;
                    }

                    boolean flag = isWater(world, x - 1, y, z) && isWater(world, x + 1, y, z) && isWater(world, x, y, z - 1) && isWater(world, x, y, z + 1);

                    if (!flag)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }
    
    private static boolean isWater(IBlockAccess world, int x, int y, int z)
    {
        return world.getBlock(x, y, z).getMaterial() == Material.water;
    }

    public static boolean isRainingAtInSeason(World world, int x, int y, int z, ISeasonState seasonState)
    {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        IBiomeMixin biomeMixin = (IBiomeMixin) biome;

        if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(world.provider.dimensionId))
        {
            Season.TropicalSeason tropicalSeason = seasonState.getTropicalSeason();

            switch ((Season.TropicalSeason) tropicalSeason)
            {
	            case MID_DRY:
	            	return false;
	            	
	            case MID_WET:
	            	return true;
	            	
	            default:
	            	return biome.enableRain;
            }
        }

        if (biomeMixin.getEnableSnowOld() || (world.canSnowAtBody(x, y, z, false)))
        {
            return false;
        }

        return biome.enableRain;
    }
    
    ///////////////////
    // Biome methods //
    ///////////////////

    public static float getFloatTemperature(World world, BiomeGenBase biome, int x, int y, int z)
    {
        if (!SeasonsConfig.isDimensionWhitelisted(world.provider.dimensionId))
        {
            IBiomeMixin biomeMixin = (IBiomeMixin) biome;
            return biomeMixin.getFloatTemperatureOld(x, y, z);
        }
    	
        return getFloatTemperature(new SeasonTime(SeasonHelper.getSeasonState(world).getSeasonCycleTicks()).getSubSeason(), biome, x, y, z);
    }

    public static float getFloatTemperature(SubSeason subSeason, BiomeGenBase biome, int x, int y, int z)
    {
        boolean tropicalBiome = BiomeConfig.usesTropicalSeasons(biome);
        IBiomeMixin biomeMixin = (IBiomeMixin) biome;
        float biomeTemp = biomeMixin.getFloatTemperatureOld(x, y, z);

        if (!tropicalBiome && biome.temperature <= 0.8F && BiomeConfig.enablesSeasonalEffects(biome))
        {
	        switch (subSeason)
	        {
	        	default:
	        		break;
	        
		        case LATE_SPRING: case EARLY_AUTUMN:
		    		biomeTemp = MathHelper.clamp_float(biomeTemp - 0.1F, -0.5F, 2.0F);
		    		break;
	        
		        case MID_SPRING: case MID_AUTUMN:
		    		biomeTemp = MathHelper.clamp_float(biomeTemp - 0.2F, -0.5F, 2.0F);
		    		break;
	        
	        	case EARLY_SPRING: case LATE_AUTUMN:
		    		biomeTemp = MathHelper.clamp_float(biomeTemp - 0.4F, -0.5F, 2.0F);
		    		break;
	    		
	        	case EARLY_WINTER: case MID_WINTER: case LATE_WINTER:
		    		biomeTemp = MathHelper.clamp_float(biomeTemp - 0.8F, -0.5F, 2.0F);
	        		break;
	        }
        }
        
        return biomeTemp;
    }

    ////////////////////////////
    // EntityRenderer methods //
    ////////////////////////////

    public static boolean shouldRenderRainSnow(World world, BiomeGenBase biome)
    {
        IBiomeMixin biomeMixin = (IBiomeMixin) biome;
        if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(world.provider.dimensionId))
        {
            Season.TropicalSeason tropicalSeason = SeasonHelper.getSeasonState(world).getTropicalSeason();

            switch ((Season.TropicalSeason) tropicalSeason)
            {
	            case MID_DRY:
	            	return false;
	            	
	            case MID_WET:
	            	return true;
	            	
	            default:
	            	return biome.enableRain || biomeMixin.getEnableSnowOld();
            }
        }

        return biome.enableRain || biomeMixin.getEnableSnowOld();
    }

    public static boolean shouldAddRainParticles(World world, BiomeGenBase biome)
    {
        if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(world.provider.dimensionId))
        {
            Season.TropicalSeason tropicalSeason = SeasonHelper.getSeasonState(world).getTropicalSeason();

            switch ((Season.TropicalSeason) tropicalSeason)
            {
	            case MID_DRY:
	            	return false;
	            	
	            case MID_WET:
	            	return true;
	            	
	            default:
	            	return biome.enableRain;
            }
        }

        return biome.enableRain;
    }
}
