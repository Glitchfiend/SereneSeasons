/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.network.message;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.core.SereneSeasons;

import java.util.function.Supplier;

public class MessageSyncConfigs
{
    public CompoundNBT nbtOptions;

    public MessageSyncConfigs(CompoundNBT nbtOptions)
    {
        this.nbtOptions = nbtOptions;
    }

    public static void encode(MessageSyncConfigs packet, PacketBuffer buf)
    {
        buf.writeNbt(packet.nbtOptions);
    }

    public static MessageSyncConfigs decode(PacketBuffer buf)
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
