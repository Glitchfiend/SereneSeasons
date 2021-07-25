/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.season;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class SeasonSavedData extends SavedData
{
    public static final String DATA_IDENTIFIER = "seasons";
    public static final int VERSION = 0;
    
    public int seasonCycleTicks;
    
    public SeasonSavedData()
    {
        this(DATA_IDENTIFIER);
    }
    
    //This specific constructor is required for saving to occur
    public SeasonSavedData(String identifier)
    {
        super(identifier);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        this.seasonCycleTicks = nbt.getInt("SeasonCycleTicks");
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putInt("SeasonCycleTicks", this.seasonCycleTicks);
        return nbt;
    }
}
