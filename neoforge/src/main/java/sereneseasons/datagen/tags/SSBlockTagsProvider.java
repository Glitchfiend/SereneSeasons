/*******************************************************************************
 * Copyright 2026, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.references.BlockIds;
import net.minecraft.references.BlockItemId;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModTags;

import java.util.concurrent.CompletableFuture;

public class SSBlockTagsProvider extends BlockTagsProvider
{
    public SSBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, SereneSeasons.MOD_ID);
    }

    @Override
    protected BlockItemTagAppender<Block> tag(TagKey<Block> tag)
    {
        return new BlockItemTagAppender<>(super.tag(tag))
        {
            @Override
            protected ResourceKey<Block> convertElement(BlockItemId element)
            {
                return element.block();
            }
        };
    }

    @Override
    protected void addTags(HolderLookup.Provider registries)
    {
        new SSBlockItemTagsProvider(tagId -> BlockItemTagsProvider.wrapForBlocks(this.tag(tagId.block()))).run();

        // Block-only parts of crops
        this.tag(ModTags.Blocks.YEAR_ROUND_CROPS).add(BlockIds.CAVE_VINES_PLANT);
        this.tag(ModTags.Blocks.SPRING_CROPS).add(BlockIds.BAMBOO_SAPLING);
        this.tag(ModTags.Blocks.SUMMER_CROPS).add(BlockIds.BAMBOO_SAPLING);

        this.tag(ModTags.Blocks.GREENHOUSE_GLASS)
            .addOptionalTag(TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("forge", "glass")))
            .addOptionalTag(TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "glass_blocks")));

        this.tag(ModTags.Blocks.UNBREAKABLE_INFERTILE_CROPS)
            .add(
                BlockItemIds.OAK_SAPLING,
                BlockItemIds.BIRCH_SAPLING,
                BlockItemIds.SPRUCE_SAPLING,
                BlockItemIds.JUNGLE_SAPLING,
                BlockItemIds.ACACIA_SAPLING,
                BlockItemIds.DARK_OAK_SAPLING,
                BlockItemIds.MANGROVE_LEAVES,
                BlockItemIds.MANGROVE_PROPAGULE,
                BlockItemIds.CHERRY_SAPLING,
                BlockItemIds.AZALEA,
                BlockItemIds.FLOWERING_AZALEA,
                BlockItemIds.GRASS_BLOCK,
                BlockItemIds.MOSS_BLOCK,
                BlockItemIds.ROOTED_DIRT,
                BlockItemIds.BIG_DRIPLEAF,
                BlockItemIds.SMALL_DRIPLEAF,
                BlockItemIds.SHORT_GRASS,
                BlockItemIds.FERN,
                BlockItemIds.PINK_PETALS,
                BlockItemIds.SUNFLOWER,
                BlockItemIds.LILAC,
                BlockItemIds.ROSE_BUSH,
                BlockItemIds.PEONY,
                BlockItemIds.SUGAR_CANE,
                BlockItemIds.CACTUS,
                BlockItemIds.BAMBOO,
                BlockItemIds.RED_MUSHROOM,
                BlockItemIds.BROWN_MUSHROOM,
                BlockItemIds.NETHER_WART,
                BlockItemIds.CRIMSON_FUNGUS,
                BlockItemIds.WARPED_FUNGUS,
                BlockItemIds.CRIMSON_NYLIUM,
                BlockItemIds.WARPED_NYLIUM,
                BlockItemIds.TWISTING_VINES,
                BlockItemIds.WEEPING_VINES,
                BlockItemIds.GLOW_BERRY_CROP,
                BlockItemIds.SEAGRASS,
                BlockItemIds.SEA_PICKLE,
                BlockItemIds.KELP,
                BlockItemIds.GLOW_LICHEN
            )
            .add(
                BlockIds.BIG_DRIPLEAF_STEM,
                BlockIds.BAMBOO_SAPLING,
                BlockIds.TWISTING_VINES_PLANT,
                BlockIds.WEEPING_VINES_PLANT,
                BlockIds.CAVE_VINES_PLANT,
                BlockIds.KELP_PLANT
            );
    }
}
