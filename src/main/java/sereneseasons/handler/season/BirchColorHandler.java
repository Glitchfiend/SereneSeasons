package sereneseasons.handler.season;

import javax.annotation.Nullable;

import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;
import sereneseasons.season.SeasonTime;

public class BirchColorHandler
{
	public static void init()
    {
		Minecraft.getInstance().getBlockColors().register((BlockState state, @Nullable IEnviromentBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) ->
		{
			BlockPlanks.EnumType plankstype = (BlockPlanks.EnumType) state.getValue(BlockOldLeaf.VARIANT);

			if (plankstype == BlockPlanks.EnumType.SPRUCE)
			{
				return ColorizerFoliage.getFoliageColorPine();
			}
			else if (plankstype == BlockPlanks.EnumType.BIRCH)
			{
				int birchColor = ColorizerFoliage.getFoliageColorBirch();
				int dimension = Minecraft.getMinecraft().player.dimension;

				if (worldIn != null && pos != null && ModConfig.seasons.changeBirchColour && SeasonsConfig.isDimensionWhitelisted(dimension))
				{
					Biome biome = worldIn.getBiome(pos);

					if (BiomeConfig.enablesSeasonalEffects(biome))
					{
						SeasonTime calendar = SeasonHandler.getClientSeasonTime();
						ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason();
						birchColor = colorProvider.getBirchColor();
					}
				}

				return birchColor;
			}
			else
			{
				return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
			}
		}, Blocks.LEAVES);
    }
}
