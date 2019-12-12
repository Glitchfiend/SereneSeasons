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

import java.util.Iterator;
import java.util.Optional;

@Mod.EventBusSubscriber
public class RandomUpdateHandler
{
	private void adjustWeatherFrequency(World world, Season season)
	{
		if (season == Season.WINTER)
		{
			if (ModConfig.seasons.changeWeatherFrequency)
			{
				if (world.getWorldInfo().isThundering())
				{
					world.getWorldInfo().setThundering(false);
					;
				}
				if (!world.getWorldInfo().isRaining() && world.getWorldInfo().getRainTime() > 36000)
				{
					world.getWorldInfo().setRainTime(world.rand.nextInt(24000) + 12000);
				}
			}
		}
		else
		{
			if (ModConfig.seasons.changeWeatherFrequency)
			{
				if (season == Season.SPRING)
				{
					if (!world.getWorldInfo().isRaining() && world.getWorldInfo().getRainTime() > 96000)
					{
						world.getWorldInfo().setRainTime(world.rand.nextInt(84000) + 12000);
					}
				}
				else if (season == Season.SUMMER)
				{
					if (!world.getWorldInfo().isThundering() && world.getWorldInfo().getThunderTime() > 36000)
					{
						world.getWorldInfo().setThunderTime(world.rand.nextInt(24000) + 12000);
					}
				}
			}
		}
	}

	private void meltInChunk(ChunkManager chunkManager, Chunk chunkIn, Season.SubSeason subSeason)
	{
		ServerWorld world = chunkManager.world;
		ChunkPos chunkpos = chunkIn.getPos();
		int i = chunkpos.getXStart();
		int j = chunkpos.getZStart();
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

		if (world.rand.nextInt(meltRand) == 0)
		{
			BlockPos topAirPos = world.getHeight(Heightmap.Type.MOTION_BLOCKING, world.func_217383_a(i, 0, j, 15));
			BlockPos topGroundPos = topAirPos.down();
			BlockState groundState = world.getBlockState(topGroundPos);
			Biome biome = world.getBiome(topAirPos);

			if (!BiomeConfig.enablesSeasonalEffects(biome))
				return;

			if (groundState.getBlock() == Blocks.SNOW)
			{
				if (SeasonHooks.getBiomeTemperature(world, biome, topGroundPos) >= 0.15F)
				{
					world.setBlockState(topGroundPos, Blocks.AIR.getDefaultState());
				}
			}
			else if (groundState.getBlock() == Blocks.ICE)
			{
				if (SeasonHooks.getBiomeTemperature(world, biome, topGroundPos) >= 0.15F)
				{
					((IceBlock) Blocks.ICE).turnIntoWater(groundState, world, topGroundPos);
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
				if (ModConfig.seasons.generateSnowAndIce && SeasonsConfig.isDimensionWhitelisted(event.world.getDimension().getType().getId()))
				{
					ServerWorld world = (ServerWorld) event.world;
					ChunkManager chunkManager = world.getChunkProvider().chunkManager;

					// Replicate the behaviour of ServerChunkProvider
					chunkManager.func_223491_f().forEach((chunkHolder) ->
					{
						Optional<Chunk> optional = chunkHolder.func_219297_b().getNow(ChunkHolder.UNLOADED_CHUNK).left();
						if (optional.isPresent())
						{
							Chunk chunk = optional.get();
							ChunkPos chunkpos = chunkHolder.getPosition();
							if (!chunkManager.isOutsideSpawningRadius(chunkpos))
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