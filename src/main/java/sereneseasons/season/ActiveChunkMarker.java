package sereneseasons.season;

import net.minecraft.world.World;
import sereneseasons.util.BinaryHeap.Node;
import sereneseasons.season.ChunkData;

/**
 * Used to mark chunks as active and keep tracking them. Patching time stamps for active chunks are
 * always kept up-to-date in {@link ChunkPatchingManager#onServerWorldTick(net.minecraft.world.WorldServer)}.
 */
public class ActiveChunkMarker extends Node<Long>
{
    private final ChunkData data;
    private final World world;
    private long lastVisitTime;

    /**
     * The constructor. Called from {@link ChunkPatchingManager#onServerWorldTick(net.minecraft.world.WorldServer)}.
     * 
     * @param data the marked chunk
     * @param world the world
     */
    ActiveChunkMarker(ChunkData data, World world)
    {
        this.data = data;
        this.world = world;
        this.lastVisitTime = 0;

        data.setBelongingAC(this);
    }

    /**
     * Returns the chunk key.
     * 
     * @return the chunk key.
     */
    ChunkKey getKey()
    {
        return data.getKey();
    }

    /**
     * Returns the chunk meta data.
     * 
     * @return the meta data.
     */
    ChunkData getData()
    {
        return data;
    }

    /**
     * Returns the world for the chunk.
     * 
     * @return the world.
     */
    World getWorld()
    {
        return world;
    }

    /**
     * Returns the time stamp the chunk activity was tested last time.
     * 
     * @return the world.
     */
    long getLastVisitTime()
    {
        return lastVisitTime;
    }

    /**
     * Used to untrack a chunk of being active. <br/>
     * <br/>
     * <b>Important</b>: Shouldn't be used directly, as the heap of tracked chunks at {@link ChunkPatchingManager}
     * needs to be in synch with this action.
     */
    void internalUnmark()
    {
        data.setBelongingAC(null);
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