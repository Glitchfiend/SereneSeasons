/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.IceBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;

import java.util.Iterator;

@Mod.EventBusSubscriber
public class RandomUpdateHandler
{
	//Randomly melt ice and snow when it isn't winter
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER)
		{

			Season.SubSeason subSeason = SeasonHelper.getSeasonState(event.world).getSubSeason();
			Season season = subSeason.getSeason();

			if (season == Season.WINTER)
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
			else
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

				if (ModConfig.seasons.generateSnowAndIce && SeasonsConfig.isDimensionWhitelisted(event.world.getDimension().getType().getId()))
				{
					ServerWorld world = (ServerWorld)event.world;
					for (Iterator<Chunk> iterator = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator()); iterator.hasNext();)
					{
						Chunk chunk = iterator.next();
						int x = chunk.getPos().x << 4;
						int z = chunk.getPos().z << 4;

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
							BlockPos pos = world.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(x + (randOffset & 15), 0, z + (randOffset >> 8 & 15)));
							Biome biome = world.getBiome(pos);

							if(!BiomeConfig.enablesSeasonalEffects(biome))
								continue;

							boolean first = true;
							for (int y = pos.getY(); y >= 0; y--)
							{
								Block block = chunk.getBlockState(new BlockPos(pos.getX(), y, pos.getZ())).getBlock();

								if (block == Blocks.SNOW)
								{
									pos = new BlockPos(pos.getX(), y, pos.getZ());

									if (SeasonASMHelper.getFloatTemperature(world, biome, pos) >= 0.15F)
									{
										world.setBlockToAir(pos);
										break;
									}
								}

								if(!first)
								{
									if(block == Blocks.ICE)
									{
										pos = new BlockPos(pos.getX(), y, pos.getZ());
										// TODO
										if (SeasonASMHelper.getFloatTemperature(world, biome, pos) >= 0.15F)
										{
											((IceBlock) Blocks.ICE).turnIntoWater(world, pos);
											break;
										}
									}
								}
								else
									first = false;
							}
						}
					}
				}
			}
		}
	}
}