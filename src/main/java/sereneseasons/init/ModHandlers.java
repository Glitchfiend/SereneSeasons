/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.init;

import net.minecraft.world.biome.BiomeColors;
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

public class ModHandlers
{
    private static final SeasonHandler SEASON_HANDLER = new SeasonHandler();

    public static void setup()
    {
        PacketHandler.init();

        //Handlers for functionality related to seasons
        MinecraftForge.EVENT_BUS.register(SEASON_HANDLER);
        SeasonHelper.dataProvider = SEASON_HANDLER;
        
        MinecraftForge.EVENT_BUS.register(new RandomUpdateHandler());
        
        MinecraftForge.EVENT_BUS.register(new SeasonSleepHandler());
        
        MinecraftForge.EVENT_BUS.register(new SeasonalCropGrowthHandler());

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            registerSeasonColourHandlers();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static BiomeColors.IColorResolver originalGrassColorResolver;
    @OnlyIn(Dist.CLIENT)
    private static BiomeColors.IColorResolver originalFoliageColorResolver;

    @OnlyIn(Dist.CLIENT)
    private static void registerSeasonColourHandlers()
    {
        originalGrassColorResolver = BiomeColors.GRASS_COLOR;
        originalFoliageColorResolver = BiomeColors.FOLIAGE_COLOR;

        BiomeColors.GRASS_COLOR = (biome, blockPosition) ->
        {
            SeasonTime calendar = SeasonHandler.getClientSeasonTime();
            ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason();
            return SeasonColorUtil.applySeasonalGrassColouring(colorProvider, biome, originalGrassColorResolver.getColor(biome, blockPosition));
        };

        BiomeColors.FOLIAGE_COLOR = (biome, blockPosition) ->
        {
            SeasonTime calendar = SeasonHandler.getClientSeasonTime();
            ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason();
            return SeasonColorUtil.applySeasonalFoliageColouring(colorProvider, biome, originalFoliageColorResolver.getColor(biome, blockPosition));
        };
    }
}
