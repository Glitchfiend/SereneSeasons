package sereneseasons.handler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sereneseasons.world.chunk.ChunkKey;
import sereneseasons.world.chunk.SeasonChunkData;
import sereneseasons.world.chunk.SeasonChunkManager;

public class ChunkHandler {
	@SubscribeEvent
	public void onChunkSave(ChunkDataEvent.Save event) {
		SeasonChunkData chunkData = SeasonChunkManager.INSTANCE.getStoredChunkData(event.getChunk(), false);
		if( chunkData != null ) {
			NBTTagCompound nbt = chunkData.serializeNBT();
			event.getData().setTag("SereneSeasons", nbt);
		}
	}
	
	@SubscribeEvent
	public void onChunkLoad(ChunkDataEvent.Load event) {
		NBTTagCompound nbt = event.getData().getCompoundTag("SereneSeasons");
		if( nbt == null )
			return;
		if( !SeasonChunkData.hasNBTData(nbt) )
			return;
		
		Chunk chunk = event.getChunk();
		ChunkKey key = new ChunkKey(chunk.getPos(), event.getWorld());
		
		SeasonChunkData chunkData = new SeasonChunkData(key, chunk, 0);
		chunkData.deserializeNBT(nbt);
		SeasonChunkManager.INSTANCE.registerChunkData(chunkData);		
	}
	
    @SubscribeEvent
    public void onWorldUnloaded(WorldEvent.Unload event)
    {
        World world = event.getWorld();
        if (world.isRemote)
            return;

        SeasonChunkManager.INSTANCE.onWorldUnload(world);
        // Season data cleanup
//        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
//        seasonData.onWorldUnload(world);
    }

    @SubscribeEvent
    public void onChunkUnloaded(ChunkEvent.Unload event)
    {
        if (event.getWorld().isRemote)
            return;

        Chunk chunk = event.getChunk();
        SeasonChunkManager.INSTANCE.onChunkUnloaded(chunk);

//        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(chunk.getWorld());
//        seasonData.onChunkUnloaded(chunk);
    }
}
