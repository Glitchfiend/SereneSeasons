/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import java.util.HashMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
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
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                if (server != null && server.getCurrentPlayerCount() == 0)
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
        World world = player.worldObj;
        
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
        if (Minecraft.getMinecraft().thePlayer == null) return;
        
        int dimension = Minecraft.getMinecraft().thePlayer.dimension;

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
                Minecraft.getMinecraft().renderGlobal.loadRenderers();
                lastSeason = calendar.getSubSeason();
            }
        }
    }

    @SubscribeEvent
    public void onPopulateChunk(PopulateChunkEvent.Populate event)
    {
        if (!event.world.isRemote && event.type != PopulateChunkEvent.Populate.EventType.ICE || !SeasonsConfig.isDimensionWhitelisted(event.world.provider.dimensionId))
            return;

        event.setResult(Event.Result.DENY);
        int x = event.chunkX * 16 + 8;
        int z = event.chunkZ * 16 + 8;
        
        for (int k2 = 0; k2 < 16; ++k2)
        {
            for (int j3 = 0; j3 < 16; ++j3)
            {
                int y = event.world.getPrecipitationHeight(x + k2, z + j3);

                if (SeasonASMHelper.canBlockFreezeInSeason(event.world, x, y - 1, z, false, SeasonHelper.getSeasonState(event.world), true))
                {
                    event.world.setBlock(x, y - 1, z, Blocks.ice);
                }

                if (SeasonASMHelper.canSnowAtInSeason(event.world, x, y, z, true, SeasonHelper.getSeasonState(event.world), true))
                {
                    event.world.setBlock(x, y, z, Blocks.snow_layer);
                }
            }
        }
    }
    
    public static void sendSeasonUpdate(World world)
    {
        if (!world.isRemote)
        {
            SeasonSavedData savedData = getSeasonSavedData(world);
            PacketHandler.instance.sendToAll(new MessageSyncSeasonCycle(world.provider.dimensionId, savedData.seasonCycleTicks));
        }
    }
    
    public static SeasonSavedData getSeasonSavedData(World world)
    {
        MapStorage mapStorage = world.perWorldStorage;
        SeasonSavedData savedData = (SeasonSavedData)mapStorage.loadData(SeasonSavedData.class, SeasonSavedData.DATA_IDENTIFIER);

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
