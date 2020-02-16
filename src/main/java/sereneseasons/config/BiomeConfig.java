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

        return biome.getTemperature() > 0.8F;
    }

    public static boolean infertileBiome(Biome biome)
    {
        List<String> infertileBiomes = Lists.newArrayList("minecraft:nether", "minecraft:the_end", "minecraft:small_end_islands", "minecraft:end_midlands",
                "minecraft:end_highlands", "minecraft:end_barrens", "minecraft:the_void",
                "biomesoplenty:ominous_woods", "biomesoplenty:wasteland", "biomesoplenty:volcano", "biomesoplenty:volcano_edge");

        String name = biome.getRegistryName().toString();

        if (infertileBiomes.contains(name))
        {
            return true;
        }

        return false;
    }

    public static boolean lessColorChange(Biome biome)
    {
        List<String> lessColorChangeBiomes = Lists.newArrayList("minecraft:swamp", "minecraft:swamp_hills",
                "biomesoplenty:bog", "biomesoplenty:mire", "biomesoplenty:ominous_woods");

        String name = biome.getRegistryName().toString();

        if (lessColorChangeBiomes.contains(name))
        {
            return true;
        }

        return false;
    }

    private static void addBlacklistedBiomes(Map<String, BiomeData> map)
    {
        List<String> blacklistedBiomes = Lists.newArrayList("minecraft:mushroom_fields", "minecraft:mushroom_fields_shore", "minecraft:ocean",
                "minecraft:deep_ocean", "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean", "minecraft:deep_warm_ocean", "minecraft:river", "minecraft:the_void",

                "biomesoplenty:mystic_grove", "biomesoplenty:origin_beach", "biomesoplenty:origin_hills", "biomesoplenty:rainbow_valley");

        for (String biomeName : blacklistedBiomes)
        {
            if (!map.containsKey(biomeName))
                map.put(biomeName, new BiomeData(false, false));
            else
                map.get(biomeName).enableSeasonalEffects = false;
        }
    }

    private static void addTropicalBiomes(Map<String, BiomeData> map)
    {
        List<String> tropicalBiomes = Lists.newArrayList("minecraft:warm_ocean", "minecraft:deep_warm_ocean");

        for (String biomeName : tropicalBiomes)
        {
            if (!map.containsKey(biomeName))
                map.put(biomeName, new BiomeData(true, true));
            else
                map.get(biomeName).useTropicalSeasons = true;
        }
    }
}
