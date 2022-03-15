/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.handler.PacketHandler;
import sereneseasons.handler.season.*;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.SeasonColorUtil;

import java.util.Optional;

public class ModHandlers
{
    private static final SeasonHandler SEASON_HANDLER = new SeasonHandler();

    public static void init()
    {
        PacketHandler.init();

        //Handlers for functionality related to seasons
        MinecraftForge.EVENT_BUS.register(SEASON_HANDLER);
        SeasonHelper.dataProvider = SEASON_HANDLER;

        MinecraftForge.EVENT_BUS.register(new RandomUpdateHandler());
        MinecraftForge.EVENT_BUS.register(new SeasonalCropGrowthHandler());
        MinecraftForge.EVENT_BUS.register(new TimeSkipHandler());

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            registerSeasonColourHandlers();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static ColorResolver originalGrassColorResolver;
    @OnlyIn(Dist.CLIENT)
    private static ColorResolver originalFoliageColorResolver;

    @OnlyIn(Dist.CLIENT)
    private static void registerSeasonColourHandlers()
    {
        originalGrassColorResolver = BiomeColors.GRASS_COLOR_RESOLVER;
        originalFoliageColorResolver = BiomeColors.FOLIAGE_COLOR_RESOLVER;

        BiomeColors.GRASS_COLOR_RESOLVER = (biome, x, z) ->
        {
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
            Holder<Biome> biomeHolder = biomeRegistry.getResourceKey(biome).flatMap(key -> Optional.of(biomeRegistry.getOrCreateHolder(key))).orElse(null);
            int originalColor = originalGrassColorResolver.getColor(biome, x, z);

            if (biomeHolder != null)
            {
                SeasonTime calendar = SeasonHandler.getClientSeasonTime();
                ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biomeHolder) ? calendar.getTropicalSeason() : calendar.getSubSeason();

                return SeasonColorUtil.applySeasonalGrassColouring(colorProvider, biomeHolder, originalColor);
            }

            return originalColor;
        };

        BiomeColors.FOLIAGE_COLOR_RESOLVER = (biome, x, z) ->
        {
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
            Holder<Biome> biomeHolder = biomeRegistry.getResourceKey(biome).flatMap(key -> Optional.of(biomeRegistry.getOrCreateHolder(key))).orElse(null);
            int originalColor = originalFoliageColorResolver.getColor(biome, x, z);

            if (biomeHolder != null)
            {
                SeasonTime calendar = SeasonHandler.getClientSeasonTime();
                ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biomeHolder) ? calendar.getTropicalSeason() : calendar.getSubSeason();

                return SeasonColorUtil.applySeasonalFoliageColouring(colorProvider, biomeHolder, originalColor);
            }

            return originalColor;
        };
    }
}
