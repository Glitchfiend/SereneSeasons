/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.datagen.provider;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import sereneseasons.api.SSBlocks;
import sereneseasons.api.SSItems;

import java.util.Set;

public class SSRecipeProvider extends RecipeProvider
{
    public SSRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput)
    {
        super(recipeOutput, advancementOutput);
    }

    public static MultiRegistryBootstrap create()
    {
        return new MultiRegistryBootstrap()
        {
            @Override
            public Set<ResourceKey<? extends Registry<?>>> requestedRegistries()
            {
                return Set.of(Registries.RECIPE, Registries.ADVANCEMENT);
            }

            @Override
            public void run(MultiRegistryBootstrap.BootstrapGetter registries)
            {
                new SSRecipeProvider(registries.get(Registries.RECIPE), registries.get(Registries.ADVANCEMENT)).buildRecipes();
            }
        };
    }

    @Override
    protected void buildRecipes()
    {
        this.shaped(RecipeCategory.REDSTONE, SSBlocks.SEASON_SENSOR).define('G', Items.GLASS).define('Q', Items.QUARTZ).define('C', SSItems.CALENDAR).define('#', Blocks.COBBLESTONE_SLAB).pattern("GGG").pattern("QCQ").pattern("###").unlockedBy("has_calendar", has(SSItems.CALENDAR)).save(output);
        this.shaped(RecipeCategory.TOOLS, SSItems.CALENDAR).define('P', Items.PAPER).define('C', Items.CLOCK).pattern("PPP").pattern("PCP").pattern("PPP").unlockedBy("has_clock", has(Items.CLOCK)).save(output);
    }
}
