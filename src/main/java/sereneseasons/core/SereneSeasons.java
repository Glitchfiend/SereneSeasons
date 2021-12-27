package sereneseasons.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sereneseasons.handler.season.BirchColorHandler;
import sereneseasons.init.*;

@Mod(value = SereneSeasons.MOD_ID)
public class SereneSeasons
{
    public static final String MOD_ID = "sereneseasons";

    public static SereneSeasons instance;
    public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static Logger logger = LogManager.getLogger(MOD_ID);

    public SereneSeasons()
    {
        instance = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);

        ModHandlers.init();
        ModConfig.init();
        ModGameRules.init();
        ModTags.setup();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        BirchColorHandler.setup();
        proxy.registerItemModelsProperties();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    private void loadComplete(final FMLLoadCompleteEvent event)
    {
    }

    private void serverAboutToStart(final ServerAboutToStartEvent event) {}
}
