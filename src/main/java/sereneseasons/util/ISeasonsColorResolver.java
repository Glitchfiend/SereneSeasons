package sereneseasons.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper.ColorResolver;

public interface ISeasonsColorResolver extends ColorResolver {
	default int getColorAtPos(Biome biome, BlockPos blockPosition) {
		return getColorAtPos(null, biome, blockPosition);
	}
	
	int getColorAtPos(IBlockAccess blockAccess, Biome biome, BlockPos blockPosition);
}
