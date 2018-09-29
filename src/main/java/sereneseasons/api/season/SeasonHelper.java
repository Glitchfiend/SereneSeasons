/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.api.season;

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
	
	/**
	 * Is called mainly from {@link SeasonASMHelper#canSnowAtInSeason}. <br/>
	 * Returns whether snow can fall on specific position. 
	 * 
	 * @param world the world
	 * @param pos the position
	 * @param checkLight if <code>true</code> it is checked if block is exposed to light.
	 * @param allowSnowLayer if <code>true</code> it will ignore if snow is already present.
	 * @param seasonState the current season state
	 * @return <code>true</code> iff yes.
	 */
    public static boolean canSnowAtInSeason(World world, BlockPos pos, boolean checkLight, boolean allowSnowLayer, @Nullable ISeasonState seasonState)
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

                if (allowSnowLayer && state.getBlock() == Blocks.SNOW_LAYER)
                {
                	return true;
                }
                
                if (state.getBlock().isAir(state, world, pos) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos))
                {
                    return true;
                }
            }

            return false;
        }
        
        return true;
    }
    
	/**
	 * Is called mainly from {@link SeasonASMHelper#canBlockFreezeInSeason}. <br/>
	 * Returns whether a specific block can be freezed. 
	 * 
	 * @param world the world
	 * @param pos the position
	 * @param noWaterAdj if <code>true</code> it will skip adjacent ice blocks.
	 * @param allowMeltableIce if <code>true</code> it will ignore if meltable ice is already present.
	 * @param seasonState the current season state
	 * @return <code>true</code> iff yes.
	 */
    public static boolean canBlockFreezeInSeason(World world, BlockPos pos, boolean noWaterAdj, boolean allowMeltableIce, @Nullable ISeasonState seasonState)
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
                else if( allowMeltableIce && block == Blocks.ICE ) {	// Note: Only blocks which are meltable. No packed ice!
                	return true;
                }
            }

            return false;
        }
    }
	
    public interface ISeasonDataProvider
    {
        ISeasonState getServerSeasonState(World world);
        ISeasonState getClientSeasonState();
    }
}
