package sereneseasons.init;

import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
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
        SereneSeasons.logger.info("Registering blocks...");
    	greenhouse_glass = registerBlock(new GlassBlock(Block.Properties.of(Material.GLASS, MaterialColor.COLOR_GREEN).strength(0.3F).sound(SoundType.GLASS).noOcclusion()), "greenhouse_glass");

        season_sensors[0] = registerBlock(new SeasonSensorBlock(Block.Properties.of(Material.STONE).strength(0.2F).sound(SoundType.STONE), SeasonSensorBlock.DetectorType.SPRING), "season_sensor_spring");
        season_sensors[1] = registerBlockNoGroup(new SeasonSensorBlock(Block.Properties.of(Material.STONE).strength(0.2F).sound(SoundType.STONE), SeasonSensorBlock.DetectorType.SUMMER), "season_sensor_summer");
        season_sensors[2] = registerBlockNoGroup(new SeasonSensorBlock(Block.Properties.of(Material.STONE).strength(0.2F).sound(SoundType.STONE), SeasonSensorBlock.DetectorType.AUTUMN), "season_sensor_autumn");
        season_sensors[3] = registerBlockNoGroup(new SeasonSensorBlock(Block.Properties.of(Material.STONE).strength(0.2F).sound(SoundType.STONE), SeasonSensorBlock.DetectorType.WINTER), "season_sensor_winter");

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            RenderType translucentRenderType = RenderType.translucent();
            RenderTypeLookup.setRenderLayer(greenhouse_glass, translucentRenderType);
        }
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
    {
        SereneSeasons.logger.info("Registering tile entities...");
        season_sensor_tile_entity = registerTileEntityType("season_sensor", SeasonSensorTileEntity::new, season_sensors);
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

    public static <T extends TileEntity> TileEntityType<T> registerTileEntityType(String name, Supplier<? extends T> factoryIn, Block... validBlocks)
    {
        TileEntityType type = TileEntityType.Builder.of(factoryIn, validBlocks).build(null);
        type.setRegistryName(name);
        ForgeRegistries.TILE_ENTITIES.register(type);
        return type;
    }
}
