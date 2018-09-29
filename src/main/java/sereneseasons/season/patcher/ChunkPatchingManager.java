package sereneseasons.season.patcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.season.patcher.PatchedChunkData.ActiveChunkMarker;
import sereneseasons.util.BinaryHeap;
import sereneseasons.util.ChunkUtils;
import sereneseasons.world.chunk.ChunkKey;
import sereneseasons.world.chunk.SeasonChunkData;
import sereneseasons.world.chunk.SeasonChunkManager;

/**
 * A manager to maintain a queue of chunks which require a patching (e.g. adding water freeze or snow in winter).
 */
public class ChunkPatchingManager
{
	private ChunkPatcher patcher = new ChunkPatcher(); 
	
//    private int numPatcherPerTick;
//    private int awaitTicksBeforeDeactivation;

    public int statisticsVisitedActive;
    public int statisticsAddedToActive;
    public int statisticsDeletedFromActive;
    public int statisticsPendingAmount;
    public int statisticsRejectedPendingAmount;

    public HashMap<ChunkKey, PatchedChunkData> chunkDataMap = new HashMap<ChunkKey, PatchedChunkData>();
    
    public HashSet<ChunkKey> pendingChunksMask = new HashSet<ChunkKey>();
    public LinkedList<PendingChunkEntry> pendingChunkList = new LinkedList<PendingChunkEntry>();

    public BinaryHeap<Long, ActiveChunkMarker> activeChunksHeap = new BinaryHeap<Long, ActiveChunkMarker>();

    /**
     * The constructor.
     */
    public ChunkPatchingManager()
    {
        statisticsVisitedActive = 0;
        statisticsAddedToActive = 0;
        statisticsDeletedFromActive = 0;
        statisticsPendingAmount = 0;
        statisticsRejectedPendingAmount = 0;
    }
    
    /**
     * Returns back additional information about the chunk required for patching.
     * 
     * @param data the chunk meta data
     * @return the additional information
     */
    public PatchedChunkData getPatchedChunkData(SeasonChunkData data) {
    	ChunkKey key = data.getKey();
    	PatchedChunkData pcd = chunkDataMap.get(data.getKey());
    	if( pcd == null ) {
    		pcd = new PatchedChunkData(data);
    		chunkDataMap.put(key, pcd);
    	}
    	return pcd;
    }

    /**
     * Is used to submit neighbor chunks for patching. <br/>
     * <br/>
     * <b>Conditions to met when patching</b>: It is necessary to ensure that
     * a chunk is patched only after it is populated itself as well as a set of neighbors. Structures may generated
     * at neighbors during population which would overlap the chunk itself, like trees or village huts a.s.o.
     * This logic is used simply to reduce artifacts with snow which would stay under the overlaps, like snow under trees. 
     * 
     * @param world world of the chunk
     * @param chunkPos position of the chunk
     */
    public void notifyLoadedAndPopulated(World world, ChunkPos chunkPos)
    {
        SeasonChunkData chunkData = SeasonChunkManager.INSTANCE.getStoredChunkData(world, chunkPos, false);
        if (chunkData != null)
        {
        	PatchedChunkData pcd = getPatchedChunkData(chunkData);
        	
            // Notify all listening neighbors
            for (int i = 0; i < ChunkKey.NEIGHBORS.length; i++)
            {
                if (!pcd.isNeighborToBeNotified(i))
                    continue;
                ChunkPos nbPos = ChunkKey.NEIGHBORS[i].getOffset(chunkPos);

                // re enqueue for patching
                enqueueChunkOnce(world, nbPos);
                pcd.setNeighborToNotify(i, false);
            }
        }
    }

    /**
     * Enqueues a chunk for patching. Will get processed at {@link #onServerTick()}.
     * 
     * @param chunk the chunk
     */
    public void enqueueChunkOnce(Chunk chunk)
    {
        ChunkKey key = new ChunkKey(chunk.getPos(), chunk.getWorld());
        if (pendingChunksMask.contains(key))
            return;
        pendingChunksMask.add(key);
        pendingChunkList.add(new PendingChunkEntry(chunk));
    }

    /**
     * Like {@link #enqueueChunkOnce(Chunk)}, enqueues a chunk for patching.
     * May be used alternatively if the loading state of the actual chunk
     * is not known or chunk is not loaded. 
     * 
     * @param world the world managing the chunk
     * @param chunkPos the position of the chunk within the world
     */
    public void enqueueChunkOnce(World world, ChunkPos chunkPos)
    {
        ChunkKey key = new ChunkKey(chunkPos, world);
        if (pendingChunksMask.contains(key))
            return;
        pendingChunksMask.add(key);
        pendingChunkList.add(new PendingChunkEntry(key, world));
    }

    /**
     * Adds chunk to patching if generated. <br/>
     * 
     * @deprecated Is not used anymore.
     * @param world the world managing the chunk
     * @param pos the position of the chunk within the world
     */
    @Deprecated
    private void addChunkIfGenerated(World world, ChunkPos pos)
    {
        if (!world.isChunkGeneratedAt(pos.x, pos.z))
            return;
        enqueueChunkOnce(world, pos);
    }

    /**
     * Adds neighbor chunk to patching if generated. <br/>
     * 
     * @deprecated Is not used anymore.
     * @param world the world managing the chunk
     * @param pos the position of the chunk within the world
     */
    @Deprecated
    public void enqueueGeneratedNeighborChunks(World world, ChunkPos pos)
    {
        for (ChunkKey.Neighbor nb : ChunkKey.NEIGHBORS)
        {
            addChunkIfGenerated(world, nb.getOffset(pos));
        }
    }
    
    /**
     * Untracks a chunk from being active. Is used internally.
     * 
     * @param chunkData the chunk to untrack.
     */
    private void internalUntrackFromActive(PatchedChunkData pcd)
    {
        ActiveChunkMarker ac = pcd.getBelongingAC();
        if (ac != null)
        {
            activeChunksHeap.remove(ac);
            ac.internalUnmark();
        }
    }

    /**
     * Is called by {@link sereneseasons.handler.season.SeasonChunkPatchingHandler#onWorldTick}. <br/>
     * Used to mark chunks as active by same logic as {@link WorldServer#updateBlocks} is iterating through chunks.
     * Patching time stamps for active chunks are kept up to date.
     * 
     * @param world the server world
     */
    public void onServerWorldTick(WorldServer world)
    {
    	// IMPORTANT: When porting mod to newer Minecraft versions,
    	//            please make sure that logic is in synch with WorldServer.updateBlocks()
    	//            to retrieve all actively updated chunks as Minecraft does
    	
        world.profiler.startSection("seasonChunkFind");

        // Iterate through actively updated chunks to enqueue them for patching
        // and begin tracking them
        statisticsVisitedActive = 0;
        statisticsAddedToActive = 0;
        Iterator<Chunk> iter = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator());
        while (iter.hasNext())
        {
            Chunk activeChunk = iter.next();
            SeasonChunkData chunkData = SeasonChunkManager.INSTANCE.getStoredChunkData(activeChunk, true);
            PatchedChunkData pcd = getPatchedChunkData(chunkData);

            ActiveChunkMarker ac = pcd.getBelongingAC();
            if (ac == null)
            {
                // Roll up patches
                enqueueChunkOnce(activeChunk);
                pcd.setToBePatched(true);

                // Tag as active and as awaiting to be patched
                ac = pcd.createBelongingAC(world);

                statisticsAddedToActive++;
            }
            else if (!pcd.getIsToBePatched())
            {
                // For an active chunk (having no pending patching)
                // the time is always actual
                chunkData.setPatchTimeTo(world.getTotalWorldTime());
            }

            activeChunksHeap.remove(ac);
            ac.setNodeKey(world.getTotalWorldTime());
            activeChunksHeap.add(ac);

            statisticsVisitedActive++;
        }

        world.profiler.endSection();
    }

    /**
     * Is called by {@link sereneseasons.handler.season.SeasonChunkPatchingHandler#onChunkUnload}. <br/>
     * Event listener to handle unloaded chunks. They are untracked from being active.
     * 
     * @param event the Forge event
     */
    public void onChunkUnload(Chunk chunk)
    {
        SeasonChunkData data = SeasonChunkManager.INSTANCE.getStoredChunkData(chunk, false);
        if (data != null) {
        	PatchedChunkData pcd = getPatchedChunkData(data);
        	
            internalUntrackFromActive(pcd);
            pcd.setToBePatched(false);
        }
    }

    /**
     * Is called by {@link sereneseasons.handler.season.SeasonChunkPatchingHandler#onWorldUnload}. <br/>
     * Used to cleanup all pending patching jobs for the specific world. All chunks marked as active are untracked as well. 
     *   
     * @param world the world.
     */
    public void cleanupOnServerWorldUnload(World world)
    {
        // Clear loadedChunkQueue
        Iterator<PendingChunkEntry> pcIter = pendingChunkList.iterator();
        while (pcIter.hasNext())
        {
            PendingChunkEntry entry = pcIter.next();
            if (entry.getWorld() == world)
            {
                pendingChunksMask.remove(entry.getKey());
                pcIter.remove();
            }
        }

        // Clear active chunk tracking for the world
        LinkedList<ActiveChunkMarker> chunksRetainedActive = new LinkedList<ActiveChunkMarker>();
        for (ActiveChunkMarker ac : activeChunksHeap)
        {
            if (ac.isAssociatedToWorld(world) )
                ac.internalUnmark();
            else
            	chunksRetainedActive.add(ac);
        }

        activeChunksHeap.clear();
        for (ActiveChunkMarker ac : chunksRetainedActive)
        {
            activeChunksHeap.add(ac);
        }
        
    	// Remove all patched chunk data associated with this world
        Iterator<Map.Entry<ChunkKey, PatchedChunkData>> pcdIter = chunkDataMap.entrySet().iterator();
        while( pcdIter.hasNext() ) {
        	Map.Entry<ChunkKey, PatchedChunkData> pcdEntry = pcdIter.next();
        	PatchedChunkData pcd = pcdEntry.getValue();
        	SeasonChunkData data = pcd.getChunkData();
        	if( data.getKey().isAssociatedToWorld(world) )
        		pcdIter.remove();
        }
    }

    /**
     * Server tick handler to consume queue for patching as well as handling other tasks like untracking active chunks.
     */
    public void onServerTick()
    {
        int numPatcherPerTick = SyncedConfig.getIntValue(SeasonsOption.NUM_PATCHES_PER_TICK);
        int awaitTicksBeforeDeactivation = SyncedConfig.getIntValue(SeasonsOption.PATCH_TICK_DISTANCE);

        LinkedList<PendingChunkEntry> chunksInProcess = pendingChunkList;
        statisticsDeletedFromActive = 0;

        // Iterate through loaded chunks to untrack chunks marked as active which weren't
        // visited by onServerWorldTick() for a longer time
        while (true)
        {
            ActiveChunkMarker ac = activeChunksHeap.peek();
            if (ac == null)
                break;

            // Wait for discount and then remove
            World world = ac.getWorld();
            if (ac.getLastVisitTime() + awaitTicksBeforeDeactivation <= world.getTotalWorldTime())
            {
                ac.internalUnmark();
                activeChunksHeap.remove(ac);

                statisticsDeletedFromActive++;
            }
            else
                break;
        }

        pendingChunkList = new LinkedList<PendingChunkEntry>();	// This construct is required, because
        														// chunks are associated to their SeasonSavedData pendant
        														// on lazy loading when loading/generating neighbors

        statisticsPendingAmount = chunksInProcess.size();
        statisticsRejectedPendingAmount = 0;

        // Process patching queue. Reject a chunk from patching which got unloaded
        //     or a neighbor is unpopulated (see javadoc of method notifyLoadedAndPopulated for reason, why). 
        int numProcessed = 0;
        for (PendingChunkEntry entry : chunksInProcess)
        {
        	// Only patch a maximal amount of chunks per tick to avoid server lags.
            if (numProcessed >= numPatcherPerTick)
                break;
            numProcessed++;

            Chunk chunk = entry.getChunk();
            SeasonChunkData chunkData = SeasonChunkManager.INSTANCE.getStoredChunkData(chunk, true);
            PatchedChunkData pcd = getPatchedChunkData(chunkData);

            // Check for unloaded. Reject if so
            if (!chunk.isLoaded())
            {
                internalUntrackFromActive(pcd);
                pcd.setToBePatched(false);

                statisticsRejectedPendingAmount++;
                continue;
            }

            // Check for unpopulated neighbors. Reject if so
            ChunkPos chunkPos = chunk.getPos();
            World world = chunk.getWorld();
            int unavailableChunkMask = ChunkUtils.identifyUnloadedOrUnpopulatedNeighbors(world, chunkPos);
            if (unavailableChunkMask != 0)
            {
                // Set on waiting and notification list.
                for (int i = 0; i < ChunkKey.NEIGHBORS.length; i++)
                {
                    int bit = 0x1 << i;
                    if ((unavailableChunkMask & bit) == 0)
                        continue;

                    ChunkKey nbKey = ChunkKey.NEIGHBORS[i].getOffset(chunkData.getKey());
                    int oppositeI = ChunkKey.NEIGHBORS[i].getOppositeIdx();
                    SeasonChunkData nbChunkData = SeasonChunkManager.INSTANCE.getStoredChunkData(nbKey, true);
                    PatchedChunkData nbPcd = getPatchedChunkData(nbChunkData);

                    nbPcd.setNeighborToNotify(oppositeI, true);
                }

                internalUntrackFromActive(pcd);
                pcd.setToBePatched(false);

                statisticsRejectedPendingAmount++;
                continue;
            }

            // Perform a chunk patch and clear to-be-patched flag.
            patcher.patchChunkTerrain(chunk, chunkData);
            pcd.setToBePatched(false);
        }

        // Remove all processed chunks from list within the lock
        // First drop all processed chunks
        for (int i = 0; i < numProcessed; i++)
        {
            PendingChunkEntry chunkEntry = chunksInProcess.getFirst();
            pendingChunksMask.remove(chunkEntry.getKey());
            chunksInProcess.removeFirst();
        }

        // reinsert unprocessed entries to queue
        if (chunksInProcess.size() > 0)
        {
            chunksInProcess.addAll(pendingChunkList);
            pendingChunkList = chunksInProcess;
        }
    }

    
    ////////////////////

    /**
     * An entry to point to pending chunks for patching. Covers case, the actual chunk is not known.
     */
    private static class PendingChunkEntry
    {
        private final ChunkKey key;
        private final World world;
        private Chunk chunk;

        /**
         * Constructor which is called if the chunk is known.
         * 
         * @param chunk the chunk.
         */
        public PendingChunkEntry(Chunk chunk)
        {
            this.world = chunk.getWorld();
            this.key = new ChunkKey(chunk.getPos(), this.world);
            this.chunk = chunk;
        }

        /**
         * Constructor which is called if chunk is not known to be loaded.
         * 
         * @param key a key uniquely identifying the chunk on the server.
         * @param world the world the chunk is in.
         */
        public PendingChunkEntry(ChunkKey key, World world)
        {
            this.key = key;
            this.world = world;
            this.chunk = null;
        }

        /**
         * Returns a hashable key identifying a chunk on the server.
         * 
         * @return the key.
         */
        public ChunkKey getKey()
        {
            return this.key;
        }

        /**
         * Returns the world of the chunk.
         * 
         * @return the world.
         */
        public World getWorld()
        {
            return this.world;
        }

        /**
         * Gets the chunk. Performs a lazy loading if chunk is not loaded.
         * 
         * @return the chunk.
         */
        public Chunk getChunk()
        {
            if (chunk == null)
            {
                chunk = world.getChunkFromChunkCoords(key.getPos().x, key.getPos().z);
            }
            return chunk;
        }
    }
}
