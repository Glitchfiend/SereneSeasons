/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IceBlock;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.ServerWorldInfo;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;
import sereneseasons.season.SeasonHooks;
import sereneseasons.util.biome.BiomeUtil;

import java.util.Iterator;
import java.util.Optional;

@Mod.EventBusSubscriber
public class RandomUpdateHandler
{
	private void adjustWeatherFrequency(World world, Season season)
	{
		if (!SeasonsConfig.changeWeatherFrequency.get())
			return;

		IServerWorldInfo serverLevelData = (IServerWorldInfo)world.getLevelData();

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

	private void meltInChunk(ChunkManager chunkManager, Chunk chunkIn, Season.SubSeason subSeason)
	{
		ServerWorld world = chunkManager.level;
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
			BlockPos topAirPos = world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, world.getBlockRandomPos(i, 0, j, 15));
			BlockPos topGroundPos = topAirPos.below();
			BlockState aboveGroundState = world.getBlockState(topAirPos);
			BlockState groundState = world.getBlockState(topGroundPos);
			RegistryKey<Biome> biome = world.getBiomeName(topAirPos).orElse(null);

			if (!BiomeConfig.enablesSeasonalEffects(biome))
				return;

			if (aboveGroundState.getBlock() == Blocks.SNOW && SeasonsConfig.generateSnow.get())
			{
				if (SeasonHooks.getBiomeTemperature(world, biome, topGroundPos) >= 0.15F)
				{
					world.setBlockAndUpdate(topAirPos, Blocks.AIR.defaultBlockState());
				}
			}
			else if (groundState.getBlock() == Blocks.ICE && SeasonsConfig.generateIce.get())
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
				if ((SeasonsConfig.generateSnow.get() || SeasonsConfig.generateIce.get()) && SeasonsConfig.isDimensionWhitelisted(event.world.dimension()))
				{
					ServerWorld world = (ServerWorld) event.world;
					ChunkManager chunkManager = world.getChunkSource().chunkMap;

					// Replicate the behaviour of ServerChunkProvider
					chunkManager.getChunks().forEach((chunkHolder) ->
					{
						Optional<Chunk> optional = chunkHolder.getEntityTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left();
						if (optional.isPresent())
						{
							Chunk chunk = optional.get();
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