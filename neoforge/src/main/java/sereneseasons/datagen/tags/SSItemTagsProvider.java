/*******************************************************************************
 * Copyright 2026, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import sereneseasons.core.SereneSeasons;

import java.util.concurrent.CompletableFuture;

public class SSItemTagsProvider extends ItemTagsProvider
{
    public SSItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, SereneSeasons.MOD_ID);
    }

    @Override
    protected BlockItemTagAppender<Item> tag(TagKey<Item> tag)
    {
        return new BlockItemTagAppender<>(super.tag(tag))
        {
            @Override
            protected ResourceKey<Item> convertElement(BlockItemId element)
            {
                return element.item();
            }
        };
    }

    @Override
    protected void addTags(HolderLookup.Provider registries)
    {
        new SSBlockItemTagsProvider(tagId -> BlockItemTagsProvider.wrapForItems(this.tag(tagId.item()))).run();
    }
}
