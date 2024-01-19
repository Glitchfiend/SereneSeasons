/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;

import glitchcore.network.CustomPacket;
import glitchcore.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import sereneseasons.core.SereneSeasons;
import sereneseasons.network.SyncSeasonCyclePacket;

public class ModPackets
{
    private static final ResourceLocation CHANNEL = new ResourceLocation(SereneSeasons.MOD_ID, "main");
    public static final PacketHandler HANDLER = new PacketHandler(CHANNEL);

    public static void init()
    {
        register("sync_season_cycle", new SyncSeasonCyclePacket());
    }

    public static void register(String name, CustomPacket<?> packet)
    {
        HANDLER.register(new ResourceLocation(SereneSeasons.MOD_ID, name), packet);
    }
}
