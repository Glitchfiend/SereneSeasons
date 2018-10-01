package sereneseasons.api.config;

import com.google.common.collect.Maps;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.core.SereneSeasons;
import sereneseasons.handler.PacketHandler;
import sereneseasons.network.message.MessageSyncConfigs;

import java.util.Map;

public class SyncedConfig
{
    public static Map<String, SyncedConfigEntry> optionsToSync = Maps.newHashMap();	// Uses NBT Name

    public static void addOption(ISyncedOption option, String defaultValue)
    {
    	if( optionsToSync.containsKey(option.getNBTOptionName()) )
    		throw new IllegalArgumentException("Ambiguous NBT comform option names.");
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
    
    /**
     * Distributes actual configuration to one or all clients.
     * 
     * @param player the player to send the configuration to.
     *        If <code>null</code> then configuration is sent to every player.
     */
    public static void sendConfigUpdate(EntityPlayerMP player) {
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
