/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.season;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;
import sereneseasons.season.journal.SeasonJournal;

public class SeasonSavedData extends WorldSavedData
{
    public static final String DATA_IDENTIFIER = "seasons";

    public int seasonCycleTicks;
    
    public SeasonJournal journal = new SeasonJournal();

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
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
    	nbt.setInteger("SeasonCycleTicks", this.seasonCycleTicks);
    	nbt.setTag("Journal", this.journal.serializeNBT());

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
}
