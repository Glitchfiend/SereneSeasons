package sereneseasons.core;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import sereneseasons.api.SSBlocks;
import sereneseasons.api.SSItems;
import sereneseasons.command.SSCommand;
import sereneseasons.init.ModBlocks;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModFertility;
import sereneseasons.init.ModHandlers;
import sereneseasons.init.ModItems;

@Mod(modid = SereneSeasons.MOD_ID, version = SereneSeasons.MOD_VERSION, name = SereneSeasons.MOD_NAME, dependencies = "required-after:AppleCore")
public class SereneSeasons
{
    public static final String MOD_NAME = "Serene Seasons";
    public static final String MOD_ID = "sereneseasons";
    public static final String MOD_VERSION = "@MOD_VERSION@";

    @Instance(MOD_ID)
    public static SereneSeasons instance;

    @SidedProxy(clientSide = "sereneseasons.core.CommonProxy", serverSide = "sereneseasons.core.CommonProxy")
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

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(SSBlocks.greenhouse_glass, 1), new Object[] {
            "#G#", "GWG", "#G#",
            '#', "dyeCyan",
            'W', "plankWood",
            'G', "blockGlass"
        }));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(SSItems.season_clock, 1), new Object[] {
            " # ", "#R#", " # ",
            '#', "gemQuartz",
            'R', "dustRedstone"
        }));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(SSBlocks.season_sensors[0], 1), new Object[] {
            "GGG", "QSQ", "###",
            '#', new ItemStack(Blocks.stone_slab, 1, 3),
            'S', SSItems.season_clock,
            'Q', "gemQuartz",
            'G', "blockGlass"
        }));

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
