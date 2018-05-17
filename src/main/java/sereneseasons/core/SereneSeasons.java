package sereneseasons.core;

import java.io.File;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import sereneseasons.command.SSCommand;
import sereneseasons.init.*;

@Mod(modid = SereneSeasons.MOD_ID, version = SereneSeasons.MOD_VERSION, name = SereneSeasons.MOD_NAME, dependencies = "required-after:forge@[1.0.0.0,)")
public class SereneSeasons
{
    public static final String MOD_NAME = "Serene Seasons";
    public static final String MOD_ID = "sereneseasons";
    public static final String MOD_VERSION = "@MOD_VERSION@";

    @Instance(MOD_ID)
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
        ModFertility.init();

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

    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new SSCommand());
    }
}
