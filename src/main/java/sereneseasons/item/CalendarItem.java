/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonTime;

public class CalendarItem extends Item
{
    public CalendarItem(Item.Properties builder)
    {
        super(builder);

        this.addPropertyOverride(new ResourceLocation("time"), new IItemPropertyGetter()
        {
            @OnlyIn(Dist.CLIENT)
            double field_185088_a;
            @OnlyIn(Dist.CLIENT)
            double field_185089_b;
            @OnlyIn(Dist.CLIENT)
            int ticks;

            @Override
            @OnlyIn(Dist.CLIENT)
            public float call(ItemStack stack, World world, LivingEntity entity)
            {
                Entity holder = (Entity)(entity != null ? entity : stack.getItemFrame());

                if (world == null && holder != null)
                {
                    world = holder.world;
                }

                if (world == null)
                {
                    return 0.0F;
                }
                else
                {
                    double d0;
                    
                    if (SeasonsConfig.isDimensionWhitelisted(world.getDimension().getType().getId()))
                    {
                        int seasonCycleTicks = SeasonHelper.getSeasonState(world).getSeasonCycleTicks();
                        d0 = (double)((float)seasonCycleTicks / (float) SeasonTime.ZERO.getCycleDuration());
                    }
                    else
                    {
                        d0 = Math.random();
                    }

                    return MathHelper.positiveModulo((float)d0, 1.0F);
                }
            }
        });

        this.addPropertyOverride(new ResourceLocation("seasontype"), new IItemPropertyGetter()
        {
            @OnlyIn(Dist.CLIENT)
            double field_185088_a;
            @OnlyIn(Dist.CLIENT)
            double field_185089_b;
            @OnlyIn(Dist.CLIENT)
            int ticks;

            @Override
            @OnlyIn(Dist.CLIENT)
            public float call(ItemStack stack, World world, LivingEntity entity)
            {
                Entity holder = (Entity)(entity != null ? entity : stack.getItemFrame());

                if (world == null && holder != null)
                {
                    world = holder.world;
                }

                if (world == null)
                {
                    return 0.0F;
                }
                else
                {
                    float seasontype;

                    if (SeasonsConfig.isDimensionWhitelisted(world.getDimension().getType().getId()))
                    {
                        if (holder != null)
                        {
                            Biome biome = world.func_226691_t_(holder.getPosition());

                            if (BiomeConfig.usesTropicalSeasons(biome))
                            {
                                seasontype = 1.0F;
                            }
                            else
                            {
                                seasontype = 0.0F;
                            }
                        }
                        else
                        {
                            seasontype = 0.0F;
                        }
                    }
                    else
                    {
                        seasontype = 0.0F;
                    }

                    return seasontype;
                }
            }
        });
    }
}
