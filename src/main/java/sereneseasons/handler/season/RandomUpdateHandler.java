/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;
import sereneseasons.season.SeasonASMHelper;

public class RandomUpdateHandler
{
    void turnIntoWater(World worldIn, int x, int y, int z)
    {
        if (worldIn.provider.isHellWorld)
        {
            worldIn.setBlockToAir(x, y, z);
        }
        else
        {
            worldIn.setBlock(x, y, z, Blocks.water);
            worldIn.notifyBlockOfNeighborChange(x, y, z, Blocks.water);
        }
    }

    // Randomly melt ice and snow when it isn't winter
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == Phase.END && event.side == Side.SERVER)
        {

            Season.SubSeason subSeason = SeasonHelper.getSeasonState(event.world).getSubSeason();
            Season season = subSeason.getSeason();

            if (season == Season.WINTER)
            {
                if (ModConfig.seasons.changeWeatherFrequency)
                {
                    if (event.world.getWorldInfo().isThundering())
                    {
                        event.world.getWorldInfo().setThundering(false);
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

                if (ModConfig.seasons.generateSnowAndIce && SeasonsConfig.isDimensionWhitelisted(event.world.provider.dimensionId))
                {
                    WorldServer world = (WorldServer) event.world;
                    List chunks = new ArrayList(world.theChunkProviderServer.loadedChunks);
                    for (Iterator<Chunk> iterator = chunks.iterator(); iterator.hasNext();)
                    {
                        Chunk chunk = iterator.next();
                        int x = chunk.xPosition << 4;
                        int z = chunk.zPosition << 4;

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
                            x += randOffset & 15;
                            z += randOffset >> 8 & 15;
                            int yMax = world.getPrecipitationHeight(x, z);
                            BiomeGenBase biome = world.getBiomeGenForCoords(x, z);

                            if (!BiomeConfig.enablesSeasonalEffects(biome))
                                continue;

                            boolean first = true;
                            for (int y = yMax; y >= 0; y--)
                            {
                                Block block = chunk.getBlock(x & 0xF, y, z & 0xF);

                                if (block == Blocks.snow_layer)
                                {
                                    if (SeasonASMHelper.getFloatTemperature(world, biome, x, y, z) >= 0.15F)
                                    {
                                        world.setBlockToAir(x, y, z);
                                        break;
                                    }
                                }

                                if (!first)
                                {
                                    if (block == Blocks.ice)
                                    {
                                        if (SeasonASMHelper.getFloatTemperature(world, biome, x, y, z) >= 0.15F)
                                        {
                                            turnIntoWater(world, x, y, z);
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