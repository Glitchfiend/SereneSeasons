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
    	List<String> blacklistedBiomes = Lists.newArrayList("minecraft:desert", "minecraft:desert_hills",
    			"minecraft:mutated_desert", "minecraft:jungle", "minecraft:jungle_hills", "minecraft:jungle_edge",
    			"minecraft:mutated_jungle", "minecraft:mutated_jungle_edge", "minecraft:mesa", "minecraft:mesa_rock",
    			"minecraft:mesa_clear_rock", "minecraft:mutated_mesa", "minecraft:mutated_mesa_rock",
    			"minecraft:mutated_mesa_clear_rock", "minecraft:mushroom_island", "minecraft:mushroom_island_shore",
    			"minecraft:savanna", "minecraft:savanna_rock", "minecraft:mutated_savanna",
    			"minecraft:mutated_savanna_rock", "biomesoplenty:bamboo_forest", "biomesoplenty:bayou",
    			"biomesoplenty:brushland", "biomesoplenty:eucalyptus_forest", "biomesoplenty:lush_desert",
    			"biomesoplenty:mangrove", "biomesoplenty:mystic_grove", "biomesoplenty:ominous_woods",
    			"biomesoplenty:outback", "biomesoplenty:overgrown_cliffs", "biomesoplenty:rainforest",
    			"biomesoplenty:sacred_springs", "biomesoplenty:tropical_rainforest", "biomesoplenty:wasteland",
    			"biomesoplenty:xeric_shrubland", "biomesoplenty:flower_island", "biomesoplenty:origin_island",
    			"biomesoplenty:tropical_island", "biomesoplenty:volcanic_island", "biomesoplenty:oasis",
    			"biomesoplenty:white_beach");
    	
        blacklistedBiomeNames = JsonUtil.getOrCreateConfigFile(configDir, "biome_blacklist.json", blacklistedBiomes, new TypeToken<List<String>>(){}.getType());

        for (String biomeName : blacklistedBiomeNames)
        {
            ResourceLocation loc = new ResourceLocation(biomeName);

            if (ForgeRegistries.BIOMES.containsKey(loc))
            {
                SeasonColourUtil.biomeBlacklist.add(ForgeRegistries.BIOMES.getValue(loc));
            }
        }
    }
}
