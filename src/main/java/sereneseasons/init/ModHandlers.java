/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.init;

import net.minecraft.resources.ResourceKey;
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
import sereneseasons.core.SereneSeasons;
import sereneseasons.handler.PacketHandler;
import sereneseasons.handler.season.*;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.SeasonColorUtil;
import sereneseasons.util.biome.BiomeUtil;

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
            ResourceKey<Biome> biomeKey = BiomeUtil.getBiomeKey(biome);
            SeasonTime calendar = SeasonHandler.getClientSeasonTime();
            ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biomeKey) ? calendar.getTropicalSeason() : calendar.getSubSeason();

            return SeasonColorUtil.applySeasonalGrassColouring(colorProvider, biomeKey, originalGrassColorResolver.getColor(biome, x, z));
        };

        BiomeColors.FOLIAGE_COLOR_RESOLVER = (biome, x, z) ->
        {
            ResourceKey<Biome> biomeKey = BiomeUtil.getBiomeKey(biome);
            SeasonTime calendar = SeasonHandler.getClientSeasonTime();
            ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biomeKey) ? calendar.getTropicalSeason() : calendar.getSubSeason();

            return SeasonColorUtil.applySeasonalFoliageColouring(colorProvider, biomeKey, originalFoliageColorResolver.getColor(biome, x, z));
        };
    }
}
