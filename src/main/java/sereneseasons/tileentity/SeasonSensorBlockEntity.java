/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import sereneseasons.api.SSBlockEntities;

public class SeasonSensorBlockEntity extends BlockEntity
{
    public SeasonSensorBlockEntity(BlockPos pos, BlockState state)
    {
        super(SSBlockEntities.SEASON_SENSOR.get(), pos, state);
    }
}
