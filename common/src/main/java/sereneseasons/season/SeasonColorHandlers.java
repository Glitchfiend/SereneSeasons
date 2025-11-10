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
import java.util.Optional;

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

        Registry<Biome> biomeRegistry = level.registryAccess().lookupOrThrow(Registries.BIOME);
        Holder.Reference<Biome> biomeHolder = biomeRegistry.getResourceKey(biome).flatMap(biomeRegistry::get).orElse(null);

        if (biomeHolder != null)
        {
            return getSeasonalColor(level, biomeHolder, x, z, type, originalColor);
        }

        return originalColor;
    }

    public static int getSeasonalColor(@Nullable Level level, Holder<Biome> biomeHolder, double x, double z, ResolverType type)
    {
        int originalColor = originalColorFor(biomeHolder, x, z, type);
        return getSeasonalColor(level, biomeHolder, x, z, type, originalColor);
    }

    public static int getSeasonalColor(@Nullable Level level, Holder<Biome> biomeHolder, double x, double z, ResolverType type, int originalColor)
    {
        if (level == null || biomeHolder == null)
        {
            return originalColor;
        }

        if (biomeHolder.is(ModTags.Biomes.BLACKLISTED_BIOMES) || !ModConfig.seasons.isDimensionWhitelisted(level.dimension()))
        {
            return originalColor;
        }

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

    private static int originalColorFor(Holder<Biome> biomeHolder, double x, double z, ResolverType type)
    {
        ColorResolver resolver = switch (type) {
            case GRASS -> originalGrassColorResolver;
            case FOLIAGE -> originalFoliageColorResolver;
        };

        if (resolver != null)
        {
            return resolver.getColor(biomeHolder.value(), x, z);
        }

        return switch (type) {
            case GRASS -> biomeHolder.value().getGrassColor(x, z);
            case FOLIAGE -> biomeHolder.value().getFoliageColor();
        };
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
