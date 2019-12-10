package sereneseasons.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sereneseasons.command.SSCommand;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModFertility;
import sereneseasons.init.ModHandlers;

import java.io.File;

@Mod(value = SereneSeasons.MOD_ID)
public class SereneSeasons
{
    public static final String MOD_ID = "sereneseasons";

    public static SereneSeasons instance;
    public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static Logger logger = LogManager.getLogger(MOD_ID);
    public static File configDirectory;


    public SereneSeasons()
    {
        instance = this;

        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);

        ModHandlers.init();

        proxy.registerRenderers();

        ModConfig.init(configDirectory);

        ModFertility.init();
        ModHandlers.postInit();
    }

    public void serverStarting(FMLServerStartingEvent evt)
    {
        event.registerServerCommand(new SSCommand());
    }
}
