/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import sereneseasons.util.biome.BiomeUtil;
import sereneseasons.util.config.JsonUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

public class BiomeConfig
{
    public static boolean enablesSeasonalEffects(ResourceKey<Biome> key)
    {
        if (key == null) return false;

        if (ServerConfig.blacklistedBiomes.get().contains(key.location().toString()))
        {
            return false;
        }

        return true;
    }

    public static boolean usesTropicalSeasons(ResourceKey<Biome> key)
    {
        if (key == null) return false;

        Biome biome = BiomeUtil.getBiome(key);

        if (ServerConfig.tropicalBiomes.get().contains(key.location().toString()))
        {
            return true;
        }

        return biome.getBaseTemperature() > 0.8F;
    }

    public static boolean infertileBiome(ResourceKey<Biome> biome)
    {
        if (biome == null) return false;

        List<String> infertileBiomes = Lists.newArrayList("biomesoplenty:wasteland");

        String name = biome.location().toString();

        if (infertileBiomes.contains(name))
        {
            return true;
        }

        return false;
    }

    public static boolean lessColorChange(ResourceKey<Biome> biome)
    {
        if (biome == null) return false;

        List<String> lessColorChangeBiomes = Lists.newArrayList("minecraft:swamp", "minecraft:swamp_hills",
                "biomesoplenty:mystic_grove", "biomesoplenty:mystic_plains", "biomesoplenty:tundra", "biomesoplenty:tundra_basin", "biomesoplenty:tundra_bog",
                "biomesoplenty:ominous_woods", "biomesoplenty:ominous_mire", "biomesoplenty:muskeg", "biomesoplenty:seasonal_forest", "biomesoplenty:seasonal_orchard",
                "biomesoplenty:seasonal_pumpkin_patch", "biomesoplenty:dead_forest", "biomesoplenty:tall_dead_forest", "biomesoplenty:burnt_forest");

        String name = biome.location().toString();

        if (lessColorChangeBiomes.contains(name))
        {
            return true;
        }

        return false;
    }
}
