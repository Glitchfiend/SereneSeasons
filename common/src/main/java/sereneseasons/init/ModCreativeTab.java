/*******************************************************************************
 * Copyright 2022, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sereneseasons.api.SSItems;
import sereneseasons.core.SereneSeasons;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

public class ModCreativeTab
{
    public static void registerCreativeTabs(BiConsumer<ResourceLocation, CreativeModeTab> func)
    {
        var ITEM_BLACKLIST = ImmutableList.of(SSItems.SS_ICON);
        var tab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .icon(() -> new ItemStack(SSItems.SS_ICON))
                .title(Component.translatable("itemGroup.tabSereneSeasons"))
                .displayItems((displayParameters, output) ->
                {
                    for (Field field : SSItems.class.getFields())
                    {
                        if (field.getType() != Item.class) continue;

                        try
                        {
                            Item item = (Item) field.get(null);
                            if (!ITEM_BLACKLIST.contains(item))
                                output.accept(new ItemStack(item));
                        }
                        catch (IllegalAccessException e)
                        {
                        }
                    }
                }).build();

        register(func, "main", tab);
    }

    private static CreativeModeTab register(BiConsumer<ResourceLocation, CreativeModeTab> func, String name, CreativeModeTab tab)
    {
        func.accept(new ResourceLocation(SereneSeasons.MOD_ID, name), tab);
        return tab;
    }
}
