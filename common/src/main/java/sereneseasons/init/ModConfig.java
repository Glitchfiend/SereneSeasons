/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;


import sereneseasons.config.FertilityConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.config.SeasonsConfigModel;

public class ModConfig
{
    public static FertilityConfig fertility;
    public static SeasonsConfig seasons;

    public static void init()
    {
        fertility = FertilityConfig.createAndLoad();

        var inst = SeasonsConfig.createAndLoad();
        SeasonsConfigModel.setInstance(inst);
        seasons = inst;
    }
}
