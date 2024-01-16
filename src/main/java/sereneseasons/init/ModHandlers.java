/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;

import net.minecraftforge.common.MinecraftForge;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.handler.PacketHandler;
import sereneseasons.handler.season.SeasonHandler;

public class ModHandlers
{
    private static final SeasonHandler SEASON_HANDLER = new SeasonHandler();

    public static void init()
    {
        PacketHandler.init();

        //Handlers for functionality related to seasons
        SeasonHelper.dataProvider = SEASON_HANDLER;
    }
}
