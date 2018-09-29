package sereneseasons.world.chunk;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import sereneseasons.core.SereneSeasons;

/**
 * Manager to store additional information for chunks.
 */
public class SeasonChunkManager {
	private HashMap<ChunkKey, SeasonChunkData> managedChunks = new HashMap<ChunkKey, SeasonChunkData>();
	
	public static final SeasonChunkManager INSTANCE = new SeasonChunkManager();

	/**
	 * The constructor. Only singleton allowed.
	 */
	private SeasonChunkManager() {
	}

	/**
	 * Returns all managed chunks.
	 * 
	 * @return a collection of managed chunks.
	 */
	public Collection<SeasonChunkData> getManagedChunks() {
		return Collections.unmodifiableCollection(managedChunks.values());
	}

    /**
     * Returns a meta data entry for a chunk. Can create a new one if not existing. 
     * 
     * @param chunk the chunk
     * @param bCreateIfNotExisting if <code>true</code> then a new entry is created if not existing.
     * @return the chunk meta data for seasons.
     */
    public SeasonChunkData getStoredChunkData(Chunk chunk, boolean bCreateIfNotExisting)
    {
        ChunkPos cpos = chunk.getPos();
        ChunkKey key = new ChunkKey(cpos, chunk.getWorld());
        SeasonChunkData chunkData = managedChunks.get(key);
        if (chunkData != null)
        {
            Chunk curChunk = chunkData.getCachedChunk();
            if (curChunk != null)
            {
                if (curChunk != chunk)
                {
                    if (curChunk.isLoaded())
                        SereneSeasons.logger.error("Chunk mismatching in SeasonChunkManager.getStoredChunkData .");
                    curChunk = null;
                }
            }

            if (curChunk == null)
            {
                if (bCreateIfNotExisting)
                {
                    chunkData.attachLoadedChunk(chunk);
                }
                else
                    return null;
            }
            return chunkData;
        }
        if (!bCreateIfNotExisting)
            return null;

        long lastPatchTime = 0; // Initial time. Should be bigger than
                                // ActiveChunkMarker.getSmallerKey() value!

        chunkData = new SeasonChunkData(key, chunk, lastPatchTime);
        managedChunks.put(key, chunkData);
        return chunkData;
    }

    /**
     * Returns a meta data entry for a chunk key. Can create a new one if not existing. 
     * 
     * @param key the chunk key.
     * @param bCreateIfNotExisting if <code>true</code> then a new entry is created if not existing.
     * @return the chunk meta data for seasons.
     */
    public SeasonChunkData getStoredChunkData(ChunkKey key, boolean bCreateIfNotExisting)
    {
        SeasonChunkData chunkData = managedChunks.get(key);
        if (chunkData == null && bCreateIfNotExisting)
        {
            long lastPatchTime = 0; // Initial time. Should be bigger than
                                    // ActiveChunkMarker.getSmallerKey() value!

            chunkData = new SeasonChunkData(key, null, lastPatchTime);
            managedChunks.put(key, chunkData);
        }
        return chunkData;
    }

    /**
     * Returns a meta data entry for a chunk position. Can create a new one if not existing. 
     * 
     * @param world the world of the chunk.
     * @param pos the chunk position.
     * @param bCreateIfNotExisting if <code>true</code> then a new entry is created if not existing.
     * @return the chunk meta data for seasons.
     */
    public SeasonChunkData getStoredChunkData(World world, ChunkPos pos, boolean bCreateIfNotExisting)
    {
        ChunkKey key = new ChunkKey(pos, world);
        return getStoredChunkData(key, bCreateIfNotExisting);
    }
    
    /**
     * Called from {@link sereneseasons.handler.season.SeasonHandler#onWorldUnloaded}. <br/>
     * Cleanup routine if a world got unloaded.
     * 
     * @param world the unloaded world.
     */
    public void onWorldUnload(World world)
    {
        // Clear managed chunk tags associated to the world
    	Iterator<Map.Entry<ChunkKey, SeasonChunkData>> iter = managedChunks.entrySet().iterator();
    	while( iter.hasNext() ) {
    		SeasonChunkData data = iter.next().getValue();
    		if( data.isAssociatedToWorld(world) )
    			iter.remove();
    	}
    }

    /**
     * Called from {@link sereneseasons.handler.season.SeasonHandler#onChunkUnloaded}. <br/>
     * Cleanup routine if a chunk got unloaded. In this case the meta is detached from Chunk.
     *   
     * @param chunk the unloaded chunk.
     */
    public void onChunkUnloaded(Chunk chunk)
    {
        ChunkKey key = new ChunkKey(chunk.getPos(), chunk.getWorld());
        SeasonChunkData chunkData = managedChunks.get(key);
        if (chunkData != null)
        {
            chunkData.detachLoadedChunk();
        }
    }
}
