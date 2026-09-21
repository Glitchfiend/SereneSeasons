/*******************************************************************************
 * Copyright 2026, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.datagen.provider;

import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import sereneseasons.datagen.loot.SSBlockLoot;

import java.util.List;
import java.util.Set;

public class SSLootTableProvider
{
    public static SingleRegistryBootstrap<LootTable> create()
    {
        return new LootTableProvider(Set.of(), List.of(new LootTableProvider.SubProviderEntry(SSBlockLoot::new, LootContextParamSets.BLOCK)));
    }
}
