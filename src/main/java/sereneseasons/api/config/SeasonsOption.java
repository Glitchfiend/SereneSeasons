/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.api.config;

public enum SeasonsOption implements ISyncedOption
{
    DAY_DURATION("Day Duration"),
    SUB_SEASON_DURATION("Sub Season Duration"),
    STARTING_SUB_SEASON("Starting Sub Season"),
    PROGRESS_SEASON_WHILE_OFFLINE("Progress Season While Offline");
    
    private final String optionName;

    SeasonsOption(String name)
    {
        this.optionName = name;
    }

    @Override
    public String getOptionName()
    {
        return this.optionName;
    }
}
