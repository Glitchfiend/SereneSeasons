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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonTime;

public class ItemSeasonClock extends Item
{
    public ItemSeasonClock(Item.Properties builder)
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
                    
                    d0 = this.actualFrame(world, d0);
                    return MathHelper.positiveModulo((float)d0, 1.0F);
                }
            }
            @OnlyIn(Dist.CLIENT)
            private double actualFrame(World world, double frame)
            {
                if (world.getGameTime() != this.ticks)
                {
                    this.ticks = (int)world.getGameTime();
                    double newFrame = frame - this.field_185088_a;

                    if (newFrame < -0.5D)
                    {
                        ++newFrame;
                    }

                    this.field_185089_b += newFrame * 0.1D;
                    this.field_185089_b *= 0.9D;
                    this.field_185088_a += this.field_185089_b;
                }

                return this.field_185088_a;
            }
        });
    }
}
