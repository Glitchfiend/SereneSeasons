/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.season;

import net.minecraft.core.BlockPos;
import sereneseasons.api.season.Season;

/** Now with extra seasoning*/
public interface ISeasonedWorld 
{
    boolean canSnowAtInSeason(BlockPos pos, boolean checkLight, Season season);
    boolean canBlockFreezeInSeason(BlockPos pos, boolean noWaterAdj, Season season);
}
