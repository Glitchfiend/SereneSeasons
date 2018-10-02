/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.init;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.handler.PacketHandler;
import sereneseasons.handler.season.BirchColorHandler;
import sereneseasons.handler.season.ProviderIceHandler;
import sereneseasons.handler.season.RandomUpdateHandler;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.handler.season.SeasonSleepHandler;
import sereneseasons.handler.season.SeasonalCropGrowthHandler;
import sereneseasons.handler.season.WeatherFrequencyHandler;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.ISeasonsColorResolver;
import sereneseasons.util.SeasonColourUtil;

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
        MinecraftForge.TERRAIN_GEN_BUS.register(new ProviderIceHandler());
        MinecraftForge.EVENT_BUS.register(new SeasonSleepHandler());

        MinecraftForge.EVENT_BUS.register(new WeatherFrequencyHandler());
        MinecraftForge.EVENT_BUS.register(new SeasonalCropGrowthHandler());

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            registerSeasonColourHandlers();
        }
    }

    @SideOnly(Side.CLIENT)
    private static BiomeColorHelper.ColorResolver originalGrassColorResolver;
    @SideOnly(Side.CLIENT)
    private static BiomeColorHelper.ColorResolver originalFoliageColorResolver;

    @SideOnly(Side.CLIENT)
    private static void registerSeasonColourHandlers()
    {
        originalGrassColorResolver = BiomeColorHelper.GRASS_COLOR;
        originalFoliageColorResolver = BiomeColorHelper.FOLIAGE_COLOR;

        BiomeColorHelper.GRASS_COLOR = new ISeasonsColorResolver() {
			@Override
			public int getColorAtPos(IBlockAccess blockAccess, Biome biome, BlockPos blockPosition) {
				if( SeasonHandler.isWorldBlacklisted(blockAccess) )
					return originalGrassColorResolver.getColorAtPos(biome, blockPosition);
	            SeasonTime calendar = new SeasonTime(SeasonHandler.clientSeasonCycleTicks);
	            ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason();
	            return SeasonColourUtil.applySeasonalGrassColouring(colorProvider, biome, originalGrassColorResolver.getColorAtPos(biome, blockPosition));
			}
        };

        BiomeColorHelper.FOLIAGE_COLOR = new ISeasonsColorResolver() {

			@Override
			public int getColorAtPos(IBlockAccess blockAccess, Biome biome, BlockPos blockPosition) {
				if( SeasonHandler.isWorldBlacklisted(blockAccess) )
					return originalFoliageColorResolver.getColorAtPos(biome, blockPosition);
				SeasonTime calendar = new SeasonTime(SeasonHandler.clientSeasonCycleTicks);
	            ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(biome) ? calendar.getTropicalSeason() : calendar.getSubSeason();
	            return SeasonColourUtil.applySeasonalFoliageColouring(colorProvider, biome, originalFoliageColorResolver.getColorAtPos(biome, blockPosition));
	        }
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
