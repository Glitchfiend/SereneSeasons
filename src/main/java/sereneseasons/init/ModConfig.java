/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.init;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.ConfigHandler;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.config.json.BiomeData;
import sereneseasons.core.SereneSeasons;
import sereneseasons.util.SeasonColourUtil;
import sereneseasons.util.config.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModConfig
{
    public static List<ConfigHandler> configHandlers = Lists.newArrayList();

    public static SeasonsConfig seasons;

    public static void preInit(File configDir)
    {
        seasons = new SeasonsConfig(new File(configDir, "seasons.cfg"));
    }

    public static void init(File configDir)
    {
        BiomeConfig.init(configDir);
    }
}
