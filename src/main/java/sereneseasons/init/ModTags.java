package sereneseasons.init;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags
{
    public static void setup()
    {
        Blocks.setup();
        Items.setup();
    }

    public static class Blocks
    {
        private static void setup() {}

        public static final Tag.Named<Block> spring_crops = BlockTags.bind("sereneseasons:spring_crops");
        public static final Tag.Named<Block> summer_crops = BlockTags.bind("sereneseasons:summer_crops");
        public static final Tag.Named<Block> autumn_crops = BlockTags.bind("sereneseasons:autumn_crops");
        public static final Tag.Named<Block> winter_crops = BlockTags.bind("sereneseasons:winter_crops");

        public static final Tag.Named<Block> greenhouse_glass = BlockTags.bind("sereneseasons:greenhouse_glass");
        public static final Tag.Named<Block> unbreakable_infertile_crops = BlockTags.bind("sereneseasons:unbreakable_infertile_crops");
    }

    public static class Items
    {
        private static void setup() {}

        public static final Tag.Named<Item> spring_crops = ItemTags.bind("sereneseasons:spring_crops");
        public static final Tag.Named<Item> summer_crops = ItemTags.bind("sereneseasons:summer_crops");
        public static final Tag.Named<Item> autumn_crops = ItemTags.bind("sereneseasons:autumn_crops");
        public static final Tag.Named<Item> winter_crops = ItemTags.bind("sereneseasons:winter_crops");
    }
}
