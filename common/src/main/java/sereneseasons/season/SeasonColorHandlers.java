package sereneseasons.season;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;
import sereneseasons.util.SeasonColorUtil;

import javax.annotation.Nullable;

public class SeasonColorHandlers
{
	public static void setup()
    {
		registerGrassAndFoliageColorHandlers();
    }

	private static ColorResolver originalGrassColorResolver;
	private static ColorResolver originalFoliageColorResolver;

	private static void registerGrassAndFoliageColorHandlers()
	{
		originalGrassColorResolver = BiomeColors.GRASS_COLOR_RESOLVER;
		originalFoliageColorResolver = BiomeColors.FOLIAGE_COLOR_RESOLVER;

		BiomeColors.GRASS_COLOR_RESOLVER = (biome, x, z) ->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Level level = minecraft.level;
			Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
			Holder<Biome> biomeHolder = biomeRegistry.getResourceKey(biome).flatMap(key -> biomeRegistry.getHolder(key)).orElse(null);
			int originalColor = originalGrassColorResolver.getColor(biome, x, z);

			if (biomeHolder != null)
			{
				ISeasonState calendar = SeasonHelper.getSeasonState(level);
				ISeasonColorProvider colorProvider = biomeHolder.is(ModTags.Biomes.TROPICAL_BIOMES) ? calendar.getTropicalSeason() : calendar.getSubSeason();

				return SeasonColorUtil.applySeasonalGrassColouring(colorProvider, biomeHolder, originalColor);
			}

			return originalColor;
		};

		BiomeColors.FOLIAGE_COLOR_RESOLVER = (biome, x, z) ->
		{
			Minecraft minecraft = Minecraft.getInstance();
			Level level = minecraft.level;
			Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
			Holder<Biome> biomeHolder = biomeRegistry.getResourceKey(biome).flatMap(key -> biomeRegistry.getHolder(key)).orElse(null);
			int originalColor = originalFoliageColorResolver.getColor(biome, x, z);

			if (biomeHolder != null)
			{
				ISeasonState calendar = SeasonHelper.getSeasonState(level);
				ISeasonColorProvider colorProvider = biomeHolder.is(ModTags.Biomes.TROPICAL_BIOMES) ? calendar.getTropicalSeason() : calendar.getSubSeason();

				return SeasonColorUtil.applySeasonalFoliageColouring(colorProvider, biomeHolder, originalColor);
			}

			return originalColor;
		};
	}
}
