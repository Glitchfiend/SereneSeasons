/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import sereneseasons.api.SSBlocks;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.block.SeasonSensorBlock;

public class SeasonSensorTileEntity extends TileEntity implements ITickableTileEntity
{
    public SeasonSensorTileEntity()
    {
        super(SSBlocks.season_sensor_tile_entity);
    }

    @Override
    public void tick()
    {
        if (this.level != null && !this.level.isClientSide && SeasonHelper.getSeasonState(this.level).getSeasonCycleTicks() % 20L == 0L)
        {
            BlockState blockstate = this.getBlockState();
            Block block = blockstate.getBlock();
            if (block instanceof SeasonSensorBlock)
            {
                ((SeasonSensorBlock)block).updatePower(this.level, this.worldPosition);
            }
        }
    }
}
