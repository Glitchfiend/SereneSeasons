package sereneseasons.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.block.SeasonSensorBlock;
import sereneseasons.core.SereneSeasons;
import sereneseasons.tileentity.SeasonSensorTileEntity;
import sereneseasons.util.inventory.ItemGroupSS;

import java.util.function.Supplier;

import static sereneseasons.api.SSBlocks.*;

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
        season_sensor_tile_entity = registerTileEntityType("season_sensor", SeasonSensorTileEntity::new, season_sensor);
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

    public static <T extends BlockEntity> BlockEntityType<T> registerTileEntityType(String name, Supplier<? extends T> factoryIn, Block... validBlocks)
    {
        BlockEntityType type = BlockEntityType.Builder.of(factoryIn, validBlocks).build(null);
        type.setRegistryName(name);
        ForgeRegistries.TILE_ENTITIES.register(type);
        return type;
    }
}
