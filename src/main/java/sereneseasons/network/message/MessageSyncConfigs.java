/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.network.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.core.SereneSeasons;

import java.util.function.Supplier;

public class MessageSyncConfigs
{
    public CompoundTag nbtOptions;

    public MessageSyncConfigs(CompoundTag nbtOptions)
    {
        this.nbtOptions = nbtOptions;
    }

    public static void encode(MessageSyncConfigs packet, FriendlyByteBuf buf)
    {
        buf.writeNbt(packet.nbtOptions);
    }

    public static MessageSyncConfigs decode(FriendlyByteBuf buf)
    {
        return new MessageSyncConfigs(buf.readNbt());
    }

    public static class Handler
    {
        public static void handle(final MessageSyncConfigs packet, Supplier<NetworkEvent.Context> context)
        {
            context.get().enqueueWork(() ->
            {
                for (String key : packet.nbtOptions.getAllKeys())
                {
                    SyncedConfig.SyncedConfigEntry entry = SyncedConfig.optionsToSync.get(key);

                    if (entry == null) SereneSeasons.logger.error("Option " + key + " does not exist locally!");

                    entry.value = packet.nbtOptions.getString(key);
                    SereneSeasons.logger.info("SS configuration synchronized with the server");
                }
            });
            context.get().setPacketHandled(true);
        }
    }
}
