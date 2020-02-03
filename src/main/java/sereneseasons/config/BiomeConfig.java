/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import sereneseasons.config.json.BiomeData;
import sereneseasons.util.config.JsonUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

public class BiomeConfig
{
    // We use a HashMap for maximum performance as JsonUtil#getOrCreateConfigFile will return a LinkedHashMap
    public static final Map<ResourceLocation, BiomeData> biomeDataMap = Maps.newHashMap();

    public static void init(File configDir)
    {
        Map<String, BiomeData> defaultBiomeData = Maps.newHashMap();
        addBlacklistedBiomes(defaultBiomeData);
        addTropicalBiomes(defaultBiomeData);
        addDisabledCropBiomes(defaultBiomeData);

        biomeDataMap.clear();

        Map<String, BiomeData> tmpBiomeDataMap = JsonUtil.getOrCreateConfigFile(configDir, "biome_info.json", defaultBiomeData, new TypeToken<Map<String, BiomeData>>(){}.getType());

        if (tmpBiomeDataMap != null && !tmpBiomeDataMap.isEmpty())
        {
            // We convert our keys to ResourceLocations here as to avoid calling `ResourceLocation#toString()` everywhere
            // This reduces CPU overhead and garbage collector pressure
            for (Map.Entry<String, BiomeData> entry : tmpBiomeDataMap.entrySet())
            {
                biomeDataMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
            }
        }
    }

    public static boolean enablesSeasonalEffects(Biome biome)
    {
        ResourceLocation name = biome.getRegistryName();

        if (biomeDataMap.containsKey(name))
        {
            return biomeDataMap.get(name).enableSeasonalEffects;
        }

        return true;
    }

    public static boolean usesTropicalSeasons(Biome biome)
    {
        ResourceLocation name = biome.getRegistryName();

        if (biomeDataMap.containsKey(name))
        {
            return biomeDataMap.get(name).useTropicalSeasons;
        }

        return false;
    }

    public static boolean disablesCrops(Biome biome)
    {
        ResourceLocation name = biome.getRegistryName();

        if (biomeDataMap.containsKey(name))
        {
            return biomeDataMap.get(name).disableCrops;
        }

        return false;
    }

    private static void addBlacklistedBiomes(Map<String, BiomeData> map)
    {
        List<String> blacklistedBiomes = Lists.newArrayList(
                "minecraft:mushroom_fields", "minecraft:mushroom_fields_shore", "minecraft:ocean", "minecraft:deep_ocean", "minecraft:frozen_ocean",
                "minecraft:deep_frozen_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:warm_ocean", "minecraft:deep_warm_ocean", "minecraft:river",

                "biomesoplenty:mystic_grove", "biomesoplenty:ominous_woods", "biomesoplenty:rainbow_valley", "biomesoplenty:wasteland");

        for (String biomeName : blacklistedBiomes)
        {
            if (!map.containsKey(biomeName))
                map.put(biomeName, new BiomeData(false, false, false));
            else
                map.get(biomeName).enableSeasonalEffects = false;
        }
    }

    private static void addTropicalBiomes(Map<String, BiomeData> map)
    {
        List<String> tropicalBiomes = Lists.newArrayList(
                "minecraft:jungle", "minecraft:jungle_hills", "minecraft:modified_jungle", "minecraft:jungle_edge", "minecraft:modified_jungle_edge",
                "minecraft:bamboo_jungle", "minecraft:bamboo_jungle_hills", "minecraft:mushroom_fields", "minecraft:mushroom_fields_shore", "minecraft:desert",
                "minecraft:desert_hills", "minecraft:desert_lakes", "minecraft:savanna", "minecraft:savanna_plateau", "minecraft:shattered_savanna",
                "minecraft:shattered_savanna_plateau", "minecraft:badlands", "minecraft:badlands_plateau", "minecraft:modified_badlands_plateau",
                "minecraft:wooded_badlands_plateau", "minecraft:modified_wooded_badlands_plateau", "minecraft:eroded_badlands",
                "minecraft:warm_ocean", "minecraft:deep_warm_ocean",

                "biomesoplenty:bayou", "biomesoplenty:brushland", "biomesoplenty:floodplain", "biomesoplenty:lush_grassland", "biomesoplenty:lush_swamp", "biomesoplenty:mangrove",
                "biomesoplenty:outback", "biomesoplenty:overgrown_cliffs", "biomesoplenty:rainforest", "biomesoplenty:scrubland",
                "biomesoplenty:tropical_rainforest", "biomesoplenty:wasteland", "biomesoplenty:xeric_shrubland",
                "biomesoplenty:tropics", "biomesoplenty:volcano", "biomesoplenty:oasis", "biomesoplenty:white_beach");

        for (String biomeName : tropicalBiomes)
        {
            if (!map.containsKey(biomeName))
                map.put(biomeName, new BiomeData(true, true, false));
            else
                map.get(biomeName).useTropicalSeasons = true;
        }
    }

    private static void addDisabledCropBiomes(Map<String, BiomeData> map)
    {
        List<String> disabledCropBiomes = Lists.newArrayList("biomesoplenty:wasteland", "biomesoplenty:volcano");

        for (String biomeName : disabledCropBiomes)
        {
            if (!map.containsKey(biomeName))
                map.put(biomeName, new BiomeData(false, false, true));
            else
                map.get(biomeName).disableCrops = true;
        }
    }
}
