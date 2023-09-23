package sereneseasons.handler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import sereneseasons.core.SereneSeasons;
import sereneseasons.network.message.MessageSyncSeasonCycle;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler
{
    public static final int PROTOCOL_VERSION = 0;
    public static final SimpleChannel HANDLER = ChannelBuilder
            .named(new ResourceLocation(SereneSeasons.MOD_ID, "main_channel"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .simpleChannel();
    private static int nextFreeIndex;

    public static void init()
    {
        registerMessage(MessageSyncSeasonCycle.class, MessageSyncSeasonCycle::encode, MessageSyncSeasonCycle::decode, MessageSyncSeasonCycle.Handler::handle);
    }

    private static <T> void registerMessage(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
                                            BiConsumer<T, CustomPayloadEvent.Context> consumer) {
        HANDLER.messageBuilder(type).encoder(encoder).decoder(decoder).consumerMainThread(consumer).add();
    }

}
