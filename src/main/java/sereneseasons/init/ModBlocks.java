package sereneseasons.init;

import static sereneseasons.api.SSBlocks.greenhouse_glass;
import static sereneseasons.api.SSBlocks.season_sensors;

import com.google.common.base.Preconditions;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import sereneseasons.api.ISSBlock;
import sereneseasons.block.BlockGreenhouseGlass;
import sereneseasons.block.BlockSeasonSensor;
import sereneseasons.core.SereneSeasons;
import sereneseasons.tileentity.TileEntitySeasonSensor;
import sereneseasons.util.inventory.CreativeTabSS;

public class ModBlocks
{
    public static void init()
    {
    	greenhouse_glass = registerBlock( new BlockGreenhouseGlass(), "greenhouse_glass" );
    	
        season_sensors[0] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.SPRING), "season_sensor_spring" );
        season_sensors[1] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.SUMMER), "season_sensor_summer" ).setCreativeTab(null);
        season_sensors[2] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.AUTUMN), "season_sensor_autumn" ).setCreativeTab(null);
        season_sensors[3] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.WINTER), "season_sensor_winter" ).setCreativeTab(null);
        
        GameRegistry.registerTileEntity(TileEntitySeasonSensor.class, "season_sensor");
    }

    public static void registerBlockItemModel(Block block, String stateName, int stateMeta)
    {
        Item item = Item.getItemFromBlock(block);
        SereneSeasons.proxy.registerItemVariantModel(item, stateName, stateMeta);
    }

    public static Block registerBlock(Block block, String blockName)
    {
        // by default, set the creative tab for all blocks added in BOP to CreativeTabBOP.instance
        return registerBlock(block, blockName, CreativeTabSS.instance);
    }

    public static Block registerBlock(Block block, String blockName,CreativeTabs tab)
    {
        return registerBlock(block, blockName, tab, true);
    }

    public static Block registerBlock(Block block, String blockName, CreativeTabs tab, boolean registerItemModels)
    {
        Preconditions.checkNotNull(block, "Cannot register a null block");
        block.setBlockName(blockName);
        block.setCreativeTab(tab);

        if (block instanceof ISSBlock)
        {
            // if this block supports the IBOPBlock interface then we can determine the item block class, and sub-blocks automatically
            ISSBlock bopBlock = (ISSBlock) block;

            registerBlockWithItem(block, blockName, bopBlock.getItemClass());
            SereneSeasons.proxy.registerBlockSided(block);
        }
        else
        {
            // for vanilla blocks, just register a single variant with meta=0 and assume ItemBlock for the item class
            registerBlockWithItem(block, blockName, ItemBlock.class);
            registerBlockItemModel(block, blockName, 0);
        }

        return block;
    }

    private static void registerBlockWithItem(Block block, String blockName, Class<? extends ItemBlock> clazz)
    {
        block.setBlockName(blockName);
        GameRegistry.registerBlock(block, clazz, blockName);
    }
}
