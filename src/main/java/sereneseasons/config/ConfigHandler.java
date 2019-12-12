/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.config;

import net.minecraftforge.common.MinecraftForge;
import sereneseasons.api.config.ISyncedOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.init.ModConfig;

import java.io.File;

public abstract class ConfigHandler
{
    public final String description;

    protected ConfigHandler(File configFile, String description)
    {
        loadConfiguration();

        MinecraftForge.EVENT_BUS.register(this);
        this.description = description;
        ModConfig.configHandlers.add(this);
    }

    protected abstract void loadConfiguration();

    protected <T> void addSyncedValue(ISyncedOption option, T defaultValue, String category, String comment, T... args)
    {
        SyncedConfig.addOption(option, defaultValue.toString());
    }
}
