package sereneseasons.handler.season;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.season.SeasonChunkPatcher;

/**
 * Handles sets of events related to update chunks in dependence to the seasons. <br/>
 * It holds a reference to {@link SeasonChunkPatcher} and is a central point to glue the patching mechanism
 * with the rest of the mod.
 */
public class SeasonChunkPatchingHandler
{
    private static SeasonChunkPatcher chunkPatcher = new SeasonChunkPatcher();

    /**
     * Event listener to renders statistical information on the chunk
     * patching. Used to discover server performance issues.
     * 
     * @param event the Forge event
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onDebugOverlay(final RenderGameOverlayEvent.Text event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.gameSettings.showDebugInfo)
            {
                event.getLeft().add("" + chunkPatcher.statisticsVisitedActive + " active chunks were revisited.");
                event.getLeft().add("" + chunkPatcher.statisticsAddedToActive + " chunks were marked as active.");
                event.getLeft().add("" + chunkPatcher.statisticsDeletedFromActive + " active chunks were marked as inactive.");
                event.getLeft().add("" + chunkPatcher.statisticsPendingAmount + " chunks enqueued for patching.");
                event.getLeft().add("" + chunkPatcher.statisticsRejectedPendingAmount + " enqueued chunks got rejected.");
            }
        }

    }

    /**
     * Event listener to handle loaded chunks. Enqueues it for patching (e.g. adding water freeze to it in winter).
     * Chunk must be populated before to avoid artifacts, like snow under trees. It is enqueued for patching in {@link #postPopulate}
     * instead if chunk is unpopulated.
     * 
     * @see {@link SeasonChunkPatcher#notifyLoadedAndPopulated} for more details on the patching order.
     * @param event the Forge event
     */
    @SubscribeEvent
    public void onChunkDataLoad(ChunkEvent.Load event)
    {
        if (event.getWorld().isRemote)
            return;

        Chunk chunk = event.getChunk();
        if (chunk.isTerrainPopulated())
        {
            chunkPatcher.enqueueChunkOnce(chunk);
            chunkPatcher.notifyLoadedAndPopulated(chunk.getWorld(), chunk.getPos());
        }
    }

    /**
     * Event listener to handle unloaded chunks. Usually they are removed from patching queue.
     * 
     * @param event the Forge event
     */
    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event)
    {
        if (event.getWorld().isRemote)
            return;

        Chunk chunk = event.getChunk();
        chunkPatcher.onChunkUnload(chunk);
    }

    /**
     * Event listener to handle chunks which got just populated.
     * Enqueues chunk for patching (e.g. adding water freeze to it in winter) in this case.
     * 
     * @see {@link SeasonChunkPatcher#notifyLoadedAndPopulated} for more details on the patching order.
     * @param event the Forge event.
     */
    @SubscribeEvent
    public void onPostPopulate(PopulateChunkEvent.Post event)
    {
        World world = event.getWorld();
        if (world.isRemote)
            return;

        ChunkPos pos = new ChunkPos(event.getChunkX(), event.getChunkZ());
        chunkPatcher.enqueueChunkOnce(world, pos);
        chunkPatcher.notifyLoadedAndPopulated(world, pos);
    }

    /**
     * Event listener to handle server world ticks. Used to find new chunks
     * which became active, as handled by {@link WorldServer#updateBlocks}.
     * 
     * @param event the Forge event.
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        World world = event.world;

        if (event.side == Side.SERVER && (world instanceof WorldServer))
        {
            // NOTE: Should never happen, that world isn't an instance of
            // WorldServer, but just for sure ...
            chunkPatcher.onServerWorldTick((WorldServer) world);
        }
    }

    /**
     * Event listener to handle unloading of a world. Running patching tasks belonging to the world
     * are cleaned up as well.
     * 
     * @param event the Forge event.
     */
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        World world = event.getWorld();
        if (world.isRemote)
            return;

        // Clear loadedChunkQueue
        chunkPatcher.onServerWorldUnload(world);
    }

    /**
     * Event listener to handle server ticks. If called, pending chunks patches are performed.
     * 
     * @param event the Forge event.
     */
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        // Performs pending patching tasks
        chunkPatcher.onServerTick();
    }

    /**
     * Returns an instance of the chunk patcher.
     * 
     * @return the chunk patcher.
     */
    public static SeasonChunkPatcher getSeasonChunkPatcher()
    {
        return chunkPatcher;
    }
}
