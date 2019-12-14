package sereneseasons.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.item.SeasonClockItem;
import sereneseasons.util.inventory.ItemGroupSS;

import static sereneseasons.api.SSItems.season_clock;
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
        season_clock = registerItem(new SeasonClockItem(new Item.Properties().maxStackSize(1).group(ItemGroupSS.instance)), "season_clock");
    }

    public static Item registerItem(Item item, String name)
    {
        item.setRegistryName(name);
        ForgeRegistries.ITEMS.register(item);
        return item;
    }
}
