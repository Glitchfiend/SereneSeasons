/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.api.config;

import sereneseasons.util.StringUtils;

public enum SeasonsOption implements ISyncedOption
{
    DAY_DURATION("Day Duration"),
    SUB_SEASON_DURATION("Sub Season Duration");
    
    private final String optionName;
    private final String nbtOptionName;

    SeasonsOption(String name)
    {
        this.optionName = name;
        this.nbtOptionName = StringUtils.toNBTConformKey(optionName);
        if( this.nbtOptionName.isEmpty() )
        	throw new IllegalArgumentException("key is invalid.");
    }

    @Override
    public String getOptionName()
    {
        return this.optionName;
    }

	@Override
	public String getNBTOptionName() {
		return nbtOptionName;
	}
}
