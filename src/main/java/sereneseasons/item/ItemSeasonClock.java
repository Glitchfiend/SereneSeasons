/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;
import sereneseasons.season.SeasonTime;

public class ItemSeasonClock extends Item
{
    public ItemSeasonClock()
    {
        this.addPropertyOverride(new ResourceLocation("time"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            double field_185088_a;
            @SideOnly(Side.CLIENT)
            double field_185089_b;
            @SideOnly(Side.CLIENT)
            int ticks;
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, World world, EntityLivingBase entity)
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
                    
                    if (SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension()))
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
            @SideOnly(Side.CLIENT)
            private double actualFrame(World world, double frame)
            {
                if (world.getTotalWorldTime() != this.ticks)
                {
                    this.ticks = (int)world.getTotalWorldTime();
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
