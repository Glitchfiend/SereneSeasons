package sereneseasons.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.item.CalendarItem;
import sereneseasons.util.inventory.ItemGroupSS;

import static sereneseasons.api.SSItems.calendar;
import static sereneseasons.api.SSItems.ss_icon;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
    	// SS Creative Tab Icon
        ss_icon = registerItem(new Item(new Item.Properties()), "ss_icon");

        // Main Items
        calendar = registerItem(new CalendarItem(new Item.Properties().stacksTo(1).tab(ItemGroupSS.instance)), "calendar");
    }

    public static Item registerItem(Item item, String name)
    {
        item.setRegistryName(name);
        ForgeRegistries.ITEMS.register(item);
        return item;
    }
}
