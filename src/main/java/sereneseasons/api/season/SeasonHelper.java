/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.api.season;

import net.minecraft.init.Biomes;
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

    /**
     * Shifts temperature down in some biomes like Plains to avoid raining in winter for them.
     * 
     * @param temperature temperature as retrieved from {@link Biome#getTemperature} or
     * {@link Biome#getDefaultTemperature}
     * @param biome regarded biome
     * @param season regarded season
     * @return the modified temperature
     */
    private static float modifyTemperature(float temperature, Biome biome, Season season ) {
		if( biome == Biomes.PLAINS && season == Season.WINTER ) {
			temperature -= 0.1F;
		}
		
		return temperature;
    }
    
    /**
     * Returns a modified temperature by {@link #modifyTemperature(float, Biome, Season)}
     * returned by {@link Biome#getTemperature(BlockPos)}.
     * 
     * @param biome regarded biome
     * @param season regarded season
     * @return the modified temperature
     */
    public static float getModifiedTemperatureForBiome(Biome biome, Season season) {
		float temperature = biome.getDefaultTemperature();
		return modifyTemperature(temperature, biome, season);
    }
    
    /**
     * Returns a modified temperature by {@link #modifyTemperature(float, Biome, Season)}
     * returned by {@link Biome#getDefaultTemperature()}.
     * 
     * @param biome regarded biome
     * @param season regarded season
     * @return the modified temperature
     */
    public static float getModifiedFloatTemperatureAtPos(Biome biome, BlockPos pos, Season season) {
		float temperature = biome.getTemperature(pos);
		return modifyTemperature(temperature, biome, season);
    }

    /**
     * Helper method for {@link SeasonASMHelper} evaluating the temperature. Was used in MC1.7.10 version
     * to override the temperature returned by {@link Biome}, but now is only used to trigger rain/snow particles
     * at {@link net.minecraft.client.renderer.EntityRenderer#addRainParticles} for MC1.11.2 or higher. 
     * 
     * @param biome actual biome. Must match the biome at given position.
     * @param pos actual position
     * @param subSeason actual season
     * @return the temperature influenced by season.
     */
	public static float getSeasonFloatTemperature(Biome biome, BlockPos pos, SubSeason subSeason) {
        boolean tropicalBiome = BiomeConfig.usesTropicalSeasons(biome);
        float biomeTemp = biome.getTemperature(pos);
		float biomeTempForCheck = getModifiedFloatTemperatureAtPos(biome, pos, subSeason.getSeason());

        if (!tropicalBiome && biomeTempForCheck <= 0.8F && biomeTempForCheck > 0.15F)
        {
	        switch ((SubSeason) subSeason)
	        {
	        	default:
	        		break;
	        
		        case LATE_SPRING: case EARLY_AUTUMN:
		    		biomeTemp = MathHelper.clamp(biomeTemp - 0.1F, -0.25F, 2.0F);
		    		break;
	        
		        case MID_SPRING: case MID_AUTUMN:
		    		biomeTemp = MathHelper.clamp(biomeTemp - 0.2F, 0.15F, 2.0F);
		    		break;
	        
	        	case EARLY_SPRING: case LATE_AUTUMN:
		    		biomeTemp = MathHelper.clamp(biomeTemp - 0.4F, 0.15F, 2.0F);
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
}
