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
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import sereneseasons.api.SSBlocks;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.block.BlockSeasonSensor;

public class SeasonSensorTileEntity extends TileEntity implements ITickableTileEntity
{
    public SeasonSensorTileEntity()
    {
        super(SSBlocks.season_sensor_tile_entity);
    }

    @Override
    public void tick()
    {
        if (this.world != null && !this.world.isRemote && SeasonHelper.getSeasonState(this.world).getSeasonCycleTicks() % 20L == 0L)
        {
            BlockState blockstate = this.getBlockState();
            Block block = blockstate.getBlock();
            if (block instanceof BlockSeasonSensor)
            {
                ((BlockSeasonSensor)block).updatePower(this.world, this.pos);
            }
        }
    }
}
