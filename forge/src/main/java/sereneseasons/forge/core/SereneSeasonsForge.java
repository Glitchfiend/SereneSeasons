package sereneseasons.forge.core;

import glitchcore.forge.GlitchCoreForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModClient;

@Mod(value = SereneSeasons.MOD_ID)
public class SereneSeasonsForge
{
    public SereneSeasonsForge()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::clientSetup);

        SereneSeasons.init();
        GlitchCoreForge.prepareModEventHandlers(bus);
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(ModClient::setup);
    }
}
