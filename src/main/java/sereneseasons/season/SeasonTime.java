/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.season;

import com.google.common.base.Preconditions;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;

public final class SeasonTime implements ISeasonState
{
    public static final SeasonTime ZERO = new SeasonTime(0);
    public final int time;
    
    public SeasonTime(int time)
    {
        Preconditions.checkArgument(time >= 0, "Time cannot be negative!");
        this.time = time;
    }

    @Override
    public int getDayDuration()
    {
        return SyncedConfig.getIntValue(SeasonsOption.DAY_DURATION);
    }

    @Override
    public int getSubSeasonDuration()
    {
        return getDayDuration() * SyncedConfig.getIntValue(SeasonsOption.SUB_SEASON_DURATION);
    }

    @Override
    public int getSeasonDuration()
    {
        return getSubSeasonDuration() * 3;
    }

    @Override
    public int getCycleDuration()
    {
        return getSubSeasonDuration() * Season.SubSeason.VALUES.length;
    }
    
    @Override
    public int getSeasonCycleTicks() 
    {
        return this.time;
    }

    @Override
    public int getDay()
    {
        return this.time / getDayDuration();
    }

    @Override
    public Season.SubSeason getSubSeason()
    {
        int index = (this.time / getSubSeasonDuration()) % Season.SubSeason.VALUES.length;
        return Season.SubSeason.VALUES[index];
    }

    @Override
    public Season getSeason()
    {
        return this.getSubSeason().getSeason();
    }

    @Override
    public Season.TropicalSeason getTropicalSeason()
    {
        int index = ((((this.time / getSubSeasonDuration()) + 11) / 2) + 5) % Season.TropicalSeason.VALUES.length;
        return Season.TropicalSeason.VALUES[index];
    }
}
