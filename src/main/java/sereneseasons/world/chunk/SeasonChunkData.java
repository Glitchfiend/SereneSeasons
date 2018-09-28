package sereneseasons.world.chunk;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Stores additional meta data for a chunk used by seasons, like time stamps when the chunk has been patched lately. 
 */
public class SeasonChunkData implements INBTSerializable<NBTTagCompound>
{
	// Reference to chunk
    private ChunkKey key;
    private Chunk chunk;

    // Is stored by SeasonSavedData.
    private long lastPatchedTime;
      
    /**
     * The constructor.
     * 
     * @param key key for the chunk
     * @param chunk the chunk. May be <code>null</code> if chunk is not known/loaded.
     * @param lastPatchedTime last time the chunk has been patched.
     */
    public SeasonChunkData(ChunkKey key, Chunk chunk, long lastPatchedTime)
    {
        this.key = key;
        this.chunk = chunk;
        this.lastPatchedTime = lastPatchedTime;
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
        this.chunk = null;
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
    public void setPatchTimeUptodate()
    {
        if (chunk != null)
            this.lastPatchedTime = chunk.getWorld().getTotalWorldTime();
    }

    /**
     * Sets the time stamp this chunk has been patched to a specific time.
     * 
     * @param lastPatchedTime the specific time.
     */
    public void setPatchTimeTo(long lastPatchedTime)
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
    
    /**
     * Tells if the chunk is located in the given world.
     * 
     * @param world the world
     * @return <code>true</code> iff chunk is located in the given world
     */
    public boolean isAssociatedToWorld(World world) {
    	if( chunk != null ) {
    		if( world != chunk.getWorld() )
    			return false;
    	}
    	else {
    		if( !key.isAssociatedToWorld(world) )
    			return false;
    	}
    	return true;
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setLong("LastPatchedTime", lastPatchedTime);
		return nbt;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.lastPatchedTime = nbt.getLong("LastPatchedTime");
	}
	
	public static boolean hasNBTData(NBTTagCompound nbt) {
		// Check if at least one mandatory key is existing
		return nbt.hasKey("LastPatchedTime");
	}
}
