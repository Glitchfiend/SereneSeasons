package sereneseasons.api.config;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import sereneseasons.core.SereneSeasons;
import sereneseasons.handler.PacketHandler;
import sereneseasons.network.message.MessageSyncConfigs;
import sereneseasons.util.JavaUtils;

import java.util.Map;

public class SyncedConfig
{
    public static Map<String, SyncedConfigEntry> optionsToSync = Maps.newHashMap();	// Uses NBT Name

    public static void addOption(ISyncedOption option, String defaultValue)
    {
        optionsToSync.put(option.getNBTOptionName(), new SyncedConfigEntry(defaultValue));
    }

    public static boolean getBooleanValue(ISyncedOption option)
    {
        return Boolean.valueOf(getValue(option));
    }

    public static int getIntValue(ISyncedOption option)
    {
        return Integer.valueOf(getValue(option));
    }

    public static String getValue(ISyncedOption option)
    {
        return optionsToSync.get(option.getNBTOptionName()).value;
    }

    public static void restoreDefaults()
    {
        for (SyncedConfigEntry entry : optionsToSync.values())
        {
            entry.value = entry.defaultValue;
        }
    }
    
    public static boolean allowedToSendConfig() {
    	// TODO: Fix this hackery
    	if( !JavaUtils.isClassExisting("net.minecraft.client.Minecraft") )
    		return true;  // If Minecraft class is missing, then it is a dedicated server

    	Minecraft mc = Minecraft.getMinecraft();
    	if( mc.isSingleplayer() )
    		return true;
    	if( mc.world == null ) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Distributes actual configuration to one or all clients.
     * 
     * @param player the player to send the configuration to.
     *        If <code>null</code> then configuration is sent to every player.
     */
    public static void sendConfigUpdate(EntityPlayerMP player) {
    	if( !allowedToSendConfig() )
    		return;
    	MessageSyncConfigs msg = new MessageSyncConfigs(toNBT());
    	if( player != null )
    		PacketHandler.instance.sendTo(msg, player);
    	else
    		PacketHandler.instance.sendToAll(msg);
    }
    
    public static NBTTagCompound toNBT() {
    	NBTTagCompound nbt = new NBTTagCompound();
    	
    	for( Map.Entry<String, SyncedConfigEntry> entry : optionsToSync.entrySet() ) {
    		nbt.setString(entry.getKey(), entry.getValue().value);
    	}
    	
    	return nbt;
    }
    
    public static void applyFromNBT(NBTTagCompound nbt) {
        for (String key : nbt.getKeySet())
        {
            SyncedConfig.SyncedConfigEntry entry = SyncedConfig.optionsToSync.get(key);
            
            if (entry == null) SereneSeasons.logger.error("Option " + key + " does not exist locally!");
            
            entry.value = nbt.getString(key);
        }
    }

    //////////////
    
    public static class SyncedConfigEntry
    {
        public String value;
        public final String defaultValue;

        public SyncedConfigEntry(String defaultValue)
        {
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }
    }
}
