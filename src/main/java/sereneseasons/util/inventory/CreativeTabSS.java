package sereneseasons.util.inventory;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import sereneseasons.api.SSItems;

public class CreativeTabSS extends CreativeTabs
{
    public static final CreativeTabs instance = new CreativeTabSS(CreativeTabs.getNextID(), "tabSereneSeasons");

    private CreativeTabSS(int index, String label)
    {
        super(index, label);
    }

    @Override
    public Item getTabIconItem()
    {
        return SSItems.ss_icon;
    }
}
