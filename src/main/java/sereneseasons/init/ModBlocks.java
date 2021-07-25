package sereneseasons.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.block.SeasonSensorBlock;
import sereneseasons.tileentity.SeasonSensorBlockEntity;
import sereneseasons.util.inventory.ItemGroupSS;

import static sereneseasons.api.SSBlocks.season_sensor;
import static sereneseasons.api.SSBlocks.season_sensor_tile_entity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks
{
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        season_sensor = registerBlock(new SeasonSensorBlock(Block.Properties.of(Material.STONE).strength(0.2F).sound(SoundType.STONE)), "season_sensor");
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event)
    {
        season_sensor_tile_entity = registerBlockEntityType("season_sensor", SeasonSensorBlockEntity::new, season_sensor);
    }

    public static Block registerBlock(Block block, String name)
    {
        BlockItem itemBlock = new BlockItem(block, new Item.Properties().tab(ItemGroupSS.instance));
        block.setRegistryName(name);
        itemBlock.setRegistryName(name);
        ForgeRegistries.BLOCKS.register(block);
        ForgeRegistries.ITEMS.register(itemBlock);
        return block;
    }

    public static Block registerBlockNoGroup(Block block, String name)
    {
        BlockItem itemBlock = new BlockItem(block, new Item.Properties().tab(null));
        block.setRegistryName(name);
        itemBlock.setRegistryName(name);
        ForgeRegistries.BLOCKS.register(block);
        ForgeRegistries.ITEMS.register(itemBlock);
        return block;
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntityType(String name, BlockEntityType.BlockEntitySupplier<? extends T> factoryIn, Block... validBlocks)
    {
        BlockEntityType type = BlockEntityType.Builder.of(factoryIn, validBlocks).build(null);
        type.setRegistryName(name);
        ForgeRegistries.BLOCK_ENTITIES.register(type);
        return type;
    }
}
