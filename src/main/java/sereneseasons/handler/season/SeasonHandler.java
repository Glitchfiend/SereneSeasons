/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.handler.PacketHandler;
import sereneseasons.network.message.MessageSyncSeasonCycle;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

public class SeasonHandler implements SeasonHelper.ISeasonDataProvider
{
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        World world = event.world;

        if (event.phase == TickEvent.Phase.END && !world.isRemote && world.provider.getDimension() == 0)
        {
            SeasonSavedData savedData = getSeasonSavedData(world);
            Season season = SeasonHelper.getSeasonState(world).getSubSeason().getSeason();

            if (savedData.seasonCycleTicks++ > SeasonTime.ZERO.getCycleDuration())
            {
                savedData.seasonCycleTicks = 0;
            }

            if (savedData.seasonCycleTicks % 20 == 0)
            {
                sendSeasonUpdate(world);
            }

            savedData.updateJournal(world, season);

            savedData.markDirty();
        }
    }

    @SubscribeEvent
    public void onWorldUnloaded(WorldEvent.Unload event)
    {
        World world = event.getWorld();
        if (world.isRemote)
            return;

        // Season data cleanup
        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
        seasonData.onWorldUnload(world);
    }

    @SubscribeEvent
    public void onChunkUnloaded(ChunkEvent.Unload event)
    {
        if (event.getWorld().isRemote)
            return;

        Chunk chunk = event.getChunk();

        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(chunk.getWorld());
        seasonData.onChunkUnloaded(chunk);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        World world = player.world;

        sendSeasonUpdate(world);
    }

    private SubSeason lastSeason = null;
    public static int clientSeasonCycleTicks = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        // Only do this when in the world
        if (Minecraft.getMinecraft().player == null)
            return;

        int dimension = Minecraft.getMinecraft().player.dimension;

        if (event.phase == TickEvent.Phase.END && dimension == 0 )
        {
            // Keep ticking as we're synchronized with the server only every
            // second
            if (clientSeasonCycleTicks++ > SeasonTime.ZERO.getCycleDuration())
            {
                clientSeasonCycleTicks = 0;
            }

            SeasonTime calendar = new SeasonTime(clientSeasonCycleTicks);

            if (calendar.getSubSeason() != lastSeason)
            {
                Minecraft.getMinecraft().renderGlobal.loadRenderers();
                lastSeason = calendar.getSubSeason();
            }
        }
    }

    public static void sendSeasonUpdate(World world)
    {
        if (!world.isRemote )
        {
            SeasonSavedData savedData = getSeasonSavedData(world);
            PacketHandler.instance.sendToAll(new MessageSyncSeasonCycle(savedData.seasonCycleTicks));
        }
    }

    public static SeasonSavedData getSeasonSavedData(World world)
    {
        MapStorage mapStorage = world.getPerWorldStorage();
        SeasonSavedData savedData = (SeasonSavedData) mapStorage.getOrLoadData(SeasonSavedData.class, SeasonSavedData.DATA_IDENTIFIER);

        // If the saved data file hasn't been created before, create it
        if (savedData == null)
        {
            savedData = new SeasonSavedData(SeasonSavedData.DATA_IDENTIFIER);
            mapStorage.setData(SeasonSavedData.DATA_IDENTIFIER, savedData);
            savedData.markDirty(); // Mark for saving
        }

        return savedData;
    }

    //
    // Used to implement getSeasonData in the API
    //

    public ISeasonState getServerSeasonState(World world)
    {
        SeasonSavedData savedData = getSeasonSavedData(world);
        return new SeasonTime(savedData.seasonCycleTicks);
    }

    public ISeasonState getClientSeasonState()
    {
        return new SeasonTime(clientSeasonCycleTicks);
    }
}
