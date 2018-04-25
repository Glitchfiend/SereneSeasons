/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.api.season;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.config.BiomeConfig;

public class SeasonHelper 
{
    public static ISeasonDataProvider dataProvider;

    /** 
     * Obtains data about the state of the season cycle in the world. This works both on
     * the client and the server.
     */
    public static ISeasonState getSeasonState(World world)
    {
    	ISeasonState data;

        if (!world.isRemote)
        {
            data = dataProvider.getServerSeasonState(world);
        }
        else
        {
            data = dataProvider.getClientSeasonState();
        }

        return data;
    }
    
    /**
     * Checks if the season provided allows snow to fall at a certain
     * biome temperature.
     * 
     * @param season The season to check
     * @param temperature The biome temperature to check
     * @return True if suitable, otherwise false
     */
    public static boolean canSnowAtTempInSeason(Season season, float temperature)
    {
        //If we're in winter, the temperature can be anything equal to or below 0.8
        return temperature < 0.15F || (season == Season.WINTER && temperature <= 0.8F );
    }

    private static float modifyTemperature(float temperature, Biome biome, Season season ) {
		if( biome == Biomes.PLAINS && season == Season.WINTER ) {
			temperature -= 0.1F;
		}
		
		return temperature;
    }
    
    public static float getModifiedTemperatureForBiome(Biome biome, Season season) {
		float temperature = biome.getDefaultTemperature();
		return modifyTemperature(temperature, biome, season);
    }
    
    public static float getModifiedFloatTemperatureAtPos(Biome biome, BlockPos pos, Season season) {
		float temperature = biome.getTemperature(pos);
		return modifyTemperature(temperature, biome, season);
    }

	public static float getSeasonFloatTemperature(Biome biome, BlockPos pos, SubSeason subSeason) {
        boolean tropicalBiome = BiomeConfig.usesTropicalSeasons(biome);
        float biomeTemp = biome.getTemperature(pos);

        if (!tropicalBiome && getModifiedTemperatureForBiome(biome, subSeason.getSeason()) <= 0.8F && biome.getDefaultTemperature() > 0.0F)
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
	
    public interface ISeasonDataProvider
    {
        ISeasonState getServerSeasonState(World world);
        ISeasonState getClientSeasonState();
    }

    public static IBlockState getCropFromType(int type)
    {
        switch(type) {
        case 1:
            return Blocks.WHEAT.getDefaultState();
        case 2:
            return Blocks.POTATOES.getDefaultState();
        case 3:
            return Blocks.CARROTS.getDefaultState();
        case 4:
            return Blocks.PUMPKIN_STEM.getDefaultState();
        case 5:
            return Blocks.MELON_STEM.getDefaultState();
        case 6:
            return Blocks.BEETROOTS.getDefaultState();
        }
        return null;
    }

    public static int getTypeFromCrop(Block block)
    {
        if( block == Blocks.BEETROOTS )
            return 6;
        if( block == Blocks.MELON_STEM )
            return 5;
        if( block == Blocks.PUMPKIN_STEM )
            return 4;
        if( block == Blocks.CARROTS  )
            return 3;
        if( block == Blocks.POTATOES )
            return 2;
        if( block == Blocks.WHEAT )
            return 1;
        return 0;
    }
}
