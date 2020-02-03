package sereneseasons.handler.season;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.ILightReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
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
	public static void setup()
    {
		if (FMLEnvironment.dist == Dist.CLIENT)
		{
			Minecraft.getInstance().getBlockColors().register((BlockState state, @Nullable ILightReader lightReader, @Nullable BlockPos pos, int tintIndex) ->
			{
				int birchColor = FoliageColors.getBirchColor();
				World world = Minecraft.getInstance().player.level;
				int dimension = Minecraft.getInstance().player.dimension.getId();

				if (world != null && pos != null && SeasonsConfig.changeBirchColor.get() && SeasonsConfig.isDimensionWhitelisted(dimension))
				{
					Biome biome = world.getBiome(pos);

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
