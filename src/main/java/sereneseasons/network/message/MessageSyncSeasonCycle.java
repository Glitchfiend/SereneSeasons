/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sereneseasons.handler.season.SeasonHandler;

import java.util.function.Supplier;

public class MessageSyncSeasonCycle
{
    public int dimension;
    public int seasonCycleTicks;
    
    public MessageSyncSeasonCycle() {}
    
    public MessageSyncSeasonCycle(int dimension, int seasonCycleTicks)
    {
        this.dimension = dimension;
        this.seasonCycleTicks = seasonCycleTicks;
    }

    public static void encode(MessageSyncSeasonCycle packet, PacketBuffer buf)
    {
        buf.writeInt(packet.dimension);
        buf.writeInt(packet.seasonCycleTicks);
    }

    public static MessageSyncSeasonCycle decode(PacketBuffer buf)
    {
        return new MessageSyncSeasonCycle(buf.readInt(), buf.readInt());
    }

    public static class Handler
    {
        public static void handle(final MessageSyncSeasonCycle packet, Supplier<NetworkEvent.Context> context)
        {
            context.get().enqueueWork(() ->
            {
                if (Minecraft.getInstance().player == null) return;
                int playerDimension = Minecraft.getInstance().player.dimension.getId();

                if (playerDimension == packet.dimension)
                    SeasonHandler.clientSeasonCycleTicks.replace(playerDimension, packet.seasonCycleTicks);
            });
            context.get().setPacketHandled(true);
        }
    }
}
