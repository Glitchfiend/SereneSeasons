package sereneseasons.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import sereneseasons.core.SereneSeasons;
import sereneseasons.item.CalendarItem;
import sereneseasons.util.inventory.CreativeModeTabSS;

import java.util.function.Supplier;

import static sereneseasons.api.SSItems.CALENDAR;
import static sereneseasons.api.SSItems.SS_ICON;

public class ModItems
{
    public static void setup()
    {
        registerItems();
    }

    private static void registerItems()
    {
    	// SS Creative Tab Icon
        SS_ICON = registerItem(() -> new Item(new Item.Properties()), "ss_icon");

        // Main Items
        CALENDAR = registerItem(() -> new CalendarItem(new Item.Properties().stacksTo(1).tab(CreativeModeTabSS.INSTANCE)), "calendar");
    }

    public static RegistryObject<Item> registerItem(Supplier<Item> itemSupplier, String name)
    {
        return SereneSeasons.ITEM_REGISTER.register(name, itemSupplier);
    }
}
