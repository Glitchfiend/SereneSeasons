/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.config.json;

import com.google.gson.annotations.SerializedName;

public class BiomeData
{
    @SerializedName("enable_seasonal_effects")
    public boolean enableSeasonalEffects;

    @SerializedName("use_tropical_seasons")
    public boolean useTropicalSeasons;
    
    @SerializedName("disable_crops")
    public boolean disableCrops;

    public BiomeData(boolean enableSeasonalEffects, boolean useTropicalSeasons, boolean disableCrops)
    {
        this.enableSeasonalEffects = enableSeasonalEffects;
        this.useTropicalSeasons = useTropicalSeasons;
        this.disableCrops = disableCrops;
    }
}
