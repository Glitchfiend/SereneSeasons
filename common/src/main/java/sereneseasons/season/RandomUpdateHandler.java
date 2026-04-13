/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.season;

import com.google.common.collect.Lists;
import glitchcore.event.TickEvent;
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
import net.minecraft.world.level.saveddata.WeatherData;
import net.minecraft.world.level.storage.ServerLevelData;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;

import java.util.Collections;
import java.util.List;

public class RandomUpdateHandler
{
	private static void adjustWeatherFrequency(Level world, Season.SubSeason subSeason)
	{
		if (!ModConfig.seasons.changeWeatherFrequency)
			return;

		WeatherData weatherData = ((ServerLevel)world).getWeatherData();
		SeasonsConfig.SeasonProperties seasonProperties = ModConfig.seasons.getSeasonProperties(subSeason);

		if (seasonProperties.canRain())
		{
			if (!weatherData.isRaining() && weatherData.getRainTime() > seasonProperties.maxRainTime())
			{
				weatherData.setRainTime(world.getRandom().nextInt(seasonProperties.maxRainTime() - seasonProperties.minRainTime()) + seasonProperties.minRainTime());
			}
		}
		else if (weatherData.isRaining()) weatherData.setRaining(false);

		if (seasonProperties.canThunder())
		{
			if (!weatherData.isThundering() && weatherData.getThunderTime() > seasonProperties.maxThunderTime())
			{
				weatherData.setThunderTime(world.getRandom().nextInt(seasonProperties.maxThunderTime() - seasonProperties.minThunderTime()) + seasonProperties.minThunderTime());
			}
		}
		else if (weatherData.isThundering()) weatherData.setThundering(false);
	}

	private static void meltInChunk(ChunkMap chunkMap, LevelChunk chunkIn, float meltChance)
	{
		ServerLevel world = chunkMap.level;
		ChunkPos chunkpos = chunkIn.getPos();
		int i = chunkpos.getMinBlockX();
		int j = chunkpos.getMinBlockZ();

		if (meltChance > 0 && world.getRandom().nextFloat() < meltChance)
		{
			BlockPos topAirPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, world.getBlockRandomPos(i, 0, j, 15));
			BlockPos topGroundPos = topAirPos.below();
			BlockState aboveGroundState = world.getBlockState(topAirPos);
			BlockState groundState = world.getBlockState(topGroundPos);
			Holder<Biome> biome = world.getBiome(topAirPos);
			Holder<Biome> groundBiome = world.getBiome(topGroundPos);

			if (!biome.is(ModTags.Biomes.BLACKLISTED_BIOMES) && SeasonHooks.getBiomeTemperature(world, biome, topGroundPos, world.getSeaLevel()) >= 0.15F)
			{
				if (aboveGroundState.getBlock() == Blocks.SNOW)
				{
					world.setBlockAndUpdate(topAirPos, Blocks.AIR.defaultBlockState());
				}
			}

			if (!groundBiome.is(ModTags.Biomes.BLACKLISTED_BIOMES) && SeasonHooks.getBiomeTemperature(world, groundBiome, topGroundPos, world.getSeaLevel()) >= 0.15F)
			{
				if (groundState.getBlock() == Blocks.ICE)
				{
					((IceBlock) Blocks.ICE).melt(groundState, world, topGroundPos);
				}
			}
		}
	}


	//Randomly melt ice and snow when it isn't winter
	public static void onWorldTick(TickEvent.Level event)
	{
		if (event.getPhase() != TickEvent.Phase.END || event.getLevel().isClientSide())
			return;

		ServerLevel level = (ServerLevel)event.getLevel();
		Season.SubSeason subSeason = SeasonHelper.getSeasonState(level).getSubSeason();
		Season season = subSeason.getSeason();

		SeasonsConfig.SeasonProperties seasonProperties = ModConfig.seasons.getSeasonProperties(subSeason);
		float meltRand = seasonProperties.meltChance() / 100.0F;
		int rolls = seasonProperties.meltRolls();

		adjustWeatherFrequency(level, subSeason);

		if(rolls > 0 && meltRand > 0.0F)
		{
			if (ModConfig.seasons.generateSnowAndIce && ModConfig.seasons.isDimensionWhitelisted(level.dimension()))
			{
				ChunkMap chunkMap = level.getChunkSource().chunkMap;
				chunkMap.forEachBlockTickingChunk(chunk -> {
					ChunkPos chunkpos = chunk.getPos();
					if ((chunkMap.anyPlayerCloseEnoughForSpawning(chunkpos)))
					{
						if (level.shouldTickBlocksAt(chunkpos.pack()))
						{
							for(int i = 0; i < rolls; i++)
							{
								meltInChunk(chunkMap, chunk, meltRand);
							}
						}
					}
				});
			}
		}
	}

	record ChunkAndHolder(LevelChunk chunk, ChunkHolder holder) {
	}
}