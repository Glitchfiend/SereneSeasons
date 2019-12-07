/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonTime;

public class ItemSeasonClock extends Item
{

	private IIcon[] icons = new IIcon[64];

	private double field_185089_b;
	private double field_185088_a;
	private long ticks;

	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		for (int i = 0; i < 64; i++)
		{
			String num = String.valueOf(i);
			if (num.length() < 2)
				num = "0" + num;
			this.icons[i] = iconRegister.registerIcon("sereneseasons:season_clock_" + num);
		}
		this.itemIcon = iconRegister.registerIcon("sereneseasons:season_clock_00");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int renderPass)
	{
		Minecraft mc = Minecraft.getMinecraft();
		WorldClient worldClient = mc.theWorld;
		EntityClientPlayerMP entityClientPlayerMP = mc.thePlayer;
		if (worldClient != null && entityClientPlayerMP != null && stack != null)
		{
			float frame = apply(stack, (World) worldClient, (EntityLivingBase) entityClientPlayerMP);
			int index = (int) Math.round(frame * 64);
			return this.icons[index];
		}
		return this.itemIcon;
	}

	@Override
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	private static float positiveModulo(float numerator, float denominator)
	{
		return (numerator % denominator + denominator) % denominator;
	}

	@SideOnly(Side.CLIENT)
	public float apply(ItemStack stack, World world, EntityLivingBase entity)
	{
		Entity holder = (Entity) (entity != null ? entity : stack.getItemFrame());

		if (world == null && holder != null)
		{
			world = holder.worldObj;
		}

		if (world == null)
		{
			return 0.0F;
		} else
		{
			double d0;

			if (SeasonsConfig.isDimensionWhitelisted(world.provider.dimensionId))
			{
				int seasonCycleTicks = SeasonHelper.getSeasonState(world).getSeasonCycleTicks();
				d0 = (double) ((float) seasonCycleTicks / (float) SeasonTime.ZERO.getCycleDuration());
			} else
			{
				d0 = Math.random();
			}

			d0 = this.actualFrame(world, d0);
			return positiveModulo((float) d0, 1.0F);
		}
	}

	@SideOnly(Side.CLIENT)
	private double actualFrame(World world, double frame)
	{
		if (world.getTotalWorldTime() != this.ticks)
		{
			this.ticks = (int) world.getTotalWorldTime();
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
}
