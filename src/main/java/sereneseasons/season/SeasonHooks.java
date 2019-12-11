/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.season;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;

public class SeasonHooks
{
    public float getTemperature(BlockPos pos) {
        if (pos.getY() > 64) {
            float f = (float)(TEMPERATURE_NOISE.getValue((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F)) * 4.0D);
            return this.getDefaultTemperature() - (f + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
        } else {
            return this.getDefaultTemperature();
        }
    }

    public boolean doesWaterFreeze(IWorldReader worldIn, BlockPos water, boolean mustBeAtEdge) {
        if (this.func_225486_c(water) >= 0.15F) {
            return false;
        } else {
            if (water.getY() >= 0 && water.getY() < worldIn.getDimension().getHeight() && worldIn.getLightFor(LightType.BLOCK, water) < 10) {
                BlockState blockstate = worldIn.getBlockState(water);
                IFluidState ifluidstate = worldIn.getFluidState(water);
                if (ifluidstate.getFluid() == Fluids.WATER && blockstate.getBlock() instanceof FlowingFluidBlock) {
                    if (!mustBeAtEdge) {
                        return true;
                    }

                    boolean flag = worldIn.hasWater(water.west()) && worldIn.hasWater(water.east()) && worldIn.hasWater(water.north()) && worldIn.hasWater(water.south());
                    if (!flag) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean doesSnowGenerate(IWorldReader worldIn, BlockPos pos) {
        if (this.func_225486_c(pos) >= 0.15F) {
            return false;
        } else {
            if (pos.getY() >= 0 && pos.getY() < 256 && worldIn.getLightFor(LightType.BLOCK, pos) < 10) {
                BlockState blockstate = worldIn.getBlockState(pos);
                if (blockstate.isAir(worldIn, pos) && Blocks.SNOW.getDefaultState().isValidPosition(worldIn, pos)) {
                    return true;
                }
            }

            return false;
        }
    }
}
