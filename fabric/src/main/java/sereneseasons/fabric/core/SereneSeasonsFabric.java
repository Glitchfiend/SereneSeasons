/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.fabric.core;

import glitchcore.fabric.GlitchCoreInitializer;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModClient;

public class SereneSeasonsFabric implements GlitchCoreInitializer
{
    @Override
    public void onInitialize()
    {
        SereneSeasons.init();
    }

    @Override
    public void onInitializeClient()
    {
        ModClient.setup();
    }
}
