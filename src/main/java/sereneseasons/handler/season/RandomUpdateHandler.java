/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import java.util.Iterator;

import net.minecraft.block.Block;
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
import sereneseasons.init.ModConfig;
import sereneseasons.season.SeasonASMHelper;

public class RandomUpdateHandler 
{
    //Randomly melt ice and snow when it isn't winter
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == Phase.END && event.side == Side.SERVER)
        {
        	
            Season.SubSeason subSeason = SeasonHelper.getSeasonState(event.world).getSubSeason();
            Season season = subSeason.getSeason();
            
            if(season == Season.WINTER)
            {
            	if (ModConfig.seasons.changeWeatherFrequency)
            	{
            		if (event.world.getWorldInfo().isThundering())
            		{
            			event.world.getWorldInfo().setThundering(false);;
            		}
            		if (!event.world.getWorldInfo().isRaining() && event.world.getWorldInfo().getRainTime() > 36000)
            		{
            			event.world.getWorldInfo().setRainTime(event.world.rand.nextInt(24000) + 12000);
            		}
            	}
            }
            else //Only melt when it isn't winter
            {
            	if (ModConfig.seasons.changeWeatherFrequency)
            	{
            		if (season == Season.SPRING)
            		{
            			if (!event.world.getWorldInfo().isRaining() && event.world.getWorldInfo().getRainTime() > 96000)
            			{
            				event.world.getWorldInfo().setRainTime(event.world.rand.nextInt(84000) + 12000);
            			}
            		}
            		else if (season == Season.SUMMER)
            		{
            			if (!event.world.getWorldInfo().isThundering() && event.world.getWorldInfo().getThunderTime() > 36000)
            			{
            				event.world.getWorldInfo().setThunderTime(event.world.rand.nextInt(24000) + 12000);
            			}
            		}
                }
            	
            	WorldServer world = (WorldServer)event.world;
                for (Iterator<Chunk> iterator = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator()); iterator.hasNext();)
                {
                    Chunk chunk = iterator.next();
                    int x = chunk.x << 4;
                    int z = chunk.z << 4;
                    
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
                        BlockPos pos = world.getPrecipitationHeight(new BlockPos(x + (randOffset & 15), 0, z + (randOffset >> 8 & 15)));
                        boolean first = true;

                        while (pos.getY() >= 0)
                        {
                        	Block block = world.getBlockState(pos).getBlock();

                       		if (block == Blocks.SNOW_LAYER && SeasonASMHelper.getFloatTemperature(world.getBiome(pos), pos) >= 0.15F)
                       		{
                       			world.setBlockToAir(pos);
                       			break;
                       		}

                       		if(!first)
                       		{
                       			if(block == Blocks.ICE && SeasonASMHelper.getFloatTemperature(world.getBiome(pos), pos) >= 0.15F)
                       			{
                       				((BlockIce)Blocks.ICE).turnIntoWater(world, pos);
                       				break;
                       			}
                            }
                       		else
                       			first = false;

                       		pos = pos.down();
                        }
                    }
                }
            }
        }
    }
}
