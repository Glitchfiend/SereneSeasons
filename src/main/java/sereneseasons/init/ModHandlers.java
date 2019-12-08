/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.init;

import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.handler.PacketHandler;
import sereneseasons.handler.season.BirchColorHandler;
import sereneseasons.handler.season.RandomUpdateHandler;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.handler.season.SeasonSleepHandler;
import sereneseasons.handler.season.SeasonalCropGrowthHandler;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.SeasonColorUtil;

public class ModHandlers
{
    private static final SeasonHandler SEASON_HANDLER = new SeasonHandler();

    public static void init()
    {
        PacketHandler.init();

        //Handlers for functionality related to seasons
        MinecraftForge.EVENT_BUS.register(SEASON_HANDLER);
        MinecraftForge.TERRAIN_GEN_BUS.register(SEASON_HANDLER);
        SeasonHelper.dataProvider = SEASON_HANDLER;
        
        MinecraftForge.EVENT_BUS.register(new RandomUpdateHandler());
        
        MinecraftForge.EVENT_BUS.register(new SeasonSleepHandler());
        
        MinecraftForge.EVENT_BUS.register(new SeasonalCropGrowthHandler());

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            registerSeasonColourHandlers();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static BiomeColorHelper.ColorResolver originalGrassColorResolver;
    @OnlyIn(Dist.CLIENT)
    private static BiomeColorHelper.ColorResolver originalFoliageColorResolver;

    @OnlyIn(Dist.CLIENT)
    private static void registerSeasonColourHandlers()
    {
        originalGrassColorResolver = BiomeColorHelper.GRASS_COLOR;
        originalFoliageColorResolver = BiomeColorHelper.FOLIAGE_COLOR;

        BiomeColorHelper.GRASS_COLOR = (biome, blockPosition) ->
        {
            SeasonTime calendar = SeasonHandler.getClientSeasonTime();
            ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason();
            return SeasonColorUtil.applySeasonalGrassColouring(colorProvider, biome, originalGrassColorResolver.getColorAtPos(biome, blockPosition));
        };

        BiomeColorHelper.FOLIAGE_COLOR = (biome, blockPosition) ->
        {
            SeasonTime calendar = SeasonHandler.getClientSeasonTime();
            ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason();
            return SeasonColorUtil.applySeasonalFoliageColouring(colorProvider, biome, originalFoliageColorResolver.getColorAtPos(biome, blockPosition));
        };
    }
    
    public static void postInit()
    {
    	if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
    		BirchColorHandler.init();
        }
    }
}
