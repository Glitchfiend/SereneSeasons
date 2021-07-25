/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.tileentity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import sereneseasons.api.SSBlocks;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.block.SeasonSensorBlock;

public class SeasonSensorTileEntity extends BlockEntity implements TickableBlockEntity
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
