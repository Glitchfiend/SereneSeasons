/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.season;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldSavedData;
import sereneseasons.core.SereneSeasons;
import sereneseasons.season.journal.SeasonJournal;
import sereneseasons.util.DataUtils;
import sereneseasons.world.chunk.SeasonChunkManager;

public class SeasonSavedData extends WorldSavedData
{
    public static final String DATA_IDENTIFIER = "seasons";

    public int seasonCycleTicks;
    
    public SeasonJournal journal = new SeasonJournal();
//    public SeasonChunkManager chunkMan = new SeasonChunkManager();

    public SeasonSavedData()
    {
        this(DATA_IDENTIFIER);
    }

    // This specific constructor is required for saving to occur
    public SeasonSavedData(String identifier)
    {
        super(identifier);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        this.seasonCycleTicks = nbt.getInteger("SeasonCycleTicks");

        this.journal = new SeasonJournal();
        this.journal.deserializeNBT(nbt.getCompoundTag("Journal"));

//        this.chunkMan = new SeasonChunkManager();
//        this.chunkMan.deserializeNBT(nbt.getCompoundTag("Chunks"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
    	nbt.setInteger("SeasonCycleTicks", this.seasonCycleTicks);
    	nbt.setTag("Journal", this.journal.serializeNBT());
//    	nbt.setTag("Chunks", this.chunkMan.serializeNBT());

        return nbt;
    }
    
    /**
     * Returns the journal.
     * 
     * @return the journal.
     */
    public SeasonJournal getJournal() {
    	return this.journal;
    }
    
    /**
     * Returns the chunk manager.
     * 
     * @return the chunk manager
     */
//	public SeasonChunkManager getChunkManager() {
//		return this.chunkMan;
//	}

    /**
     * Called from {@link sereneseasons.handler.season.SeasonHandler#onWorldUnloaded}. <br/>
     * Cleanup routine if a world got unloaded.
     * 
     * @param world the unloaded world.
     */
//    public void onWorldUnload(World world)
//    {
//    	chunkMan.onWorldUnload(world);
//    }

    /**
     * Called from {@link sereneseasons.handler.season.SeasonHandler#onChunkUnloaded}. <br/>
     * Cleanup routine if a chunk got unloaded. In this case the meta is detached from Chunk.
     *   
     * @param chunk the unloaded chunk.
     */
//    public void onChunkUnloaded(Chunk chunk)
//    {
//    	chunkMan.onChunkUnloaded(chunk);
//    }

}
