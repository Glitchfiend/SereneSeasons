/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.world.WorldServer;
import sereneseasons.season.SeasonSavedData;

public class SeasonSleepHandler 
{
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == Phase.START && event.side == Side.SERVER)
        {
            WorldServer world = (WorldServer)event.world;

            //Called before all players are awoken for the next day
            if (world.areAllPlayersAsleep())
            {
                SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
                long timeDiff = 24000L - ((world.getWorldInfo().getWorldTime() + 24000L) % 24000L);
                seasonData.seasonCycleTicks += timeDiff;
                seasonData.markDirty();
                SeasonHandler.sendSeasonUpdate(world);
            }
        }
    }
}
