package sereneseasons.forge.core;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModClient;

@Mod(value = SereneSeasons.MOD_ID)
public class SereneSeasonsForge
{
    public SereneSeasonsForge(FMLJavaModLoadingContext context)
    {
        IEventBus bus = context.getModEventBus();
        bus.addListener(this::clientSetup);

        SereneSeasons.init();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(ModClient::setup);
    }
}
