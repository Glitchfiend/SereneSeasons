package sereneseasons.season.patcher;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import sereneseasons.util.BinaryHeap.Node;
import sereneseasons.world.chunk.ChunkKey;
import sereneseasons.world.chunk.SeasonChunkData;

class PatchedChunkData {
	private final SeasonChunkData chunkData;
	
    // Volatile properties not stored in SeasonSavedData.
    private boolean isToBePatched;
    private ActiveChunkMarker belongingAC;
    private int notifyNeighborsOnLoadingPopulated;
    
    PatchedChunkData(SeasonChunkData chunkData) {
    	this.chunkData = chunkData;
    	
        this.isToBePatched = false;
        this.belongingAC = null;
        this.notifyNeighborsOnLoadingPopulated = 0;
    }
    
    /**
     * Returns associated chunk data.
     * 
     * @return the chunk data
     */
    public SeasonChunkData getChunkData() {
    	return chunkData;
    }
    
    /**
     * Sets if a neighbor needs to be patched.
     * 
     * @see {@link ChunkKey#NEIGHBORS}
     * @see {@link ChunkPatchingManager#notifyLoadedAndPopulated}
     * @param idx index of the neighbor
     * @param bToSet iff flag is to be set.
     */
    public void setNeighborToNotify(int idx, boolean bToSet)
    {
        if (idx < 0 || idx >= 8)
            throw new IllegalArgumentException("index should be between 0 and 7");
        int bit = 0x1 << idx;
        if (bToSet)
            this.notifyNeighborsOnLoadingPopulated |= bit;
        else
            this.notifyNeighborsOnLoadingPopulated &= ~bit;
    }

    /**
     * Returns if a neighbor needs to be patched.
     * 
     * @see {@link ChunkKey#NEIGHBORS}
     * @see {@link ChunkPatchingManager#notifyLoadedAndPopulated}
     * @param idx index of the neighbor
     * @return <code>true</code> iff yes.
     */
    public boolean isNeighborToBeNotified(int idx)
    {
        if (idx < 0 || idx >= 8)
            throw new IllegalArgumentException("index should be between 0 and 7");
        int bit = 0x1 << idx;
        return (this.notifyNeighborsOnLoadingPopulated & bit) != 0;
    }

    /**
     * Returns if this chunk is enqueued for patching by {@link ChunkPatchingManager}.<br/>
     * <br/>
     * <b>Note</b>: This flag is messy and needs a code revision. 
     * 
     * @param bToBePatched <code>true</code> iff yes.
     */
    void setToBePatched(boolean bToBePatched)
    {
        this.isToBePatched = bToBePatched;
    }

    /**
     * Returns the belonging marker if this chunk is actively updated by {@link WorldServer#updateBlocks()}
     * 
     * @param belongingAC
     */
    void setBelongingAC(ActiveChunkMarker belongingAC)
    {
        this.belongingAC = belongingAC;
    }
    
    /**
     * Returns whether this chunk is enqueued for patching.
     * 
     * @return <code>true</code> iff yes.
     */
    public boolean getIsToBePatched()
    {
        return isToBePatched;
    }

    /**
     * Returns the belonging marker object if the chunk is actively updated by {@link WorldServer#updateBlocks()}
     * 
     * @return the marker object.
     */
    public ActiveChunkMarker getBelongingAC()
    {
        return belongingAC;
    }
    
    /**
     * Creates belonging activity marker.
     * 
     * @param world the world
     */
    ActiveChunkMarker createBelongingAC(World world) {
    	if( belongingAC != null )
    		throw new IllegalStateException("Invalid call of createBelongingAC(). belongingAC is not null.");
    	if( !isAssociatedToWorld(world) )
    		throw new IllegalArgumentException("Incompatible worlds.");
    	
    	return new ActiveChunkMarker(world);
    }

    /**
     * Tells if the chunk is located in the given world.
     * 
     * @param world the world
     * @return <code>true</code> iff chunk is located in the given world
     */
    public boolean isAssociatedToWorld(World world) {
    	return chunkData.isAssociatedToWorld(world);
    }
    
    /////////////////////
    
    /**
     * Used to mark chunks as active and keep tracking them. Patching time stamps for active chunks are
     * always kept up-to-date in {@link ChunkPatchingManager#onServerWorldTick(net.minecraft.world.WorldServer)}.
     */
    class ActiveChunkMarker extends Node<Long>
    {
        private final World world;
        private long lastVisitTime;

        /**
         * The constructor. Called from {@link ChunkPatchingManager#onServerWorldTick(net.minecraft.world.WorldServer)}.
         * 
         * @param data the marked chunk
         * @param world the world
         */
        ActiveChunkMarker(World world)
        {
            this.world = world;
            this.lastVisitTime = 0;

            PatchedChunkData.this.setBelongingAC(this);
        }

        /**
         * Returns the chunk key.
         * 
         * @return the chunk key.
         */
        public ChunkKey getKey()
        {
            return getPatchedChunkData().getChunkData().getKey();
        }

        /**
         * Returns belonging patched chunk data information.
         * 
         * @return patched chunk data
         */
        public PatchedChunkData getPatchedChunkData() {
        	return PatchedChunkData.this;
        }
        
        /**
         * Returns the chunk meta data.
         * 
         * @return the meta data.
         */
        public SeasonChunkData getChunkData()
        {
            return getPatchedChunkData().getChunkData();
        }

        /**
         * Returns the world for the chunk.
         * 
         * @return the world.
         */
        public World getWorld()
        {
            return world;
        }
        
        /**
         * Tells if the chunk is located in the given world.
         * 
         * @param world the world
         * @return <code>true</code> iff chunk is located in the given world
         */
        public boolean isAssociatedToWorld(World world) {
        	return this.world == world;
        }

        /**
         * Returns the time stamp the chunk activity was tested last time.
         * 
         * @return the world.
         */
        public long getLastVisitTime()
        {
            return lastVisitTime;
        }

        /**
         * Used to untrack a chunk of being active. <br/>
         * <br/>
         * <b>Important</b>: Shouldn't be used directly, as the heap of tracked chunks at {@link ChunkPatchingManager}
         * needs to be in synch with this action.
         */
        public void internalUnmark()
        {
        	getPatchedChunkData().setBelongingAC(null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Node<Long> o)
        {
            return Long.compare(this.lastVisitTime, o.getNodeKey());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Long getSmallerKey()
        {
            return -1L;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setNodeKey(Long key)
        {
            this.lastVisitTime = key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getNodeKey()
        {
            return this.lastVisitTime;
        }
    }
}
