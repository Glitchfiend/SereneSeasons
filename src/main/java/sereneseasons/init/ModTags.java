package sereneseasons.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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

        public static final TagKey<Block> SPRING_CROPS = BlockTags.create(new ResourceLocation("sereneseasons:spring_crops"));
        public static final TagKey<Block> SUMMER_CROPS = BlockTags.create(new ResourceLocation("sereneseasons:summer_crops"));
        public static final TagKey<Block> AUTUMN_CROPS = BlockTags.create(new ResourceLocation("sereneseasons:autumn_crops"));
        public static final TagKey<Block> WINTER_CROPS = BlockTags.create(new ResourceLocation("sereneseasons:winter_crops"));

        public static final TagKey<Block> GREENHOUSE_GLASS = BlockTags.create(new ResourceLocation("sereneseasons:greenhouse_glass"));
        public static final TagKey<Block> UNBREAKABLE_INFERTILE_CROPS = BlockTags.create(new ResourceLocation("sereneseasons:unbreakable_infertile_crops"));
    }

    public static class Items
    {
        private static void setup() {}

        public static final TagKey<Item> SPRING_CROPS = ItemTags.create(new ResourceLocation("sereneseasons:spring_crops"));
        public static final TagKey<Item> SUMMER_CROPS = ItemTags.create(new ResourceLocation("sereneseasons:summer_crops"));
        public static final TagKey<Item> AUTUMN_CROPS = ItemTags.create(new ResourceLocation("sereneseasons:autumn_crops"));
        public static final TagKey<Item> WINTER_CROPS = ItemTags.create(new ResourceLocation("sereneseasons:winter_crops"));
    }
}
