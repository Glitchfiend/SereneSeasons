package sereneseasons.init;

import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.block.BlockSeasonSensor;
import sereneseasons.tileentity.SeasonSensorTileEntity;
import sereneseasons.util.inventory.ItemGroupSS;

import static sereneseasons.api.SSBlocks.greenhouse_glass;
import static sereneseasons.api.SSBlocks.season_sensors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks
{
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
    	greenhouse_glass = registerBlock(new GlassBlock(Block.Properties.create(Material.GLASS, MaterialColor.GREEN).hardnessAndResistance(0.3F).sound(SoundType.GLASS)), "greenhouse_glass");
    	
        season_sensors[0] = registerBlock(new BlockSeasonSensor(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.2F).sound(SoundType.WOOD), BlockSeasonSensor.DetectorType.SPRING), "season_sensor_spring");
        season_sensors[1] = registerBlock(new BlockSeasonSensor(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.2F).sound(SoundType.WOOD), BlockSeasonSensor.DetectorType.SUMMER), "season_sensor_summer");
        season_sensors[2] = registerBlock(new BlockSeasonSensor(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.2F).sound(SoundType.WOOD), BlockSeasonSensor.DetectorType.AUTUMN), "season_sensor_autumn");
        season_sensors[3] = registerBlock(new BlockSeasonSensor(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.2F).sound(SoundType.WOOD), BlockSeasonSensor.DetectorType.WINTER), "season_sensor_winter");
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
    {
        event.getRegistry().register(TileEntityType.Builder.create(SeasonSensorTileEntity::new, season_sensors).build(null).setRegistryName("season_sensor"));
    }

    public static Block registerBlock(Block block, String name)
    {
        BlockItem itemBlock = new BlockItem(block, new Item.Properties().group(ItemGroupSS.instance));
        block.setRegistryName(name);
        itemBlock.setRegistryName(name);
        ForgeRegistries.BLOCKS.register(block);
        ForgeRegistries.ITEMS.register(itemBlock);
        return block;
    }
}
