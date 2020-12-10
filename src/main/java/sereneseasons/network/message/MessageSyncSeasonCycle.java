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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.handler.season.SeasonHandler;

import java.util.function.Supplier;

public class MessageSyncSeasonCycle
{
    public RegistryKey<World> dimension;
    public int seasonCycleTicks;
    
    public MessageSyncSeasonCycle() {}
    
    public MessageSyncSeasonCycle(RegistryKey<World> dimension, int seasonCycleTicks)
    {
        this.dimension = dimension;
        this.seasonCycleTicks = seasonCycleTicks;
    }

    public static void encode(MessageSyncSeasonCycle packet, PacketBuffer buf)
    {
        buf.writeUtf(packet.dimension.location().toString());
        buf.writeInt(packet.seasonCycleTicks);
    }

    public static MessageSyncSeasonCycle decode(PacketBuffer buf)
    {
        return new MessageSyncSeasonCycle(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(buf.readUtf())), buf.readInt());
    }

    public static class Handler
    {
        public static void handle(final MessageSyncSeasonCycle packet, Supplier<NetworkEvent.Context> context)
        {
            context.get().enqueueWork(() ->
            {
                if (Minecraft.getInstance().player == null) return;
                RegistryKey<World> playerDimension = Minecraft.getInstance().player.level.dimension();

                if (playerDimension.equals(packet.dimension))
                    SeasonHandler.clientSeasonCycleTicks.replace(playerDimension, packet.seasonCycleTicks);
            });
            context.get().setPacketHandled(true);
        }
    }
}
