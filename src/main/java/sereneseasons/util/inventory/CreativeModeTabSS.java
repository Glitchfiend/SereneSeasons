package sereneseasons.util.inventory;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import sereneseasons.api.SSItems;

public class CreativeModeTabSS extends CreativeModeTab
{
    public static final CreativeModeTabSS INSTANCE = new CreativeModeTabSS(CreativeModeTab.TABS.length, "tabSereneSeasons");

    private CreativeModeTabSS(int index, String label)
    {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon()
    {
        return new ItemStack(SSItems.SS_ICON.get());
    }
}
