package sereneseasons.season.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import sereneseasons.util.IDataStorable;

/**
 * Stores additional meta data for a chunk used by seasons, like time stamps when the chunk has been patched lately. 
 */
public class SeasonChunkData
{
	// Reference to chunk
    private ChunkKey key;
    private Chunk chunk;

    // Is stored by SeasonSavedData.
    private long lastPatchedTime;
    
    /**
     * The constructor. Used for streaming.
     */
    public SeasonChunkData() {
    }
    
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
//        setToBePatched(false);
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
    
    ////////////////////
    
    /**
     * Storage object for a chunk meta data {@link SeasonChunkData}. 
     */
    public static class ChunkDataStorage implements IDataStorable
    {
        private ChunkKey key;
        private long lastPatchedTime;

        /**
         * The constructor. Used for streaming.
         */
        public ChunkDataStorage()
        {
            // For streaming
        }

        /**
         * The constructor.
         * 
         * @param key the chunk key identifying the chunk itself.
         * @param data
         */
        public ChunkDataStorage(ChunkKey key, SeasonChunkData data)
        {
            this.key = key;
            this.lastPatchedTime = data.getLastPatchedTime();
        }

        /**
         * Returns the key identifying the chunk.
         * 
         * @return the chunk key.
         */
        public ChunkKey getKey()
        {
            return key;
        }

        /**
         * Returns the time stamp a chunk has been patched last time.
         * 
         * @return the time stamp.
         */
        public long getLastPatchedTime()
        {
            return lastPatchedTime;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void writeToStream(ObjectOutputStream os) throws IOException
        {
            os.writeInt(key.getPos().x);
            os.writeInt(key.getPos().z);
            os.writeInt(key.getDimension());
            os.writeLong(lastPatchedTime);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void readFromStream(ObjectInputStream is) throws IOException
        {
            int chunkXPos = is.readInt();
            int chunkZPos = is.readInt();
            int dimension = is.readInt();
            this.key = new ChunkKey(new ChunkPos(chunkXPos, chunkZPos), dimension);
            this.lastPatchedTime = is.readLong();
        }
    }

}
