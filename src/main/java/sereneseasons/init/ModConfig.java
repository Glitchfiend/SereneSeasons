/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.init;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import sereneseasons.config.ConfigHandler;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.core.SereneSeasons;
import sereneseasons.util.SeasonColourUtil;
import sereneseasons.util.config.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModConfig
{
    public static List<ConfigHandler> configHandlers = Lists.newArrayList();

    public static List<String> blacklistedBiomeNames = Lists.newArrayList();

    public static SeasonsConfig seasons;

    public static void preInit(File configDir)
    {
        seasons = new SeasonsConfig(new File(configDir, "seasons.cfg"));
    }

    public static void init(File configDir)
    {
        blacklistedBiomeNames = JsonUtil.getOrCreateConfigFile(configDir, "biome_blacklist.json", new ArrayList<String>(), new TypeToken<List<String>>(){}.getType());

        for (String biomeName : blacklistedBiomeNames)
        {
            ResourceLocation loc = new ResourceLocation(biomeName);

            if (ForgeRegistries.BIOMES.containsKey(loc))
            {
                SeasonColourUtil.biomeBlacklist.add(ForgeRegistries.BIOMES.getValue(loc));
            }
            else
            {
                SereneSeasons.logger.error("Cannot blacklist " + loc.toString() + " as it is an invalid biome!");
            }
        }
    }
}
