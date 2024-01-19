/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;

import sereneseasons.api.season.SeasonHelper;
import sereneseasons.season.SeasonHandler;

public class ModAPI
{
    private static final SeasonHandler SEASON_HANDLER = new SeasonHandler();

    public static void init()
    {
        SeasonHelper.dataProvider = SEASON_HANDLER;
    }
}
