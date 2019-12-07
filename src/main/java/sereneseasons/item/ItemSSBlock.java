package sereneseasons.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import sereneseasons.api.ISSBlock;

public class ItemSSBlock extends ItemBlock
{

    public ISSBlock tanBlock;

    public ItemSSBlock(Block block)
    {
        super(block);
        if (block instanceof ISSBlock)
        {
            this.tanBlock = (ISSBlock)block;
        }
        else
        {
            throw new IllegalArgumentException("ItemBOPBlock must be created with a block implementing IBOPBlock");
        }
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int metadata)
    {
        return metadata;
    }
}
