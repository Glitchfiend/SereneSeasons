package sereneseasons.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags
{
    public static void setup()
    {
        Blocks.setup();
        Items.setup();
        Biomes.setup();
    }

    public static class Blocks
    {
        private static void setup() {}

        public static final TagKey<Block> SPRING_CROPS = create(ResourceLocation.parse("sereneseasons:spring_crops"));
        public static final TagKey<Block> SUMMER_CROPS = create(ResourceLocation.parse("sereneseasons:summer_crops"));
        public static final TagKey<Block> AUTUMN_CROPS = create(ResourceLocation.parse("sereneseasons:autumn_crops"));
        public static final TagKey<Block> WINTER_CROPS = create(ResourceLocation.parse("sereneseasons:winter_crops"));
        public static final TagKey<Block> YEAR_ROUND_CROPS = create(ResourceLocation.parse("sereneseasons:year_round_crops"));

        public static final TagKey<Block> GREENHOUSE_GLASS = create(ResourceLocation.parse("sereneseasons:greenhouse_glass"));
        public static final TagKey<Block> UNBREAKABLE_INFERTILE_CROPS = create(ResourceLocation.parse("sereneseasons:unbreakable_infertile_crops"));

        public static TagKey<Block> create(ResourceLocation name)
        {
            return TagKey.create(Registries.BLOCK, name);
        }
    }

    public static class Items
    {
        private static void setup() {}

        public static final TagKey<Item> SPRING_CROPS = create(ResourceLocation.parse("sereneseasons:spring_crops"));
        public static final TagKey<Item> SUMMER_CROPS = create(ResourceLocation.parse("sereneseasons:summer_crops"));
        public static final TagKey<Item> AUTUMN_CROPS = create(ResourceLocation.parse("sereneseasons:autumn_crops"));
        public static final TagKey<Item> WINTER_CROPS = create(ResourceLocation.parse("sereneseasons:winter_crops"));
        public static final TagKey<Item> YEAR_ROUND_CROPS = create(ResourceLocation.parse("sereneseasons:year_round_crops"));

        public static TagKey<Item> create(ResourceLocation name)
        {
            return TagKey.create(Registries.ITEM, name);
        }
    }

    public static class Biomes
    {
        private static void setup() {}

        public static final TagKey<Biome> BLACKLISTED_BIOMES = createBiomeTag(ResourceLocation.parse("sereneseasons:blacklisted_biomes"));
        public static final TagKey<Biome> INFERTILE_BIOMES = createBiomeTag(ResourceLocation.parse("sereneseasons:infertile_biomes"));
        public static final TagKey<Biome> LESSER_COLOR_CHANGE_BIOMES = createBiomeTag(ResourceLocation.parse("sereneseasons:lesser_color_change_biomes"));
        public static final TagKey<Biome> TROPICAL_BIOMES = createBiomeTag(ResourceLocation.parse("sereneseasons:tropical_biomes"));
    }

    private static TagKey<Biome> createBiomeTag(ResourceLocation name) {
        return TagKey.create(Registries.BIOME, name);
    }
}
