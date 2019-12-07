/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.init;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.handler.PacketHandler;
import sereneseasons.handler.season.RandomUpdateHandler;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.handler.season.SeasonSleepHandler;
import sereneseasons.handler.season.SeasonalCropGrowthHandler;

public class ModHandlers
{
    private static final SeasonHandler SEASON_HANDLER = new SeasonHandler();

    public static void init()
    {
        PacketHandler.init();

        //Handlers for functionality related to seasons
        FMLCommonHandler.instance().bus().register(SEASON_HANDLER);
        MinecraftForge.TERRAIN_GEN_BUS.register(SEASON_HANDLER);
        SeasonHelper.dataProvider = SEASON_HANDLER;
        
        FMLCommonHandler.instance().bus().register(new RandomUpdateHandler());
        
        FMLCommonHandler.instance().bus().register(new SeasonSleepHandler());
        
        MinecraftForge.EVENT_BUS.register(new SeasonalCropGrowthHandler());
    }
    
    public static void postInit()
    {
    }
}
