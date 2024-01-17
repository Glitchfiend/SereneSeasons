/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.handler.season;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sereneseasons.api.season.Season;
import sereneseasons.config.ServerConfig;
import sereneseasons.season.SeasonTime;

import java.util.HashMap;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SeasonHandlerClient
{
    static Season.SubSeason lastSeason = null;
    public static final HashMap<ResourceKey<Level>, Integer> clientSeasonCycleTicks = new HashMap<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        Player player = (Player) Minecraft.getInstance().player;

        //Only do this when in the world
        if (player == null) return;
        ResourceKey<Level> dimension = player.level().dimension();

        if (event.phase == TickEvent.Phase.END && ServerConfig.isDimensionWhitelisted(dimension))
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
