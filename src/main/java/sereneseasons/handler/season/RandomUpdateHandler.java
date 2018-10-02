/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import java.util.Iterator;

import net.minecraft.block.BlockIce;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

public class RandomUpdateHandler 
{
    //Randomly melt ice and snow when it isn't winter
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == Phase.END && event.side == Side.SERVER)
        {
            WorldServer world = (WorldServer)event.world;
            int dimId = world.provider.getDimension();
            if( SeasonHandler.isDimensionBlacklisted(dimId) )
            	return;
            
            Season season = SeasonHelper.getSeasonState(world).getSubSeason().getSeason();
            Season.SubSeason subSeason = SeasonHelper.getSeasonState(world).getSubSeason();
            
            //Only melt when it isn't winter
            if (subSeason != Season.SubSeason.EARLY_WINTER && subSeason != Season.SubSeason.MID_WINTER && subSeason != Season.SubSeason.LATE_WINTER)
            {
                for (Iterator<Chunk> iterator = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator()); iterator.hasNext();)
                {
                    Chunk chunk = (Chunk)iterator.next();
                    int x = chunk.x * 16;
                    int z = chunk.z * 16;
                    
                    int rand;
                    switch (subSeason)
                    {
	                    case EARLY_SPRING:
	                    	rand = 16;
	                    	break;
	                    case MID_SPRING:
	                    	rand = 12;
	                    	break;
	                    case LATE_SPRING:
	                    	rand = 8;
	                    	break;
	                    default:
	                    	rand = 4;
	                    	break;
                    }

                    if (world.rand.nextInt(rand) == 0)
                    {
                        world.updateLCG = world.updateLCG * 3 + 1013904223;
                        int randOffset = world.updateLCG >> 2;
                        BlockPos topPos = world.getPrecipitationHeight(new BlockPos(x + (randOffset & 15), 0, z + (randOffset >> 8 & 15)));
                        BlockPos groundPos = topPos.down();

                        if (world.getBlockState(groundPos).getBlock() == Blocks.ICE && !SeasonHelper.canSnowAtTempInSeason(season, world.getBiome(groundPos).getTemperature(groundPos)))
                        {
                            ((BlockIce)Blocks.ICE).turnIntoWater(world, groundPos);
                        }
                        else
                        {
                        	for (int i = topPos.getY(); i > 0; i--)
                        	{
                        		if (world.getBlockState(groundPos.down(i)).getBlock() == Blocks.ICE && !SeasonHelper.canSnowAtTempInSeason(season, world.getBiome(groundPos.down(i)).getTemperature(groundPos.down(i))))
                        		{
                        			((BlockIce)Blocks.ICE).turnIntoWater(world, groundPos.down(i));
                        			break;
                        		}
                        	}
                        }

                        if (world.getBlockState(topPos).getBlock() == Blocks.SNOW_LAYER && !SeasonHelper.canSnowAtTempInSeason(season, world.getBiome(topPos).getTemperature(topPos)))
                        {
                            world.setBlockToAir(topPos);
                        }
                        else
                        {
                        	for (int i = topPos.getY(); i > 0; i--)
                        	{
                        		if (world.getBlockState(topPos.down(i)).getBlock() == Blocks.SNOW_LAYER && !SeasonHelper.canSnowAtTempInSeason(season, world.getBiome(topPos.down(i)).getTemperature(topPos.down(i))))
                        		{
                        			world.setBlockToAir(topPos.down(i));
                        			break;
                        		}
                        	}
                        }
                    }
                }
            }
        }
    }
}
