/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;

import glitchcore.config.ConfigSync;
import sereneseasons.config.FertilityConfig;
import sereneseasons.config.SeasonsConfig;

public class ModConfig
{
    public static FertilityConfig fertility;
    public static SeasonsConfig seasons;

    public static void init()
    {
        fertility = new FertilityConfig();
        seasons = new SeasonsConfig();

        ConfigSync.register(fertility);
        ConfigSync.register(seasons);
    }
}
