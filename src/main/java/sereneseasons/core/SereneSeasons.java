package sereneseasons.core;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import sereneseasons.command.SSCommand;
import sereneseasons.init.ModBlocks;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModFertility;
import sereneseasons.init.ModHandlers;
import sereneseasons.init.ModItems;

@Mod(value = SereneSeasons.MOD_ID)
public class SereneSeasons
{
    public static final String MOD_ID = "sereneseasons";

    public static SereneSeasons instance;

    @SidedProxy(clientSide = "sereneseasons.core.ClientProxy", serverSide = "sereneseasons.core.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger = LogManager.getLogger(MOD_ID);
    public static File configDirectory;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        configDirectory = new File(event.getModConfigurationDirectory(), "sereneseasons");

        ModConfig.preInit(configDirectory);
        ModBlocks.init();
        ModItems.init();
        ModHandlers.init();

        proxy.registerRenderers();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ModConfig.init(configDirectory);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	ModFertility.init();
    	ModHandlers.postInit();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new SSCommand());
    }
}
