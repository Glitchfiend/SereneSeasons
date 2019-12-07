package sereneseasons.handler;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import sereneseasons.core.SereneSeasons;
import sereneseasons.network.message.MessageSyncConfigs;
import sereneseasons.network.message.MessageSyncSeasonCycle;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler
{
    public static final String PROTOCOL_VERSION = Integer.toString(0);
    public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(SereneSeasons.MOD_ID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
    private static int nextFreeIndex;

    public static void init()
    {
        registerMessage(MessageSyncSeasonCycle.class, MessageSyncSeasonCycle::encode, MessageSyncSeasonCycle::decode, MessageSyncSeasonCycle.Handler::handle);
        registerMessage(MessageSyncConfigs.class, MessageSyncConfigs::encode, MessageSyncConfigs::decode, MessageSyncConfigs.Handler::handle);
    }

    private static <T> void registerMessage(Class<T> type, BiConsumer<T, PacketBuffer> encoder, Function<PacketBuffer, T> decoder,
                                            BiConsumer<T, Supplier<NetworkEvent.Context>> consumer) {
        HANDLER.registerMessage(nextFreeIndex++, type, encoder, decoder, consumer);
    }

}
