package sereneseasons.util.inventory;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import sereneseasons.api.SSItems;

public class ItemGroupSS extends CreativeModeTab
{
    public static final ItemGroupSS instance = new ItemGroupSS(CreativeModeTab.TABS.length, "tabSereneSeasons");

    private ItemGroupSS(int index, String label)
    {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon()
    {
        return new ItemStack(SSItems.ss_icon);
    }
}
