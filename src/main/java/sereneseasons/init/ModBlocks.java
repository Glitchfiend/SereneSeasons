package sereneseasons.init;

import static sereneseasons.api.SSBlocks.greenhouse_glass;
import static sereneseasons.api.SSBlocks.season_sensors;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import sereneseasons.block.BlockGreenhouseGlass;
import sereneseasons.block.BlockSeasonSensor;
import sereneseasons.tileentity.SeasonSensorTileEntity;

public class ModBlocks
{
    public static void init()
    {
    	greenhouse_glass = registerBlock( new BlockGreenhouseGlass(), "greenhouse_glass" );
    	
        season_sensors[0] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.SPRING), "season_sensor_spring" );
        season_sensors[1] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.SUMMER), "season_sensor_summer" ).setCreativeTab(null);
        season_sensors[2] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.AUTUMN), "season_sensor_autumn" ).setCreativeTab(null);
        season_sensors[3] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.WINTER), "season_sensor_winter" ).setCreativeTab(null);
        
        GameRegistry.registerTileEntity(SeasonSensorTileEntity.class, "season_sensor");
    }

    public static Block registerBlock(Block block, String name)
    {
        BlockItem itemBlock = new BlockItem(block, new Item.Properties().group(ItemGroupBOP.instance));
        block.setRegistryName(name);
        itemBlock.setRegistryName(name);
        ForgeRegistries.BLOCKS.register(block);
        ForgeRegistries.ITEMS.register(itemBlock);
        return block;
    }
}
