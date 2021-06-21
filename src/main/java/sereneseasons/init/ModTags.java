package sereneseasons.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import sereneseasons.core.SereneSeasons;

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

        public static final Tags.IOptionalNamedTag<Block> spring_crops = BlockTags.createOptional(new ResourceLocation(SereneSeasons.MOD_ID, "spring_crops"));
        public static final Tags.IOptionalNamedTag<Block> summer_crops = BlockTags.createOptional(new ResourceLocation(SereneSeasons.MOD_ID, "summer_crops"));
        public static final Tags.IOptionalNamedTag<Block> autumn_crops = BlockTags.createOptional(new ResourceLocation(SereneSeasons.MOD_ID, "autumn_crops"));
        public static final Tags.IOptionalNamedTag<Block> winter_crops = BlockTags.createOptional(new ResourceLocation(SereneSeasons.MOD_ID, "winter_crops"));

        public static final Tags.IOptionalNamedTag<Block> greenhouse_glass = BlockTags.createOptional(new ResourceLocation(SereneSeasons.MOD_ID, "greenhouse_glass"));
        public static final Tags.IOptionalNamedTag<Block> unbreakable_infertile_crops = BlockTags.createOptional(new ResourceLocation(SereneSeasons.MOD_ID, "unbreakable_infertile_crops"));
    }

    public static class Items
    {
        private static void setup() {}

        public static final Tags.IOptionalNamedTag<Item> spring_crops = ItemTags.createOptional(new ResourceLocation(SereneSeasons.MOD_ID, "spring_crops"));
        public static final Tags.IOptionalNamedTag<Item> summer_crops = ItemTags.createOptional(new ResourceLocation(SereneSeasons.MOD_ID, "summer_crops"));
        public static final Tags.IOptionalNamedTag<Item> autumn_crops = ItemTags.createOptional(new ResourceLocation(SereneSeasons.MOD_ID, "autumn_crops"));
        public static final Tags.IOptionalNamedTag<Item> winter_crops = ItemTags.createOptional(new ResourceLocation(SereneSeasons.MOD_ID, "winter_crops"));
    }
}
