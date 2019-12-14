/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
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
import sereneseasons.season.SeasonSavedData;

@Mod.EventBusSubscriber
public class SeasonSleepHandler 
{
    private boolean lastAllPlayersSleeping = false;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.SERVER)
        {
            ServerWorld world = (ServerWorld)event.world;

            //Called before all players are awoken for the next day
            if (!lastAllPlayersSleeping && world.allPlayersSleeping)
            {
                System.out.println("Time change");
                lastAllPlayersSleeping = true;
                SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
                long timeDiff = 24000L - ((world.getWorldInfo().getDayTime() + 24000L) % 24000L);
                seasonData.seasonCycleTicks += timeDiff;
                seasonData.markDirty();
                SeasonHandler.sendSeasonUpdate(world);
            }
            else if (!world.allPlayersSleeping)
            {
                lastAllPlayersSleeping = false;
            }
        }
    }
}
