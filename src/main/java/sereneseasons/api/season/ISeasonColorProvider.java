/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.api.season;

public interface ISeasonColorProvider
{
    int getGrassOverlay();
    float getGrassSaturationMultiplier();
    int getFoliageOverlay();
    float getFoliageSaturationMultiplier();
    int getBirchColor();
}
