/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerationHandler
{
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // Client
        generator.addProvider(event.includeClient(), new SSBlockStateProvider(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new SSItemModelProvider(generator, existingFileHelper));
    }
}
