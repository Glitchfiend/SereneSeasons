/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
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
