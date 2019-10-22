package sereneseasons.util.inventory;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import sereneseasons.api.SSItems;

public class ItemGroupSS extends ItemGroup
{
    public static final ItemGroupSS instance = new ItemGroupSS(ItemGroup.GROUPS.length, "tabSereneSeasons");

    private ItemGroupSS(int index, String label)
    {
        super(index, label);
    }

    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(SSItems.ss_icon);
    }
}
