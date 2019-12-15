package sereneseasons.init;

import static sereneseasons.api.SSItems.ss_icon;
import static sereneseasons.api.SSItems.season_clock;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import sereneseasons.core.SereneSeasons;
import sereneseasons.item.ItemSeasonClock;
import sereneseasons.util.inventory.CreativeTabSS;

public class ModItems
{
    public static void init()
    {
    	registerItems();
    }
    
    public static void registerItems()
    {
        // SS Creative Tab Icon
        ss_icon = registerItem(new Item(), "ss_icon").setTextureName("sereneseasons:ss_icon");
        ss_icon.setCreativeTab(null);

        // Main Items
        season_clock = registerItem(new ItemSeasonClock(), "season_clock");
    }

    public static Item registerItem(Item item, String name)
    {
        return registerItem(item, name, CreativeTabSS.instance);
    }

    public static Item registerItem(Item item, String name, CreativeTabs tab)
    {
        item.setUnlocalizedName(name);
        if (tab != null)
        {
            item.setCreativeTab(CreativeTabSS.instance);
        }

        item.setUnlocalizedName(name);
        GameRegistry.registerItem(item, name);
        SereneSeasons.proxy.registerItemSided(item);

        return item;
    }
}
