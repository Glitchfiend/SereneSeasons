package sereneseasons.world.chunk;

import java.lang.ref.WeakReference;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;
import sereneseasons.core.SereneSeasons;

/**
 * Stores additional meta data for a chunk used by seasons, like time stamps when the chunk has been patched lately. 
 */
public class SeasonChunkData implements INBTSerializable<NBTTagCompound>
{
	// Reference to chunk
    private ChunkKey key;
    private WeakReference<Chunk> chunkRef;

    // Is stored by SeasonSavedData.
    private long lastPatchedTime;
      
    /**
     * The constructor.
     * 
     * @param key key for the chunk
     * @param chunk the chunk. May be <code>null</code> if chunk is not known/loaded.
     * @param lastPatchedTime last time the chunk has been patched.
     */
    SeasonChunkData(ChunkKey key, Chunk chunk, long lastPatchedTime)
    {
        this.key = key;
        if( chunk != null)
        	attachLoadedChunk(chunk);
        this.lastPatchedTime = lastPatchedTime;
    }

    /**
     * Attaches a loaded chunk object to this meta data. 
     * 
     * @param chunk the chunk
     */
    void attachLoadedChunk(Chunk chunk)
    {
    	Chunk currentChunk = getCachedChunk();
    	if( currentChunk != null && currentChunk.isLoaded() && currentChunk != chunk )
    		SereneSeasons.logger.error("current chunk is mismatching assigned in SeasonChunkData.attachLoadedChunk .");
        if (chunk == null)
            throw new IllegalArgumentException("chunk must be non null. Use detachLoadedChunk() for other case.");
        this.chunkRef = new WeakReference<Chunk>(chunk);
    }
    
    /**
     * Detaches the chunk from this meta data, if it got unloaded.
     */
    void detachLoadedChunk()
    {
        this.chunkRef = null;
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
     * Returns the chunk known object.
     * 
     * @return the chunk or <code>null</code> if not loaded or unknown.
     */
    public Chunk getCachedChunk()
    {
    	if( chunkRef == null)
    		return null;
    	Chunk chunk = chunkRef.get();
        return chunk;
    }
    
    /**
     * Returns chunk. Ensured it is loaded and non-<code>null</code> if world is provided.
     * 
     * @param world the world of the chunk. If <code>null</code> then chunk won't be loaded if not existing.
     * @return the chunk or <code>null</code> if not loaded.
     */
    public Chunk getChunk(World world) {
    	Chunk chunk = getCachedChunk();
    	if( world != null ) {
        	if( !isAssociatedToWorld(world) )
        		throw new IllegalArgumentException("Incompatible world.");
        	if( chunk == null ) {
        		// NOTE: ChunkHandler.onChunkLoaded may get called thus overwriting this object
        		chunk = world.getChunkFromChunkCoords(key.getPos().x, key.getPos().z);
        		attachLoadedChunk(chunk);
        	}
    	}
    	return chunk;
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
    	Chunk chunk = getCachedChunk();
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
		// NOTE: May get called again for already existing object.
		//       Happens for example on getChunk(World) call if chunk is not loaded.
		
		this.lastPatchedTime = nbt.getLong("LastPatchedTime");
	}
	
	public static boolean hasNBTData(NBTTagCompound nbt) {
		// Check if at least one mandatory key is existing
		return nbt.hasKey("LastPatchedTime");
	}
}
