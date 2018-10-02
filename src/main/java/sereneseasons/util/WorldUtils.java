package sereneseasons.util;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class WorldUtils {
	private WorldUtils() {}
	
	public static World castToWorld(IBlockAccess worldIn) {
		if( worldIn instanceof World )
			return (World)worldIn;
		if( worldIn instanceof ChunkCache ) {
			return ((ChunkCache)worldIn).world;
		}
		return null;
	}
}
