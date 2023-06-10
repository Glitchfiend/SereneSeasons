/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import sereneseasons.api.SSBlocks;
import sereneseasons.api.SSItems;
import sereneseasons.core.SereneSeasons;

import java.lang.reflect.Field;
import java.util.List;

public class ModCreativeTab
{
    private static final List<RegistryObject<Item>> ITEM_BLACKLIST = ImmutableList.of(SSItems.SS_ICON);

    private static final List<RegistryObject<Block>> BLOCK_BLACKLIST = ImmutableList.of();

    public static void setup()
    {
        SereneSeasons.CREATIVE_TAB_REGISTER.register("main", () ->
        CreativeModeTab.builder()
            .icon(() -> new ItemStack(SSItems.SS_ICON.get()))
            .title(Component.translatable("itemGroup.tabSereneSeasons"))
            .displayItems((displayParameters, output) -> {
                // Add blocks
                for (Field field : SSBlocks.class.getFields())
                {
                    if (field.getType() != RegistryObject.class) continue;

                    try
                    {
                        RegistryObject<Block> block = (RegistryObject)field.get(null);
                        if (!BLOCK_BLACKLIST.contains(block))
                            output.accept(new ItemStack(block.get()));
                    }
                    catch (IllegalAccessException e) {}
                }

                // Add items
                for (Field field : SSItems.class.getFields())
                {
                    if (field.getType() != RegistryObject.class) continue;

                    try
                    {
                        RegistryObject<Item> item = (RegistryObject)field.get(null);
                        if (!ITEM_BLACKLIST.contains(item))
                            output.accept(new ItemStack(item.get()));
                    }
                    catch (IllegalAccessException e) {}
                }
            }).build()
        );
    }
}
