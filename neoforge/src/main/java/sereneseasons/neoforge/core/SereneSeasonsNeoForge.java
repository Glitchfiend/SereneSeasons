package sereneseasons.neoforge.core;

import glitchcore.neoforge.GlitchCoreNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModClient;

@Mod(value = SereneSeasons.MOD_ID)
public class SereneSeasonsNeoForge
{
    public SereneSeasonsNeoForge(IEventBus bus)
    {
        bus.addListener(this::clientSetup);

        SereneSeasons.init();
        GlitchCoreNeoForge.prepareModEventHandlers(bus);
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(ModClient::setup);
    }
}
