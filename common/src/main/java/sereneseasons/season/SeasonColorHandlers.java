package sereneseasons.season;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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
import java.util.List;

public class SeasonColorHandlers
{
	private static final Multimap<ResolverType, ColorOverride> resolverOverrides = HashMultimap.create();

	public static void setup()
    {
		registerGrassAndFoliageColorHandlers();
    }

	public static void registerResolverOverride(ResolverType type, ColorOverride override)
	{
		resolverOverrides.put(type, override);
	}

	private static ColorResolver originalGrassColorResolver;
	private static ColorResolver originalFoliageColorResolver;

	private static void registerGrassAndFoliageColorHandlers()
	{
		originalGrassColorResolver = BiomeColors.GRASS_COLOR_RESOLVER;
		originalFoliageColorResolver = BiomeColors.FOLIAGE_COLOR_RESOLVER;

		BiomeColors.GRASS_COLOR_RESOLVER = (biome, x, z) -> resolveColors(ResolverType.GRASS, biome, x, z);
		BiomeColors.FOLIAGE_COLOR_RESOLVER = (biome, x, z) -> resolveColors(ResolverType.FOLIAGE, biome, x, z);
	}

	private static int resolveColors(ResolverType type, Biome biome, double x, double z)
	{
		int originalColor = switch (type) {
			case GRASS -> originalGrassColorResolver.getColor(biome, x, z);
			case FOLIAGE -> originalFoliageColorResolver.getColor(biome, x, z);
		};

		Minecraft minecraft = Minecraft.getInstance();
		Level level = minecraft.level;

		if (level == null) return originalColor;

		Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
		Holder<Biome> biomeHolder = biomeRegistry.getResourceKey(biome).flatMap(biomeRegistry::getHolder).orElse(null);

		if (biomeHolder != null)
		{
			ISeasonState calendar = SeasonHelper.getSeasonState(level);
			ISeasonColorProvider colorProvider = biomeHolder.is(ModTags.Biomes.TROPICAL_BIOMES) ? calendar.getTropicalSeason() : calendar.getSubSeason();

			int seasonalColor = switch (type) {
				case GRASS -> SeasonColorUtil.applySeasonalGrassColouring(colorProvider, biomeHolder, originalColor);
				case FOLIAGE -> SeasonColorUtil.applySeasonalFoliageColouring(colorProvider, biomeHolder, originalColor);
			};

			int currentColor = seasonalColor;
			for (ColorOverride override : resolverOverrides.get(type))
			{
				currentColor = override.apply(originalColor, seasonalColor, currentColor, biomeHolder, x, z);
			}

			return currentColor;
		}

		return originalColor;
	}

	public interface ColorOverride
	{
		int apply(int originalColor, int seasonalColor, int currentColor, Holder<Biome> biome, double x, double z);
	}

	public enum ResolverType
	{
		GRASS, FOLIAGE
	}
}
