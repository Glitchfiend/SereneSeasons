/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sereneseasons.api.ISSBlock;
import sereneseasons.api.SSBlocks;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.core.SereneSeasons;
import sereneseasons.item.ItemSSBlock;
import sereneseasons.season.SeasonTime;
import sereneseasons.tileentity.TileEntitySeasonSensor;

public class BlockSeasonSensor extends BlockContainer implements ISSBlock
{
    // implement ITANBlock
    @Override
    public Class<? extends ItemBlock> getItemClass()
    {
        return ItemSSBlock.class;
    }

    private final DetectorType type;

    IIcon iconSide;
    IIcon iconTop;

    public BlockSeasonSensor(DetectorType type)
    {
        super(Material.wood);
        this.type = type;
        this.setHardness(0.2F);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockBounds(0, 0, 0, 1, 0.375f, 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister ir)
    {
        switch (type)
        {
        case SPRING:
            this.iconTop = ir.registerIcon(SereneSeasons.MOD_ID + ":season_sensor_spring_top");
            break;
        case SUMMER:
            this.iconTop = ir.registerIcon(SereneSeasons.MOD_ID + ":season_sensor_summer_top");
            break;
        case AUTUMN:
            this.iconTop = ir.registerIcon(SereneSeasons.MOD_ID + ":season_sensor_autumn_top");
            break;
        case WINTER:
            this.iconTop = ir.registerIcon(SereneSeasons.MOD_ID + ":season_sensor_winter_top");
            break;
        }
        this.iconSide = ir.registerIcon(SereneSeasons.MOD_ID + ":season_sensor_side");
    }

    @Override
    public IIcon getIcon(int i, int m)
    {
        if (i == 1)
            return this.iconTop;
        return this.iconSide;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        return blockAccess.getBlockMetadata(x, y, z);
    }

    public void updatePower(World world, int x, int y, int z)
    {
        if (SeasonsConfig.isDimensionWhitelisted(world.provider.dimensionId))
        {
            int meta = world.getBlockMetadata(x, y, z);

            int power = 0;
            int startTicks = this.type.ordinal() * SeasonTime.ZERO.getSeasonDuration();
            int endTicks = (this.type.ordinal() + 1) * SeasonTime.ZERO.getSeasonDuration();
            int currentTicks = SeasonHelper.getSeasonState(world).getSeasonCycleTicks();

            if (currentTicks >= startTicks && currentTicks <= endTicks)
            {
                float delta = (float) (currentTicks - startTicks) / (float) SeasonTime.ZERO.getSeasonDuration();
                power = (int) Math.min(delta * 15.0F + 1.0F, 15.0F);
            }

            // Only update the state if the power level has actually changed
            if (meta != power)
            {
                world.setBlockMetadataWithNotify(x, y, z, power, 3);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            Block nextBlock = SSBlocks.season_sensors[(this.type.ordinal() + 1) % DetectorType.values().length];
            world.setBlock(x, y, z, nextBlock);
            ((BlockSeasonSensor) nextBlock).updatePower(world, x, y, z);
            return true;
        }
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune)
    {
        return Item.getItemFromBlock(SSBlocks.season_sensors[0]);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntitySeasonSensor();
    }

    public static enum DetectorType
    {
        SPRING, SUMMER, AUTUMN, WINTER;

        public String getName()
        {
            return this.name().toLowerCase();
        }

        @Override
        public String toString()
        {
            return this.getName();
        }
    };
}
