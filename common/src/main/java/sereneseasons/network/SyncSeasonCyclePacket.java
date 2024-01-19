/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.network;

import glitchcore.network.CustomPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import sereneseasons.season.SeasonHandlerClient;

public class SyncSeasonCyclePacket implements CustomPacket<SyncSeasonCyclePacket>
{
    public ResourceKey<Level> dimension;
    public int seasonCycleTicks;
    
    public SyncSeasonCyclePacket() {}
    
    public SyncSeasonCyclePacket(ResourceKey<Level> dimension, int seasonCycleTicks)
    {
        this.dimension = dimension;
        this.seasonCycleTicks = seasonCycleTicks;
    }

    @Override
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUtf(this.dimension.location().toString());
        buf.writeInt(this.seasonCycleTicks);
    }

    @Override
    public SyncSeasonCyclePacket decode(FriendlyByteBuf buf)
    {
        return new SyncSeasonCyclePacket(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf())), buf.readInt());
    }

    @Override
    public void handle(SyncSeasonCyclePacket packet, Context context)
    {
        context.getPlayer().ifPresent(player -> {
            ResourceKey<Level> playerDimension = player.level().dimension();

            if (playerDimension.equals(packet.dimension))
            {
                SeasonHandlerClient.clientSeasonCycleTicks.put(playerDimension, packet.seasonCycleTicks);
            }
        });
    }
}
