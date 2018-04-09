package sereneseasons.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import sereneseasons.core.SereneSeasons;
import sereneseasons.item.ItemSeasonClock;
import sereneseasons.util.inventory.CreativeTabSS;

import static sereneseasons.api.SSItems.*;

public class ModItems
{
    public static void init()
    {
    	registerItems();
        //setupModels();
    }
    
    public static void registerItems()
    {
    	// SS Creative Tab Icon
    	ss_icon = registerItem(new Item(), "ss_icon");
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

        item.setRegistryName(new ResourceLocation(SereneSeasons.MOD_ID, name));
        ForgeRegistries.ITEMS.register(item);
        SereneSeasons.proxy.registerItemSided(item);

        return item;
    }
}
