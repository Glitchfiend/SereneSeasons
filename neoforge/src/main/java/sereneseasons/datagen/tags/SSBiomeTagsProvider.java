/*******************************************************************************
 * Copyright 2026, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModTags;

import java.util.concurrent.CompletableFuture;

public class SSBiomeTagsProvider extends BiomeTagsProvider
{
    public SSBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, SereneSeasons.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries)
    {
        this.tag(ModTags.Biomes.BLACKLISTED_BIOMES).add(
            Biomes.LUSH_CAVES,
            Biomes.DRIPSTONE_CAVES,
            Biomes.DEEP_DARK,
            Biomes.OCEAN,
            Biomes.DEEP_OCEAN,
            Biomes.FROZEN_OCEAN,
            Biomes.DEEP_FROZEN_OCEAN,
            Biomes.COLD_OCEAN,
            Biomes.DEEP_COLD_OCEAN,
            Biomes.LUKEWARM_OCEAN,
            Biomes.DEEP_LUKEWARM_OCEAN,
            Biomes.WARM_OCEAN,
            Biomes.RIVER,
            Biomes.BEACH,
            Biomes.STONY_SHORE,
            Biomes.THE_VOID
        );

        this.tag(ModTags.Biomes.INFERTILE_BIOMES);

        this.tag(ModTags.Biomes.LESSER_COLOR_CHANGE_BIOMES).add(Biomes.SWAMP);

        this.tag(ModTags.Biomes.TROPICAL_BIOMES).add(
            Biomes.DESERT,
            Biomes.BADLANDS,
            Biomes.WOODED_BADLANDS,
            Biomes.ERODED_BADLANDS,
            Biomes.SAVANNA,
            Biomes.SAVANNA_PLATEAU,
            Biomes.WINDSWEPT_SAVANNA,
            Biomes.MANGROVE_SWAMP,
            Biomes.JUNGLE,
            Biomes.SPARSE_JUNGLE,
            Biomes.BAMBOO_JUNGLE,
            Biomes.MUSHROOM_FIELDS,
            Biomes.WARM_OCEAN
        );
    }
}
