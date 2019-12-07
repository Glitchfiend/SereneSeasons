package sereneseasons.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockVine;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.config.BiomeConfig;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.SeasonColourUtil;

@Mixin(Block.class)
public abstract class BlockMixin
{

	public int colorMultiplierOld(IBlockAccess blockAccess, int x, int y, int z)
	{
		// Overriden methods provided by ColorTransformer
		return 16777215;
	}

	int handleFoliage(IBlockAccess blockAccess, int x, int y, int z)
	{
		BiomeGenBase biome = blockAccess.getBiomeGenForCoords(x, z);
		if (BiomeConfig.enablesSeasonalEffects(biome))
		{
			SeasonTime calendar = SeasonHandler.getClientSeasonTime();
			ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason();
			return SeasonColourUtil.applySeasonalFoliageColouring(colorProvider, biome, colorMultiplierOld(blockAccess, x, y, z));
		}
		return colorMultiplierOld(blockAccess, x, y, z);
	}

	int handleGras(IBlockAccess blockAccess, int x, int y, int z)
	{
		BiomeGenBase biome = blockAccess.getBiomeGenForCoords(x, z);
		if (BiomeConfig.enablesSeasonalEffects(biome))
		{
			SeasonTime calendar = SeasonHandler.getClientSeasonTime();
			ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason();
			return SeasonColourUtil.applySeasonalGrassColouring(colorProvider, biome, colorMultiplierOld(blockAccess, x, y, z));
		}
		return colorMultiplierOld(blockAccess, x, y, z);
	}

	/**
	 * @author darkshadow44
	 * @reason Redirect to our coloring handlers.
	 */
	@Overwrite
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z)
	{
		Block block = (Block) (Object) this;

		if (block instanceof BlockGrass)
		{
			return handleGras(blockAccess, x, y, z);
		}

		if (block instanceof BlockBush)
		{
			return handleGras(blockAccess, x, y, z);
		}

		if (block instanceof BlockVine)
		{
			return handleFoliage(blockAccess, x, y, z);
		}

		if (block instanceof BlockLeaves)
		{
			if (block instanceof BlockOldLeaf && blockAccess.getBlockMetadata(x, y, z) % 4 == 1) // Ignore spruce
			{
				return colorMultiplierOld(blockAccess, x, y, z);
			}
			return handleFoliage(blockAccess, x, y, z);
		}

		return colorMultiplierOld(blockAccess, x, y, z);
	}
}
