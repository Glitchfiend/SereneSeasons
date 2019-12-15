package sereneseasons.api;

import net.minecraft.item.ItemBlock;

public interface ISSBlock {
    
    public Class<? extends ItemBlock> getItemClass();
    
}