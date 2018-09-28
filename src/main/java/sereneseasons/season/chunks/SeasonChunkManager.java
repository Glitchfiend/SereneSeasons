package sereneseasons.season.chunks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;
import sereneseasons.core.SereneSeasons;
import sereneseasons.util.DataUtils;

/**
 * Manager to store additional information for chunks.
 */
public class SeasonChunkManager implements INBTSerializable<NBTTagCompound> {
	public HashMap<ChunkKey, SeasonChunkData> managedChunks = new HashMap<ChunkKey, SeasonChunkData>();

	/**
	 * The constructor.
	 */
	public SeasonChunkManager() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		
        try
        {
            nbt.setByteArray("ChunkData", DataUtils.toBytebufStorable(toChunkDataList()));
        }
        catch (IOException e)
        {
        	SereneSeasons.logger.error("Couldn't store chunk patch timestamps. Some chunks won't be in synch with season.", e);
        }
		return nbt;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if( nbt != null ) {
	        try
	        {
	            List<SeasonChunkData> storedChunkData = DataUtils.toListStorable(nbt.getByteArray("ChunkData"), SeasonChunkData.class);
	            applyLoadedChunkData(storedChunkData);
	        }
	        catch (IOException e)
	        {
	        	SereneSeasons.logger.error("Couldn't load chunk patch timestamps. Some chunks won't be in synch with season.", e);
	        }
		}		
	}
	
    /**
     * Creates a list of stored chunks ready to be written to NBT.
     * 
     * @return a list of chunk meta data to be stored.
     */
    private List<SeasonChunkData> toChunkDataList()
    {
        int size = managedChunks.size();
        ArrayList<SeasonChunkData> result = new ArrayList<SeasonChunkData>(size);
        for (Map.Entry<ChunkKey, SeasonChunkData> entry : managedChunks.entrySet())
        {
        	result.add(entry.getValue());
        }
        return result;
    }

    /**
     * Transfers readen NBT data to stored chunk meta data.
     * 
     * @param list a list of readen chunk meta data. 
     */
    private void applyLoadedChunkData(List<SeasonChunkData> list)
    {
        for (SeasonChunkData data : list)
        {
        	managedChunks.put(data.getKey(), data);
        }
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
            Chunk curChunk = chunkData.getChunk();
            if (curChunk != null)
            {
                if (curChunk != chunk)
                {
                    if (curChunk.isLoaded())
                        SereneSeasons.logger.error("Chunk mismatching in SeasonSavedData.getStoredChunkData .");
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
    	Iterator<Entry<ChunkKey, SeasonChunkData>> iter = managedChunks.entrySet().iterator();
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
