/*******************************************************************************
 * Copyright 2026, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.datagen.loot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import sereneseasons.api.SSBlocks;
import sereneseasons.core.SereneSeasons;

import java.util.Map;
import java.util.Set;

public class SSBlockLoot extends BlockLootSubProvider
{
    public SSBlockLoot(LootTableSubProvider.Context context)
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), context);
    }

    @Override
    protected void generate()
    {
        this.dropSelf(SSBlocks.SEASON_SENSOR);
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return BuiltInRegistries.BLOCK.entrySet().stream().filter(e -> e.getKey().identifier().getNamespace().equals(SereneSeasons.MOD_ID)).map(Map.Entry::getValue).toList();
    }
}
