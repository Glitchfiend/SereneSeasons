/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.datagen;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import sereneseasons.core.SereneSeasons;
import sereneseasons.datagen.models.SSModelProvider;
import sereneseasons.datagen.provider.SSLootTableProvider;
import sereneseasons.datagen.provider.SSRecipeProvider;
import sereneseasons.datagen.tags.SSBiomeTagsProvider;
import sereneseasons.datagen.tags.SSBlockTagsProvider;
import sereneseasons.datagen.tags.SSItemTagsProvider;

import java.util.Set;

@EventBusSubscriber(modid = SereneSeasons.MOD_ID)
public class DataGenerationHandler
{
    private static final RegistrySetBuilder RELOADABLE_BUILDER = new RegistrySetBuilder()
            .add(Registries.LOOT_TABLE, SSLootTableProvider.create())
            .add(SSRecipeProvider.create());

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        // Loot tables and recipes
        generator.addProvider(true, DatapackBuiltinEntriesProvider.forReloadableLayer(
                output, "SS Reloadable Registries", event.getWorldLookupProvider(), event.getReloadableLookupProvider(),
                RELOADABLE_BUILDER, Set.of(SereneSeasons.MOD_ID)));

        // Tags
        generator.addProvider(true, new SSBlockTagsProvider(output, event.getWorldLookupProvider()));
        generator.addProvider(true, new SSItemTagsProvider(output, event.getWorldLookupProvider()));
        generator.addProvider(true, new SSBiomeTagsProvider(output, event.getWorldLookupProvider()));

        // Client
        generator.addProvider(true, new SSModelProvider(output));
    }
}
