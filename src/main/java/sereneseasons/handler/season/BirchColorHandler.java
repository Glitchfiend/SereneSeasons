package sereneseasons.handler.season;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
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
			Minecraft.getInstance().getBlockColors().register((BlockState state, @Nullable BlockAndTintGetter dimensionReader, @Nullable BlockPos pos, int tintIndex) ->
			{
				int birchColor = FoliageColor.getBirchColor();
				Level world = Minecraft.getInstance().player.level;
				ResourceKey<Level> dimension = Minecraft.getInstance().player.level.dimension();

				if (world != null && pos != null && SeasonsConfig.changeBirchColor.get() && SeasonsConfig.isDimensionWhitelisted(dimension))
				{
					ResourceKey<Biome> biome = world.getBiomeName(pos).orElse(null);

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
