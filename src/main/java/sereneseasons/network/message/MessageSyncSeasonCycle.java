/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sereneseasons.handler.season.SeasonHandler;

public class MessageSyncSeasonCycle implements IMessage, IMessageHandler<MessageSyncSeasonCycle, IMessage>
{
    public int dimension;
    public int seasonCycleTicks;
    
    public MessageSyncSeasonCycle() {}
    
    public MessageSyncSeasonCycle(int dimension, int seasonCycleTicks)
    {
        this.dimension = dimension;
        this.seasonCycleTicks = seasonCycleTicks;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) 
    {
        this.dimension = buf.readInt();
        this.seasonCycleTicks = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) 
    {
        buf.writeInt(this.dimension);
        buf.writeInt(this.seasonCycleTicks);
    }

    @Override
    public IMessage onMessage(MessageSyncSeasonCycle message, MessageContext ctx)
    {
        if (ctx.side == Side.CLIENT)
        {
            if (Minecraft.getMinecraft().player == null) return null;
            int playerDimension = Minecraft.getMinecraft().player.dimension;

            if (playerDimension == message.dimension)
                SeasonHandler.clientSeasonCycleTicks = message.seasonCycleTicks;
        }
        
        return null;
    }
}
