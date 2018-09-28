package sereneseasons.world.chunk;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

/**
 * An object to identify a chunk on a server uniquely. It is a position/dimension pair of the chunk.
 */
public class ChunkKey
{
    public static final Neighbor[] NEIGHBORS = new Neighbor[] {
    		new Neighbor(1, 0, 2),
    		new Neighbor(1, 1, 7),
    		new Neighbor(-1, 0, 0),
    		new Neighbor(-1, 1, 6),
    		new Neighbor(0, 1, 5),
    		new Neighbor(0, -1, 4),
    		new Neighbor(1, -1, 3),
            new Neighbor(-1, -1, 1) };

    private ChunkPos pos;
    private int dimension;
    
    /**
     * The constructor.
     * 
     * @param pos position of the chunk
     * @param world world of the chunk
     */
    public ChunkKey(ChunkPos pos, World world)
    {
        this.pos = pos;
        // NOTE: Don't use world map name dependency to build the key, otherwise
        // map renaming would break linkage to chunk if key is stored in nbt.
        this.dimension = world.provider.getDimension();
    }

    /**
     * The constructor.
     * 
     * @param pos position of the chunk
     * @param dimension dimension of the chunk is located in.
     */
    public ChunkKey(ChunkPos pos, int dimension)
    {
        this.pos = pos;
        this.dimension = dimension;
    }

    /**
     * Tells if the chunk is located in the given world.
     * 
     * @param world the world
     * @return <code>true</code> iff chunk is located in the given world
     */
    public boolean isAssociatedToWorld(World world)
    {
        if (world.isRemote)
            return false;
        // TODO: Exclude more of these exotic world types!
        
        if (world.provider.getDimension() != dimension)
            return false;
        return true;
    }

    /**
     * Returns the position of the chunk.
     * 
     * @return the chunk position.
     */
    public ChunkPos getPos()
    {
        return pos;
    }

    /**
     * Returns the dimension id the chunk is located in.
     * 
     * @return the dimension id.
     */
    public int getDimension()
    {
        return dimension;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + dimension;
        result = prime * result + ((pos == null) ? 0 : pos.hashCode());
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChunkKey other = (ChunkKey) obj;
        if (dimension != other.dimension)
            return false;
        if (pos == null)
        {
            if (other.pos != null)
                return false;
        }
        else if (!pos.equals(other.pos))
            return false;
        return true;
    }
    
    /////////

    /**
     * Identifies a chunk neighbor. 
     */
    public static class Neighbor
    {
        private final int dX;
        private final int dZ;
        private final int oppositeIdx;

        /**
         * The constructor
         * 
         * @param dX x offset to neighbor
         * @param dZ z offset to neighbor
         * @param oppositeIdx index of the opposite neighbor.
         */
        private Neighbor(int dX, int dZ, int oppositeIdx)
        {
            this.dX = dX;
            this.dZ = dZ;
            this.oppositeIdx = oppositeIdx;
        }

        /**
         * Returns the x offset.
         * 
         * @return the x offset.
         */
        public int getDX()
        {
            return dX;
        }

        /**
         * Returns the z offset.
         * 
         * @return the z offset.
         */
        public int getDZ()
        {
            return dZ;
        }

        /**
         * Returns the index of the opposite chunk.
         * 
         * @return index of the opposite chunk.
         */
        public int getOppositeIdx()
        {
            return oppositeIdx;
        }

        /**
         * Returns the neighbor chunk position. 
         * 
         * @param pos position of actual chunk
         * @return position of neighbor chunk
         */
        public ChunkPos getOffset(ChunkPos pos)
        {
            return new ChunkPos(pos.x + dX, pos.z + dZ);
        }

        /**
         * Returns the neighbor chunk position.
         * 
         * @param key the chunk key holding position of actual chunk
         * @return position of neighbor chunk
         */
        public ChunkKey getOffset(ChunkKey key)
        {
            return new ChunkKey(getOffset(key.getPos()), key.getDimension());
        }
    }
}