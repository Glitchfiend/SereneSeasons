package sereneseasons.season;

import net.minecraft.world.chunk.Chunk;

/**
 * Stores additional meta data for a chunk used by seasons, like time stamps when the chunk has been patched lately. 
 */
public class ChunkData
{
	// Reference to chunk
    private final ChunkKey key;
    private Chunk chunk;

    // Is stored by SeasonSavedData.
    private long lastPatchedTime;
    
    // Volatile properties not stored in SeasonSavedData.
    private boolean isToBePatched;
    private ActiveChunkMarker belongingAC;
    private int notifyNeighborsOnLoadingPopulated;

    /**
     * The constructor.
     * 
     * @param key key for the chunk
     * @param chunk the chunk. May be <code>null</code> if chunk is not known/loaded.
     * @param lastPatchedTime last time the chunk has been patched.
     */
    public ChunkData(ChunkKey key, Chunk chunk, long lastPatchedTime)
    {
        this.key = key;
        this.chunk = chunk;
        this.lastPatchedTime = lastPatchedTime;
        this.isToBePatched = false;
        this.belongingAC = null;
        this.notifyNeighborsOnLoadingPopulated = 0;
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
     * Returns if this chunk is enqueued for patching by {@link ChunkPatchingManager}
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
    public void setBelongingAC(ActiveChunkMarker belongingAC)
    {
        this.belongingAC = belongingAC;
    }

    /**
     * Attaches a loaded chunk object to this meta data. 
     * 
     * @param chunk the chunk
     */
    void attachLoadedChunk(Chunk chunk)
    {
        if (chunk == null)
            throw new IllegalArgumentException("chunk must be non null. Use clearLoadedChunk() for other case.");
        this.chunk = chunk;
    }
    
    /**
     * Detaches the chunk from this meta data, if it got unloaded.
     */
    void detachLoadedChunk()
    {
        setToBePatched(false);
        this.chunk = null;
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
     * Returns the chunk key.
     * 
     * @return the chunk key.
     */
    public ChunkKey getKey()
    {
        return key;
    }

    /**
     * Returns the chunk object or <code>null</code> if it is not known.
     * 
     * @return the chunk object.
     */
    public Chunk getChunk()
    {
        return chunk;
    }

    /**
     * Sets the time stamp this chunk has been patched to recent time.
     */
    void setPatchTimeUptodate()
    {
        if (chunk != null)
            this.lastPatchedTime = chunk.getWorld().getTotalWorldTime();
    }

    /**
     * Sets the time stamp this chunk has been patched to a specific time.
     * 
     * @param lastPatchedTime the specific time.
     */
    void setPatchTimeTo(long lastPatchedTime)
    {
        this.lastPatchedTime = lastPatchedTime;
    }

    /**
     * Returns the time stamp this chunk has been patched last time.
     * 
     * @return the time stamp of the patching.
     */
    public long getLastPatchedTime()
    {
        return lastPatchedTime;
    }
}
