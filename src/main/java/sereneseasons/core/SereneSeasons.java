package sereneseasons.core;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sereneseasons.api.SSBlocks;
import sereneseasons.api.SSItems;
import sereneseasons.handler.season.SeasonColorHandlers;
import sereneseasons.init.*;

import java.lang.reflect.Field;
import java.util.List;

@Mod(value = SereneSeasons.MOD_ID)
public class SereneSeasons
{
    public static final String MOD_ID = "sereneseasons";

    public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(Registries.BLOCK, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(Registries.ITEM, MOD_ID);

    public SereneSeasons()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::loadComplete);
        bus.addListener(this::registerTab);

        BLOCK_REGISTER.register(bus);
        BLOCK_ENTITY_REGISTER.register(bus);
        ITEM_REGISTER.register(bus);

        ModBlocks.setup();
        ModItems.setup();
        ModHandlers.init();
        ModConfig.init();
        ModGameRules.init();
        ModTags.setup();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            SeasonColorHandlers.setup();
            proxy.registerItemModelsProperties();
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    private void loadComplete(final FMLLoadCompleteEvent event)
    {
    }

    private void registerTab(CreativeModeTabEvent.Register event)
    {
        List<RegistryObject<Item>> itemBlacklist = List.of(SSItems.SS_ICON);
        List<RegistryObject<Block>> blockBlacklist = List.of();

        event.registerCreativeModeTab(new ResourceLocation(SereneSeasons.MOD_ID, "main"), builder ->
        {
            builder.icon(() -> new ItemStack(SSItems.SS_ICON.get()))
                .title(Component.translatable("itemGroup.tabSereneSeasons"))
                .displayItems((featureFlags, output, hasOp) -> {
                    // Add items
                    for (Field field : SSItems.class.getFields())
                    {
                        if (field.getType() != RegistryObject.class) continue;

                        try
                        {
                            RegistryObject<Item> item = (RegistryObject) field.get(null);
                            if (!itemBlacklist.contains(item))
                                output.accept(new ItemStack(item.get()));
                        }
                        catch (IllegalAccessException e) {}
                    }

                    // Add blocks
                    for (Field field : SSBlocks.class.getFields())
                    {
                        if (field.getType() != RegistryObject.class) continue;

                        try
                        {
                            RegistryObject<Block> block = (RegistryObject) field.get(null);
                            if (!blockBlacklist.contains(block))
                                output.accept(new ItemStack(block.get()));
                        }
                        catch (IllegalAccessException e) {}
                    }
                });
        });
    }
}
