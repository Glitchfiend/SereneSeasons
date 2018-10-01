package sereneseasons.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import sereneseasons.api.config.SyncedConfig;

public class NetworkEventsHandler {
    
	@SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        
        if( player instanceof EntityPlayerMP ) {
        	SyncedConfig.sendConfigUpdate((EntityPlayerMP)player);
        }
    }
	
}
