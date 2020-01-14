/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;
import sereneseasons.api.ISSBlock;
import sereneseasons.core.SereneSeasons;
import sereneseasons.item.ItemSSBlock;

public class BlockGreenhouseGlass extends BlockGlass implements ISSBlock
{
    // implement ISSBlock
    @Override
    public Class<? extends ItemBlock> getItemClass()
    {
        return ItemSSBlock.class;
    }

    IIcon icon;

    public BlockGreenhouseGlass()
    {
        super(Material.glass, false);
        this.setHardness(0.3F);
        this.setHarvestLevel("pickaxe", 0);
        this.setStepSound(Block.soundTypeGlass);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir)
    {
        this.icon = ir.registerIcon(SereneSeasons.MOD_ID + ":greenhouse_glass");
    }

    public IIcon getIcon(int i, int m)
    {
        return this.icon;
    }

    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return 1;
    }
}
