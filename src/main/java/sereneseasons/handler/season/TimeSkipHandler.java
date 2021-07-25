/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.handler.season;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import sereneseasons.core.SereneSeasons;
import sereneseasons.season.SeasonSavedData;

import java.util.HashMap;

public class TimeSkipHandler
{
    public static final HashMap<ResourceKey<Level>, Long> lastDayTimes = new HashMap<>();

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.SERVER)
        {
            ServerLevel world = (ServerLevel)event.world;
            long dayTime = world.getLevelData().getDayTime();

            if (!lastDayTimes.containsKey(world.dimension()))
                lastDayTimes.put(world.dimension(), dayTime);

            long lastDayTime = lastDayTimes.get(world.dimension());
            long difference = dayTime - lastDayTime;

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
                SereneSeasons.logger.info("Season time skipped by " + difference + " in " + world.dimension().location().toString());
            }

            lastDayTimes.put(world.dimension(), dayTime);
        }
    }
}
