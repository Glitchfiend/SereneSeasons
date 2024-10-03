/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.season;

import glitchcore.event.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.Season;
import sereneseasons.init.ModConfig;

import java.util.HashMap;

public class SeasonHandlerClient
{
    static Season.SubSeason lastSeason = null;
    public static final HashMap<ResourceKey<Level>, Integer> clientSeasonCycleTicks = new HashMap<>();

    public static void onClientTick(TickEvent.Client event)
    {
        Player player = (Player) Minecraft.getInstance().player;

        //Only do this when in the world
        if (player == null) return;
        ResourceKey<Level> dimension = player.level().dimension();

        if (event.getPhase() == TickEvent.Phase.END && ModConfig.seasons.isDimensionWhitelisted(dimension))
        {
            //Keep ticking as we're synchronized with the server only every second
            clientSeasonCycleTicks.compute(dimension, (k, v) -> v == null ? 0 : (v + 1) % SeasonTime.ZERO.getCycleDuration());

            SeasonTime calendar = new SeasonTime(clientSeasonCycleTicks.get(dimension));
            if (calendar.getSubSeason() != lastSeason)
            {
                Minecraft.getInstance().levelRenderer.allChanged();
                lastSeason = calendar.getSubSeason();
            }
        }
    }
}
