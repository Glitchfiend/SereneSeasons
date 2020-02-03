/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import sereneseasons.core.SereneSeasons;
import sereneseasons.season.SeasonSavedData;

@Mod.EventBusSubscriber
public class TimeSkipHandler
{
    private long lastDayTime = -1;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.SERVER)
        {
            ServerWorld world = (ServerWorld)event.world;
            long dayTime = world.getLevelData().getDayTime();

            if (lastDayTime == -1)
            {
                lastDayTime = dayTime;
            }

            long difference = world.getLevelData().getDayTime() - lastDayTime;

            if (difference < 0)
            {
                difference += 24000L;
            }

            // Time has skipped, skip the season time too
            if (difference > 1)
            {
                SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
                seasonData.seasonCycleTicks += difference;
                seasonData.setDirty();
                SeasonHandler.sendSeasonUpdate(world);
                SereneSeasons.logger.info("Season time skipped by " + difference);
            }

            lastDayTime = dayTime;
        }
    }
}
