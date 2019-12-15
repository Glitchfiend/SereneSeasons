package sereneseasons.handler;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import sereneseasons.core.SereneSeasons;
import sereneseasons.network.message.MessageSyncConfigs;
import sereneseasons.network.message.MessageSyncSeasonCycle;

public class PacketHandler
{
    public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(SereneSeasons.MOD_ID);

    public static void init()
    {
        instance.registerMessage(MessageSyncSeasonCycle.class, MessageSyncSeasonCycle.class, 3, Side.CLIENT);
        instance.registerMessage(MessageSyncConfigs.class, MessageSyncConfigs.class, 4, Side.CLIENT);
    }
}
