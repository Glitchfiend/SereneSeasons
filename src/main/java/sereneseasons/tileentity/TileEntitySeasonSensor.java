/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.tileentity;

import net.minecraft.tileentity.TileEntity;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.block.BlockSeasonSensor;

public class TileEntitySeasonSensor extends TileEntity
{
    @Override
    public void updateEntity()
    {
        if (this.worldObj != null && !this.worldObj.isRemote && SeasonHelper.getSeasonState(this.worldObj).getSeasonCycleTicks() % 20L == 0L)
        {
            ((BlockSeasonSensor) this.getBlockType()).updatePower(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }
    }
}
