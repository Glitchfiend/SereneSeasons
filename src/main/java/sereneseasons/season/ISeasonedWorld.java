/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.season;

import sereneseasons.api.season.Season;

/** Now with extra seasoning */
public interface ISeasonedWorld
{
    boolean canSnowAtInSeason(int x, int y, int z, boolean checkLight, Season season);

    boolean canBlockFreezeInSeason(int x, int y, int z, boolean noWaterAdj, Season season);
}
