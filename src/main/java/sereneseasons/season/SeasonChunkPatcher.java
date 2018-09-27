package sereneseasons.season;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.ActiveChunkMarker;
import sereneseasons.season.ChunkData;
import sereneseasons.season.ChunkKey;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.util.BinaryHeap;
import sereneseasons.util.ChunkUtils;
import sereneseasons.api.season.SeasonHelper;

/**
 * A manager to maintain a queue of chunks which require a patching (e.g. adding water freeze or snow in winter).
 */
public class SeasonChunkPatcher
{
	/**
	 * Fixed point threshold for probability. THR_PROB_MAX means probability is 100%.
	 */
    private static final int THR_PROB_MAX = 1000;
    
    /**
     * Amount of ticks to look back in the past at the journal
     * to avoid replaying whole journal when patching generated chunks.
     */
    private static final long RETROSPECTIVE_WINDOW_TICKS = 24000 * 9;

    private int numPatcherPerTick;
    private int awaitTicksBeforeDeactivation;

    public int statisticsVisitedActive;
    public int statisticsAddedToActive;
    public int statisticsDeletedFromActive;
    public int statisticsPendingAmount;
    public int statisticsRejectedPendingAmount;

    /**
     * Secured by multi-threading access
     */
    public HashSet<ChunkKey> pendingChunksMask = new HashSet<ChunkKey>();
    public LinkedList<PendingChunkEntry> pendingChunkList = new LinkedList<PendingChunkEntry>();

    public BinaryHeap<Long, ActiveChunkMarker> activeChunksHeap = new BinaryHeap<Long, ActiveChunkMarker>();

    public SeasonChunkPatcher()
    {
        numPatcherPerTick = SyncedConfig.getIntValue(SeasonsOption.NUM_PATCHES_PER_TICK);
        awaitTicksBeforeDeactivation = SyncedConfig.getIntValue(SeasonsOption.PATCH_TICK_DISTANCE);

        statisticsVisitedActive = 0;
        statisticsAddedToActive = 0;
        statisticsDeletedFromActive = 0;
        statisticsPendingAmount = 0;
        statisticsRejectedPendingAmount = 0;
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
        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
        ChunkData chunkData = seasonData.getStoredChunkData(world, chunkPos, false);
        if (chunkData != null)
        {
            // Notify all listening neighbors
            for (int i = 0; i < ChunkKey.NEIGHBORS.length; i++)
            {
                if (!chunkData.isNeighborToBeNotified(i))
                    continue;
                ChunkPos nbPos = ChunkKey.NEIGHBORS[i].getOffset(chunkPos);

                // re enqueue for patching
                enqueueChunkOnce(world, nbPos);
                chunkData.setNeighborToNotify(i, false);
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
    private void internalUntrackFromActive(ChunkData chunkData)
    {
        ActiveChunkMarker ac = chunkData.getBelongingAC();
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

        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);

        // Iterate through actively updated chunks to enqueue them for patching
        // and begin tracking them
        statisticsVisitedActive = 0;
        statisticsAddedToActive = 0;
        Iterator<Chunk> iter = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator());
        while (iter.hasNext())
        {
            Chunk activeChunk = iter.next();
            ChunkData chunkData = seasonData.getStoredChunkData(activeChunk, true);

            ActiveChunkMarker ac = chunkData.getBelongingAC();
            if (ac == null)
            {
                // Roll up patches
                enqueueChunkOnce(activeChunk);
                chunkData.setToBePatched(true);

                // Tag as active and as awaiting to be patched
                ac = new ActiveChunkMarker(chunkData, world);

                statisticsAddedToActive++;
            }
            else if (!chunkData.getIsToBePatched())
            {
                // For an active chunk (having no pending patching)
                // the time is always actual
                chunkData.setPatchTimeUptodate();
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
        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(chunk.getWorld());
        ChunkData data = seasonData.getStoredChunkData(chunk, false);
        if (data != null) {
            internalUntrackFromActive(data);
            data.setToBePatched(false);
        }
    }

    /**
     * Is called by {@link sereneseasons.handler.season.SeasonChunkPatchingHandler#onWorldUnload}. <br/>
     * Used to cleanup all pending patching jobs for the specific world. All chunks marked as active are untracked as well. 
     *   
     * @param world the world.
     */
    public void onServerWorldUnload(World world)
    {
        // Clear loadedChunkQueue
        Iterator<PendingChunkEntry> iter = pendingChunkList.iterator();
        while (iter.hasNext())
        {
            PendingChunkEntry entry = iter.next();
            if (entry.getWorld() == world)
            {
                pendingChunksMask.remove(entry.getKey());
                iter.remove();
            }
        }

        // Clear active chunk tracking for the world
        LinkedList<ActiveChunkMarker> chunksRetainedActive = new LinkedList<ActiveChunkMarker>();
        for (ActiveChunkMarker ac : activeChunksHeap)
        {
            if (ac.getWorld() == world)
                ac.internalUnmark();
            else
            	chunksRetainedActive.add(ac);
        }

        activeChunksHeap.clear();
        for (ActiveChunkMarker ac : chunksRetainedActive)
        {
            activeChunksHeap.add(ac);
        }
    }

    /**
     * Server tick handler to consume queue for patching as well as handling other tasks like untracking active chunks.
     */
    public void onServerTick()
    {
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

        pendingChunkList = new LinkedList<PendingChunkEntry>();

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
            SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(chunk.getWorld());
            ChunkData chunkData = seasonData.getStoredChunkData(chunk, true);

            // Check for unloaded. Reject if so
            if (!chunk.isLoaded())
            {
                internalUntrackFromActive(chunkData);
                chunkData.setToBePatched(false);

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
                    ChunkData nbChunkData = seasonData.getStoredChunkData(nbKey, true);

                    nbChunkData.setNeighborToNotify(oppositeI, true);
                }

                internalUntrackFromActive(chunkData);
                chunkData.setToBePatched(false);

                statisticsRejectedPendingAmount++;
                continue;
            }

            // Perform a chunk patch and clear to-be-patched flag.
            patchChunkTerrain(chunkData);
            chunkData.setToBePatched(false);
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
        // TODO: Remove as soon as neighbor checks are not cascading anymore!
        if (chunksInProcess.size() > 0)
        {
            chunksInProcess.addAll(pendingChunkList);
            pendingChunkList = chunksInProcess;
        }
    }

    /**
     * Patches the actual chunk by replaying the journal on it
     * depending on the passed time this chunk has been patched before, if ever. 
     * 
     * @param chunkData the actual chunk.
     */
    private void patchChunkTerrain(ChunkData chunkData)
    {
        Chunk chunk = chunkData.getChunk();
        World world = chunk.getWorld();

        Season season = SeasonHelper.getSeasonState(world).getSubSeason().getSeason();
        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);

        long lastPatchedTime = chunkData.getLastPatchedTime();
        boolean bFastForward = false;
        long windowBorder = world.getTotalWorldTime() - RETROSPECTIVE_WINDOW_TICKS;
        if (lastPatchedTime < windowBorder)
        {
            // Old entries have no effect. Considering it by reseting chunk snow
            // states and patch from newer journal entries only
            lastPatchedTime = windowBorder;
            bFastForward = true;
        }
        int fromIdx = seasonData.getJournalIndexAfterTime(lastPatchedTime);

        // determine initial state
        boolean bWasRaining = seasonData.wasLastRaining(fromIdx);
        boolean bWasCold = seasonData.wasLastCold(fromIdx);

        long rainingTrackTicks = 0;
        long coldTrackTicks = 0;

        long intervalRainingTrackStart = lastPatchedTime;
        long intervalSnowyTrackStart = lastPatchedTime;

        // initialize in case of fast forward
        if (bFastForward)
        {
            if (bWasCold)
                executePatchCommand(4, 0, chunk);  // 4 = set all snow/frozen
            else
                executePatchCommand(5, 0, chunk);  // 5 = set all molten
        }

        // Replay latest journal entries
        if (fromIdx != -1)
        {
            int command = 0; // 0 = NOP

            // Apply events from journal
            for (int curEntry = fromIdx; curEntry < seasonData.journal.size(); curEntry++)
            {
                WeatherJournalEvent wevt = seasonData.journal.get(curEntry);

                rainingTrackTicks = wevt.getTimeStamp() - intervalRainingTrackStart;
                coldTrackTicks = wevt.getTimeStamp() - intervalSnowyTrackStart;

                switch (wevt.getEventType())
                {
                    case EVENT_START_RAINING:
                        if (!bWasRaining)
                        {
                            intervalRainingTrackStart = wevt.getTimeStamp();
                            command = 0;  // 0 = NOP
                            bWasRaining = true;
                        }
                        break;
                    case EVENT_STOP_RAINING:
                        if (bWasRaining)
                        {
                            intervalRainingTrackStart = wevt.getTimeStamp();
                            if (bWasCold)
                                command = 2; // 2 = simulate chunk snow (requires duration parameter in ticks)
                            else
                                command = 0; // 0 = NOP
                            bWasRaining = false;
                        }
                        break;
                    case EVENT_TO_COLD_SEASON:
                        if (!bWasCold)
                        {
                            intervalSnowyTrackStart = wevt.getTimeStamp();
                            command = 3; // 3 = simulate melting (requires duration parameter in ticks)
                            bWasCold = true;
                        }
                        break;
                    case EVENT_TO_WARM_SEASON:
                        if (bWasCold)
                        {
                            intervalSnowyTrackStart = wevt.getTimeStamp();
                            if (bWasRaining)
                                command = 2; // 2 = simulate chunk snow (requires duration parameter in ticks).
                            else
                                command = 1; // 1 = simulate freeze only (requires duration parameter in ticks).
                            bWasCold = false;
                        }
                        break;
                    default:
                        // Do nothing
                        command = 0;  // 0 = NOP
                }

                executePatchCommand(command, coldTrackTicks, rainingTrackTicks, chunk);
            }
        }

        // Post update for running events
        rainingTrackTicks = world.getTotalWorldTime() - intervalRainingTrackStart;
        coldTrackTicks = world.getTotalWorldTime() - intervalSnowyTrackStart;

        if (seasonData.wasLastRaining(-1) && seasonData.wasLastCold(-1))
        {
            executePatchCommand(2, coldTrackTicks, rainingTrackTicks, chunk);
        }
        else if (!seasonData.wasLastCold(-1))
        {
            executePatchCommand(3, coldTrackTicks, rainingTrackTicks, chunk);
        }
        else
        {
            executePatchCommand(1, coldTrackTicks, rainingTrackTicks, chunk);
        }

        chunkData.setPatchTimeUptodate();
    }
    
    /**
     * Executes a patching action on a chunk.<br/>
     * Commands have following meaning:<br/>
     * <ul>
     *  <li>0 = NOP. Do nothing.</li>
     *  <li>1 = simulate freeze only.</li>
     *  <li>2 = simulate chunk snow (requires duration parameter in ticks).</li>
     *  <li>3 = simulate melting (requires duration parameter in ticks).</li>
     *  <li>4 = set all snow/frozen.</li>
     *  <li>5 = set all molten.</li>
     * </ul>
     * 
     * @param command the action itself
     * @param coldTrackTicks amount of ticks the weather was cold (resulting to freezing)
     * @param rainingTrackTicks amount of ticks the weather was rainy/snowy (resulting to snowing if cold)
     * @param chunk the patched chunk
     */
    private void executePatchCommand(int command, long coldTrackTicks, long rainingTrackTicks, Chunk chunk)
    {
        if (command != 0)
        {
            int threshold = 0;
            if (command == 2)
            {
                long dur = rainingTrackTicks;
                if (dur > coldTrackTicks)
                    dur = coldTrackTicks;
                threshold = evalProbPerBlockUpdateForTicks((int) dur);
            }
            else if (command == 1 || command == 3)
                threshold = evalProbPerBlockUpdateForTicks((int) coldTrackTicks);
            executePatchCommand(command, threshold, chunk);
        }
    }
    
    /**
     * executes a patching action on a chunk.<br/>
     * Commands have following meaning:<br/>
     * <ul>
     *  <li>0 = NOP. Do nothing.</li>
     *  <li>1 = simulate freeze only.</li>
     *  <li>2 = simulate chunk snow (requires duration parameter in ticks).</li>
     *  <li>3 = simulate melting (requires duration parameter in ticks).</li>
     *  <li>4 = set all snow/frozen.</li>
     *  <li>5 = set all molten.</li>
     * </ul>
     * 
     * @param command the action itself
     * @param threshold duration in ticks, if needed
     * @param chunk the patched chunk
     */
    private void executePatchCommand(int command, int threshold, Chunk chunk)
    {
        // TODO: Maybe improve client notification on block changes at setBlockState calls if performance issues are occurring!

        ChunkPos chunkPos = chunk.getPos();
        World world = chunk.getWorld();

        if (command == 4 || command == 5)
        {
            threshold = THR_PROB_MAX;
        }

        MutableBlockPos pos = new MutableBlockPos();
        for (int iX = 0; iX < 16; iX++)
        {
            for (int iZ = 0; iZ < 16; iZ++)
            {
                int height = chunk.getHeightValue(iX, iZ);
                pos.setPos(chunkPos.getXStart() + iX, height, chunkPos.getZStart() + iZ);

                BlockPos below = pos.down();

                if ((command == 1 || command == 2 || command == 4))
                {
                    // Apply snow in dependence of last rain time and apply ice
                    // in dependence of last time the season changed to cold
                    // (where canSnowAtTempInSeason have returned false before).
                    if (world.rand.nextInt(THR_PROB_MAX) < threshold)
                    {
                        if (SeasonASMHelper.canBlockFreezeInSeason(world, below, false, SubSeason.EARLY_WINTER.getDefaultState()))
                        {
                            // NOTE: Is a simplified freeze behavior
                            world.setBlockState(below, Blocks.ICE.getDefaultState(), 2);
                        }
                        else if (command != 1 && SeasonASMHelper.canSnowAtInSeason(world, pos, true, SubSeason.EARLY_WINTER.getDefaultState()))
                        {
                            world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState(), 2);
                        }
                    }
                    // TODO: Simulate crop death
                }
                else if (command == 3 || command == 5)
                {
                    // Remove snow and ice in dependence of last time the season
                    // changed to cold (where canSnowAtTempInSeason have
                    // returned true before).
                    if (world.rand.nextInt(THR_PROB_MAX) <= threshold * 10)
                    {
                        IBlockState blockState = world.getBlockState(pos);
                        if (blockState.getBlock() == Blocks.SNOW_LAYER)
                        {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                        }
                        else
                        {
                            blockState = world.getBlockState(below);
                            if (blockState.getBlock() == Blocks.ICE)
                            {
                                world.setBlockState(below, Blocks.WATER.getDefaultState(), 2);
                                world.neighborChanged(below, Blocks.WATER, below);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Evals probability per block, an action is performed.
     * 
     * @param duringTicks duration in ticks. More means, higher probability.
     * @return the probability in the interval. (0, THR_PROB_MAX]
     */
    private int evalProbPerBlockUpdateForTicks(int duringTicks)
    {
        final double fieldHitProb = 1.0 / (16.0 * 16.0);
        final double snowUpdateProbInTick = 1.0 / 16.0;
        final double correctionFactor = 0.75;
        final double hitProb = correctionFactor * fieldHitProb * snowUpdateProbInTick;
        final double missProb = 1.0 - hitProb;
        double prob = hitProb * (1.0 - Math.pow(missProb, duringTicks + 1)) / (1.0 - missProb);

        return (int) ((double) THR_PROB_MAX * prob + 0.5);
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
