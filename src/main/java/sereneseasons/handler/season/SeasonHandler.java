/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import java.util.HashMap;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.handler.PacketHandler;
import sereneseasons.network.message.MessageSyncSeasonCycle;
import sereneseasons.season.SeasonASMHelper;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

public class SeasonHandler implements SeasonHelper.ISeasonDataProvider
{
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        World world = event.world;

        if (event.phase == TickEvent.Phase.END && !world.isRemote)
        {
            if (!SyncedConfig.getBooleanValue(SeasonsOption.PROGRESS_SEASON_WHILE_OFFLINE))
            {
                MinecraftServer server = world.getMinecraftServer();
                if (server != null && server.getPlayerList().getCurrentPlayerCount() == 0)
                    return;
            }
                
            SeasonSavedData savedData = getSeasonSavedData(world);

            if (savedData.seasonCycleTicks++ > SeasonTime.ZERO.getCycleDuration())
            {
                savedData.seasonCycleTicks = 0;
            }
            
            if (savedData.seasonCycleTicks % 20 == 0)
            {
                sendSeasonUpdate(world);
            }

            savedData.markDirty();
        }
    }
    
    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        World world = player.world;
        
        sendSeasonUpdate(world);
    }

    private Season.SubSeason lastSeason = null;
    public static final HashMap<Integer, Integer> clientSeasonCycleTicks = new HashMap<>();
    public static SeasonTime getClientSeasonTime() {
        Integer i = clientSeasonCycleTicks.get(0);
    	return new SeasonTime(i == null ? 0 : i);
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) 
    {
        //Only do this when in the world
        if (Minecraft.getInstance().player == null) return;
        DimensionType dimension = Minecraft.getInstance().player.dimension;

        if (event.phase == TickEvent.Phase.END && SeasonsConfig.isDimensionWhitelisted(dimension))
        {
            clientSeasonCycleTicks.compute(dimension, (k, v) -> v == null ? 0 : v + 1);
        	
            //Keep ticking as we're synchronized with the server only every second
            if (clientSeasonCycleTicks.get(dimension) > SeasonTime.ZERO.getCycleDuration())
            {
                clientSeasonCycleTicks.put(dimension, 0);
            }
            
            SeasonTime calendar = new SeasonTime(clientSeasonCycleTicks.get(dimension));
            
            if (calendar.getSubSeason() != lastSeason)
            {
                Minecraft.getInstance().renderGlobal.loadRenderers();
                lastSeason = calendar.getSubSeason();
            }
        }
    }

    @SubscribeEvent
    public void onPopulateChunk(PopulateChunkEvent.Populate event)
    {
        if (!event.getWorld().isRemote && event.getType() != PopulateChunkEvent.Populate.EventType.ICE || !SeasonsConfig.isDimensionWhitelisted(event.getWorld().provider.getDimension()))
            return;

        event.setResult(Event.Result.DENY);
        BlockPos blockpos = new BlockPos(event.getChunkX() * 16, 0, event.getChunkZ() * 16).add(8, 0, 8);
        
        for (int k2 = 0; k2 < 16; ++k2)
        {
            for (int j3 = 0; j3 < 16; ++j3)
            {
                BlockPos blockpos1 = event.getWorld().getPrecipitationHeight(blockpos.add(k2, 0, j3));
                BlockPos blockpos2 = blockpos1.down();

                if (SeasonASMHelper.canBlockFreezeInSeason(event.getWorld(), blockpos2, false, SeasonHelper.getSeasonState(event.getWorld()), true))
                {
                    event.getWorld().setBlockState(blockpos2, Blocks.ICE.getDefaultState(), 2);
                }

                if (SeasonASMHelper.canSnowAtInSeason(event.getWorld(), blockpos1, true, SeasonHelper.getSeasonState(event.getWorld()), true))
                {
                    event.getWorld().setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState(), 2);
                }
            }
        }
    }
    
    public static void sendSeasonUpdate(World world)
    {
        if (!world.isRemote)
        {
            SeasonSavedData savedData = getSeasonSavedData(world);
            PacketHandler.instance.sendToAll(new MessageSyncSeasonCycle(world.provider.getDimension(), savedData.seasonCycleTicks));
        }
    }
    
    public static SeasonSavedData getSeasonSavedData(World world)
    {
        MapStorage mapStorage = world.getPerWorldStorage();
        SeasonSavedData savedData = (SeasonSavedData)mapStorage.getOrLoadData(SeasonSavedData.class, SeasonSavedData.DATA_IDENTIFIER);

        //If the saved data file hasn't been created before, create it
        if (savedData == null)
        {
            savedData = new SeasonSavedData(SeasonSavedData.DATA_IDENTIFIER);
            
            int startingSeason = SyncedConfig.getIntValue(SeasonsOption.STARTING_SUB_SEASON);
            
            if (startingSeason == 0)
            {
            	savedData.seasonCycleTicks = (world.rand.nextInt(12)) * SeasonTime.ZERO.getSubSeasonDuration();
            }
            if (startingSeason > 0)
            {
            	savedData.seasonCycleTicks = (startingSeason - 1) * SeasonTime.ZERO.getSubSeasonDuration();
            }
            
            mapStorage.setData(SeasonSavedData.DATA_IDENTIFIER, savedData);
            savedData.markDirty(); //Mark for saving
        }
        
        return savedData;
    }
    
    //
    // Used to implement getSeasonState in the API
    //
    
    public ISeasonState getServerSeasonState(World world)
    {
        SeasonSavedData savedData = getSeasonSavedData(world);
        return new SeasonTime(savedData.seasonCycleTicks);
    }
    
    public ISeasonState getClientSeasonState()
    {
        Integer i = clientSeasonCycleTicks.get(0);
    	return new SeasonTime(i == null ? 0 : i);
    }
}
