package sereneseasons.handler.season;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.biome.BiomeUtil;

import javax.annotation.Nullable;

public class BirchColorHandler
{
	public static void setup()
    {
		if (FMLEnvironment.dist == Dist.CLIENT)
		{
			Minecraft.getInstance().getBlockColors().register((BlockState state, @Nullable IBlockDisplayReader dimensionReader, @Nullable BlockPos pos, int tintIndex) ->
			{
				int birchColor = FoliageColors.getBirchColor();
				World world = Minecraft.getInstance().player.level;
				RegistryKey<World> dimension = Minecraft.getInstance().player.level.dimension();

				if (world != null && pos != null && SeasonsConfig.changeBirchColor.get() && SeasonsConfig.isDimensionWhitelisted(dimension))
				{
					RegistryKey<Biome> biome = world.getBiomeName(pos).orElseThrow();

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
