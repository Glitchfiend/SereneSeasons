/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.handler.season;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.config.ServerConfig;
import sereneseasons.config.ServerConfig.MeltChanceInfo;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModTags;
import sereneseasons.season.SeasonHooks;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber
public class RandomUpdateHandler
{
	private static void adjustWeatherFrequency(Level world, Season season)
	{
		if (!SeasonsConfig.changeWeatherFrequency.get())
			return;

		ServerLevelData serverLevelData = (ServerLevelData)world.getLevelData();

		if (season == Season.WINTER)
		{
			if (serverLevelData.isThundering())
			{
				serverLevelData.setThundering(false);
			}
			if (!world.getLevelData().isRaining() && serverLevelData.getRainTime() > 36000)
			{
				serverLevelData.setRainTime(world.random.nextInt(24000) + 12000);
			}
		}
		else
		{
			if (season == Season.SPRING)
			{
				if (!world.getLevelData().isRaining() && serverLevelData.getRainTime() > 96000)
				{
					serverLevelData.setRainTime(world.random.nextInt(84000) + 12000);
				}
			}
			else if (season == Season.SUMMER)
			{
				if (!world.getLevelData().isThundering() && serverLevelData.getThunderTime() > 36000)
				{
					serverLevelData.setThunderTime(world.random.nextInt(24000) + 12000);
				}
			}
		}
	}

	private static void meltInChunk(ChunkMap chunkManager, LevelChunk chunkIn, int meltRand)
	{
		ServerLevel world = chunkManager.level;
		ChunkPos chunkpos = chunkIn.getPos();
		int i = chunkpos.getMinBlockX();
		int j = chunkpos.getMinBlockZ();

		if (meltRand > 0 && world.random.nextInt(meltRand) == 0)
		{
			BlockPos topAirPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, world.getBlockRandomPos(i, 0, j, 15));
			BlockPos topGroundPos = topAirPos.below();
			BlockState aboveGroundState = world.getBlockState(topAirPos);
			BlockState groundState = world.getBlockState(topGroundPos);
			Holder<Biome> biome = world.getBiome(topAirPos);

			if (biome.is(ModTags.Biomes.BLACKLISTED_BIOMES))
				return;

			if (SeasonHooks.getBiomeTemperature(world, biome, topGroundPos) >= 0.15F)
			{
				if(aboveGroundState.getBlock() == Blocks.SNOW) world.setBlockAndUpdate(topAirPos, Blocks.AIR.defaultBlockState());
				else if(groundState.getBlock() == Blocks.ICE) ((IceBlock) Blocks.ICE).melt(groundState, world, topGroundPos);
			}
		}
	}


	//Randomly melt ice and snow when it isn't winter
	@SubscribeEvent
	public static void onWorldTick(TickEvent.LevelTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER)
		{
			Season.SubSeason subSeason = SeasonHelper.getSeasonState(event.level).getSubSeason();
			Season season = subSeason.getSeason();

			MeltChanceInfo meltInfo =  ServerConfig.getMeltInfo(subSeason);
			int meltRand = meltInfo.getMeltChance();
			int rolls = meltInfo.getRolls();

			adjustWeatherFrequency(event.level, season);

			if(rolls > 0 && meltRand > 0)
			{
				if (SeasonsConfig.generateSnowAndIce.get() && ServerConfig.isDimensionWhitelisted(event.level.dimension()))
				{
					ServerLevel level = (ServerLevel) event.level;
					ChunkMap chunkMap = level.getChunkSource().chunkMap;
					DistanceManager distanceManager = chunkMap.getDistanceManager();

					int l = distanceManager.getNaturalSpawnChunkCount();
					List<ChunkAndHolder> list = Lists.newArrayListWithCapacity(l);

					// Replicate the behaviour of ServerChunkCache
					for (ChunkHolder chunkholder : chunkMap.getChunks())
					{
						LevelChunk levelchunk = chunkholder.getTickingChunk();
						if (levelchunk != null)
						{
							list.add(new ChunkAndHolder(levelchunk, chunkholder));
						}
					}

					Collections.shuffle(list);

					for (ChunkAndHolder serverchunkcache$chunkandholder : list)
					{
						LevelChunk levelChunk = serverchunkcache$chunkandholder.chunk;
						ChunkPos chunkpos = levelChunk.getPos();
						if ((chunkMap.anyPlayerCloseEnoughForSpawning(chunkpos)) || distanceManager.shouldForceTicks(chunkpos.toLong()))
						{
							if (level.shouldTickBlocksAt(chunkpos.toLong()))
							{
								for(int i = 0; i < rolls; i++)
								{
									meltInChunk(chunkMap, levelChunk, meltRand);
								}
							}
						}
					}
				}
			}
		}
	}

	record ChunkAndHolder(LevelChunk chunk, ChunkHolder holder) {
	}
}