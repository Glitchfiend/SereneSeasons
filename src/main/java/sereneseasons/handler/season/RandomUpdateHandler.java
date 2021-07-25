/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.handler.season;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
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
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonHooks;

import java.util.Optional;

@Mod.EventBusSubscriber
public class RandomUpdateHandler
{
	private void adjustWeatherFrequency(Level world, Season season)
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

	private void meltInChunk(ChunkMap chunkManager, LevelChunk chunkIn, Season.SubSeason subSeason)
	{
		ServerLevel world = chunkManager.level;
		ChunkPos chunkpos = chunkIn.getPos();
		int i = chunkpos.getMinBlockX();
		int j = chunkpos.getMinBlockZ();
		int meltRand;

		switch (subSeason)
		{
			case EARLY_SPRING:
				meltRand = 16;
				break;
			case MID_SPRING:
				meltRand = 12;
				break;
			case LATE_SPRING:
				meltRand = 8;
				break;
			default:
				meltRand = 4;
				break;
		}

		if (world.random.nextInt(meltRand) == 0)
		{
			BlockPos topAirPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, world.getBlockRandomPos(i, 0, j, 15));
			BlockPos topGroundPos = topAirPos.below();
			BlockState aboveGroundState = world.getBlockState(topAirPos);
			BlockState groundState = world.getBlockState(topGroundPos);
			ResourceKey<Biome> biome = world.getBiomeName(topAirPos).orElse(null);

			if (!BiomeConfig.enablesSeasonalEffects(biome))
				return;

			if (aboveGroundState.getBlock() == Blocks.SNOW)
			{
				if (SeasonHooks.getBiomeTemperature(world, biome, topGroundPos) >= 0.15F)
				{
					world.setBlockAndUpdate(topAirPos, Blocks.AIR.defaultBlockState());
				}
			}
			else if (groundState.getBlock() == Blocks.ICE)
			{
				if (SeasonHooks.getBiomeTemperature(world, biome, topGroundPos) >= 0.15F)
				{
					((IceBlock) Blocks.ICE).melt(groundState, world, topGroundPos);
				}
			}
		}
	}


	//Randomly melt ice and snow when it isn't winter
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER)
		{
			Season.SubSeason subSeason = SeasonHelper.getSeasonState(event.world).getSubSeason();
			Season season = subSeason.getSeason();

			this.adjustWeatherFrequency(event.world, season);

			if (season != Season.WINTER)
			{
				if (SeasonsConfig.generateSnowAndIce.get() && SeasonsConfig.isDimensionWhitelisted(event.world.dimension()))
				{
					ServerLevel world = (ServerLevel) event.world;
					ChunkMap chunkManager = world.getChunkSource().chunkMap;

					// Replicate the behaviour of ServerChunkProvider
					chunkManager.getChunks().forEach((chunkHolder) ->
					{
						Optional<LevelChunk> optional = chunkHolder.getEntityTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left();
						if (optional.isPresent())
						{
							LevelChunk chunk = optional.get();
							ChunkPos chunkpos = chunkHolder.getPos();
							if (!chunkManager.noPlayersCloseForSpawning(chunkpos))
							{
								this.meltInChunk(chunkManager, chunk, subSeason);
							}
						}
					});
				}
			}
		}
	}
}