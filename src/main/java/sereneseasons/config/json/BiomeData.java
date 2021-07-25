/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.config.json;

import com.google.gson.annotations.SerializedName;

public class BiomeData
{
    @SerializedName("enable_seasonal_effects")
    public boolean enableSeasonalEffects;

    @SerializedName("use_tropical_seasons")
    public boolean useTropicalSeasons;

    public BiomeData(boolean enableSeasonalEffects, boolean useTropicalSeasons)
    {
        this.enableSeasonalEffects = enableSeasonalEffects;
        this.useTropicalSeasons = useTropicalSeasons;
    }
}
