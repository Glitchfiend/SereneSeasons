package sereneseasons.handler.season;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;
import sereneseasons.season.SeasonTime;

import javax.annotation.Nullable;

public class BirchColorHandler
{
	public static void init()
    {
		if (FMLEnvironment.dist == Dist.CLIENT)
		{
			Minecraft.getInstance().getBlockColors().register((BlockState state, @Nullable IEnviromentBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) ->
			{
				int birchColor = FoliageColors.getBirch();
				int dimension = Minecraft.getInstance().player.dimension.getId();

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
			}, Blocks.BIRCH_LEAVES);
		}
    }
}
