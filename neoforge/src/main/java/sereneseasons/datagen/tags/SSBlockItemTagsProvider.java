/*******************************************************************************
 * Copyright 2026, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.datagen.tags;

import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.references.BlockItemIds;
import net.minecraft.tags.BlockItemTagId;
import sereneseasons.init.ModTags;

import java.util.function.Function;

public class SSBlockItemTagsProvider extends BlockItemTagsProvider
{
    public static final BlockItemTagId SPRING_CROPS = new BlockItemTagId(ModTags.Blocks.SPRING_CROPS, ModTags.Items.SPRING_CROPS);
    public static final BlockItemTagId SUMMER_CROPS = new BlockItemTagId(ModTags.Blocks.SUMMER_CROPS, ModTags.Items.SUMMER_CROPS);
    public static final BlockItemTagId AUTUMN_CROPS = new BlockItemTagId(ModTags.Blocks.AUTUMN_CROPS, ModTags.Items.AUTUMN_CROPS);
    public static final BlockItemTagId WINTER_CROPS = new BlockItemTagId(ModTags.Blocks.WINTER_CROPS, ModTags.Items.WINTER_CROPS);
    public static final BlockItemTagId YEAR_ROUND_CROPS = new BlockItemTagId(ModTags.Blocks.YEAR_ROUND_CROPS, ModTags.Items.YEAR_ROUND_CROPS);

    public SSBlockItemTagsProvider(Function<BlockItemTagId, CombinedAppender> tagSupplier)
    {
        super(tagSupplier);
    }

    @Override
    protected void run()
    {
        this.tag(YEAR_ROUND_CROPS).add(
            BlockItemIds.OAK_SAPLING,
            BlockItemIds.RED_MUSHROOM,
            BlockItemIds.BROWN_MUSHROOM,
            BlockItemIds.NETHER_WART,
            BlockItemIds.CRIMSON_FUNGUS,
            BlockItemIds.WARPED_FUNGUS,
            BlockItemIds.GLOW_BERRY_CROP
        );

        this.tag(SPRING_CROPS).addTag(YEAR_ROUND_CROPS).add(
            BlockItemIds.BIRCH_SAPLING,
            BlockItemIds.SPRUCE_SAPLING,
            BlockItemIds.CHERRY_SAPLING,
            BlockItemIds.AZALEA,
            BlockItemIds.FLOWERING_AZALEA,
            BlockItemIds.SWEET_BERRY_CROP,
            BlockItemIds.BAMBOO,
            BlockItemIds.CARROT_CROP,
            BlockItemIds.POTATO_CROP
        );

        this.tag(SUMMER_CROPS).addTag(YEAR_ROUND_CROPS).add(
            BlockItemIds.JUNGLE_SAPLING,
            BlockItemIds.ACACIA_SAPLING,
            BlockItemIds.MANGROVE_LEAVES,
            BlockItemIds.MANGROVE_PROPAGULE,
            BlockItemIds.AZALEA,
            BlockItemIds.FLOWERING_AZALEA,
            BlockItemIds.BAMBOO,
            BlockItemIds.CACTUS,
            BlockItemIds.SUGAR_CANE,
            BlockItemIds.WHEAT_CROP,
            BlockItemIds.MELON_CROP,
            BlockItemIds.COCOA_CROP,
            BlockItemIds.TORCHFLOWER_CROP,
            BlockItemIds.PITCHER_CROP
        );

        this.tag(AUTUMN_CROPS).addTag(YEAR_ROUND_CROPS).add(
            BlockItemIds.BIRCH_SAPLING,
            BlockItemIds.PALE_OAK_SAPLING,
            BlockItemIds.SPRUCE_SAPLING,
            BlockItemIds.DARK_OAK_SAPLING,
            BlockItemIds.WHEAT_CROP,
            BlockItemIds.PUMPKIN_CROP,
            BlockItemIds.BEETROOT_CROP,
            BlockItemIds.CARROT_CROP
        );

        this.tag(WINTER_CROPS).addTag(YEAR_ROUND_CROPS).add(
            BlockItemIds.SPRUCE_SAPLING,
            BlockItemIds.SWEET_BERRY_CROP
        );
    }
}
