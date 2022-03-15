/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.config;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public class BiomeConfig
{
    private static final List<String> INFERTILE_BIOMES = ImmutableList.of("biomesoplenty:wasteland");

    private static final List<String> LESS_COLOR_CHANGE_BIOMES = ImmutableList.of("minecraft:swamp", "biomesoplenty:boreal_forest",
            "biomesoplenty:bog", "biomesoplenty:mystic_grove", "biomesoplenty:tundra", "biomesoplenty:ominous_woods", "biomesoplenty:muskeg",
            "biomesoplenty:seasonal_forest", "biomesoplenty:pumpkin_patch", "biomesoplenty:dead_forest", "biomesoplenty:old_growth_dead_forest");


    public static boolean enablesSeasonalEffects(Holder<Biome> biome)
    {
        return !biome.is(key -> ServerConfig.blacklistedBiomes.get().contains(key.location().toString()));
    }

    public static boolean usesTropicalSeasons(Holder<Biome> biome)
    {
        return biome.is(key -> ServerConfig.tropicalBiomes.get().contains(key.location().toString())) || biome.value().getBaseTemperature() > 0.8F;
    }

    public static boolean infertileBiome(Holder<Biome> biome)
    {
        return biome.is(key -> INFERTILE_BIOMES.contains(key.location().toString()));
    }

    public static boolean lessColorChange(Holder<Biome> biome)
    {
        return biome.is(key -> LESS_COLOR_CHANGE_BIOMES.contains(key.location().toString()));
    }
}
