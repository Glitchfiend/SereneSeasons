/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import sereneseasons.api.SSBlocks;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.block.SeasonSensorBlock;

public class SeasonSensorBlockEntity extends BlockEntity
{
    public SeasonSensorBlockEntity(BlockPos pos, BlockState state)
    {
        super(SSBlocks.season_sensor_tile_entity, pos, state);
    }
}
