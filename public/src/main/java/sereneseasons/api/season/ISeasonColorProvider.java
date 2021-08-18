/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
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
