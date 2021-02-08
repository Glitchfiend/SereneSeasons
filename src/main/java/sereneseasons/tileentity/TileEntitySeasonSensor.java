/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.block.BlockSeasonSensor;

public class TileEntitySeasonSensor extends TileEntity implements ITickable 
{
    @Override
    public void update()
    {
        if (this.world != null && !this.world.isRemote && SeasonHelper.getSeasonState(this.world).getSeasonCycleTicks() % 20L == 0L)
        {
            ((BlockSeasonSensor)this.getBlockType()).updatePower(this.world, this.pos);
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event)
    {
        Chunk chunk = event.getChunk();
        int chunkX = chunk.x;
        int chunkZ = chunk.z;

        if (chunkX == (this.pos.getX() / 16) && chunkZ == (this.pos.getZ() / 16) )
        {
            ((BlockSeasonSensor) this.getBlockType()).updatePower(this.world, this.pos);
        }
    }
}
