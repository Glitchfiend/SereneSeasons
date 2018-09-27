/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.season;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldSavedData;
import sereneseasons.api.season.Season;
import sereneseasons.core.SereneSeasons;
import sereneseasons.util.DataUtils;
import sereneseasons.util.IDataStorable;

public class SeasonSavedData extends WorldSavedData
{
    public static final String DATA_IDENTIFIER = "seasons";

    public int seasonCycleTicks;

    private boolean isLastColdState = false;
    private boolean isLastRainyState = false;
    public List<WeatherJournalEvent> journal = new ArrayList<WeatherJournalEvent>();

    public HashMap<ChunkKey, ChunkData> managedChunks = new HashMap<ChunkKey, ChunkData>();

    public SeasonSavedData()
    {
        this(DATA_IDENTIFIER);
    }

    // This specific constructor is required for saving to occur
    public SeasonSavedData(String identifier)
    {
        super(identifier);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        this.seasonCycleTicks = nbt.getInteger("SeasonCycleTicks");
        try
        {
            this.journal = DataUtils.toListStorable(nbt.getByteArray("WeatherJournal"), WeatherJournalEvent.class);
        }
        catch (IOException e)
        {
            SereneSeasons.logger.error("Couldn't retrieve weather journal. Use a clear one.", e);
            this.journal = new ArrayList<WeatherJournalEvent>();
        }

        try
        {
            List<ChunkDataStorage> timeStamps = DataUtils.toListStorable(nbt.getByteArray("ChunkExtraInfo"), ChunkDataStorage.class);
            applyLastPatchedTimes(timeStamps);
        }
        catch (IOException e)
        {
        	SereneSeasons.logger.error("Couldn't load chunk patch timestamps. Some chunks won't be in synch with season.", e);
        }

        determineLastState();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("SeasonCycleTicks", this.seasonCycleTicks);
        try
        {
            nbt.setByteArray("WeatherJournal", DataUtils.toBytebufStorable(journal));
        }
        catch (IOException e)
        {
        	SereneSeasons.logger.error("Couldn't store weather journal.", e);
        }

        try
        {
            nbt.setByteArray("ChunkExtraInfo", DataUtils.toBytebufStorable(toLastPatchedTimeStorable()));
        }
        catch (IOException e)
        {
        	SereneSeasons.logger.error("Couldn't store chunk patch timestamps. Some chunks won't be in synch with season.", e);
        }

        return nbt;
    }

    /**
     * Creates a list of stored chunks ready to be written to NBT.
     * 
     * @return a list of chunk meta data to be stored.
     */
    private List<ChunkDataStorage> toLastPatchedTimeStorable()
    {
        int size = managedChunks.size();
        ArrayList<ChunkDataStorage> result = new ArrayList<ChunkDataStorage>(size);
        for (Map.Entry<ChunkKey, ChunkData> entry : managedChunks.entrySet())
        {
            result.add(new ChunkDataStorage(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    /**
     * Transfers readen NBT data to stored chunk meta data.
     * 
     * @param list a list of readen chunk meta data. 
     */
    private void applyLastPatchedTimes(List<ChunkDataStorage> list)
    {
        for (ChunkDataStorage entry : list)
        {
            ChunkData data = managedChunks.get(entry.getKey());
            if (data != null)
            {
                data.setPatchTimeTo(data.getLastPatchedTime());
            }
            else
            {
                data = new ChunkData(entry.getKey(), null, entry.getLastPatchedTime());
                managedChunks.put(entry.getKey(), data);
            }
        }
    }

    /**
     * Determines {@link #isLastColdState} and {@link #isLastRainyState} states for the latest journal entry.
     */
    private void determineLastState()
    {
    	// IMPORTANT: Keep in synch, if first minecraft day is starting in a different season with a different weather.
    	
        int lastColdState = -1;
        int lastRainyState = -1;
        for (int i = journal.size() - 1; i >= 0; i--)
        {
            WeatherJournalEvent je = journal.get(i);
            WeatherEventType etype = je.getEventType();

            switch (etype)
            {
                case EVENT_TO_COLD_SEASON:
                    if (lastColdState == -1)
                        lastColdState = 1;
                    break;
                case EVENT_TO_WARM_SEASON:
                    if (lastColdState == -1)
                        lastColdState = 0;
                    break;
                case EVENT_START_RAINING:
                    if (lastRainyState == -1)
                        lastRainyState = 1;
                    break;
                case EVENT_STOP_RAINING:
                    if (lastRainyState == -1)
                        lastRainyState = 0;
                    break;
                case EVENT_UNKNOWN:
                	SereneSeasons.logger.warn("Unknown weather journal entry found.");
            }

            // Is now fully determined?
            if (lastColdState != -1 && lastRainyState != -1)
                break;
        }

        isLastColdState = (lastColdState == 1);  // -1 state is Default: First
                                                  // minecraft day is at spring.
        isLastRainyState = (lastRainyState == 1); // -1 state is Default: First
                                                  // minecraft day has no rain.
    }

    /**
     * Returns if it was raining at the time of a specific journal entry. 
     * 
     * @param atIdx the journal index.
     * @return <code>true</code> iff yes.
     */
    public boolean wasLastRaining(int atIdx)
    {
        if (atIdx != -1)
        {
            for (int i = atIdx; i < journal.size(); i++)
            {
                WeatherJournalEvent je = journal.get(i);
                WeatherEventType etype = je.getEventType();

                switch (etype)
                {
                    case EVENT_START_RAINING:
                        return false;
                    case EVENT_STOP_RAINING:
                        return true;
                    default:
                }
            }
        }

        return isLastRainyState;
    }

    /**
     * Returns if it was cold at the time of a specific journal entry. 
     * 
     * @param atIdx the journal index.
     * @return <code>true</code> iff yes.
     */
    public boolean wasLastCold(int atIdx)
    {
        if (atIdx != -1)
        {
            for (int i = atIdx; i < journal.size(); i++)
            {
                WeatherJournalEvent je = journal.get(i);
                WeatherEventType etype = je.getEventType();

                switch (etype)
                {
                    case EVENT_TO_COLD_SEASON:
                        return false;
                    case EVENT_TO_WARM_SEASON:
                        return true;
                    default:
                }
            }
        }

        return isLastColdState;
    }

    /**
     * Returns the index of the next journal entry after a given time stamp.
     * 
     * @param timeStamp the time stamp.
     * @return the index of the journal entry.
     */
    public int getJournalIndexAfterTime(long timeStamp)
    {
        // FIXME: Use subdivision to find the time point in approx. O(log n) steps.
    	//        Performance issues are expected for a longer server run.
        //        Or use some sort of caching to amortize costs.

        for (int i = 0; i < journal.size(); i++)
        {
            if (journal.get(i).getTimeStamp() >= timeStamp)
                return i;
        }

        return -1;
    }

    /**
     * Adds a recent journal entry.
     * 
     * @param w the world
     * @param eventType the event type
     */
    private void addEvent(World w, WeatherEventType eventType)
    {
        switch (eventType)
        {
            case EVENT_TO_COLD_SEASON:
                isLastColdState = true;
                break;
            case EVENT_TO_WARM_SEASON:
                isLastColdState = false;
                break;
            case EVENT_START_RAINING:
                isLastRainyState = true;
                break;
            case EVENT_STOP_RAINING:
                isLastRainyState = false;
                break;
            case EVENT_UNKNOWN:
            	SereneSeasons.logger.warn("Unknown weather event added. Ignoring");
                return;
        }

        journal.add(new WeatherJournalEvent(w.getTotalWorldTime(), eventType));
    }

    /**
     * Is called from {@link sereneseasons.handler.season.SeasonHandler#onWorldTick(WorldTickEvent)}.
     * Decides whether to add new journal element based on current weather and season state.
     * 
     * @param w the world.
     * @param curSeason the current season.
     */
    public void updateJournal(World w, Season curSeason)
    {
        if (curSeason == Season.WINTER && !wasLastCold(-1))
            addEvent(w, WeatherEventType.EVENT_TO_COLD_SEASON);
        else if (curSeason != Season.WINTER && wasLastCold(-1))
            addEvent(w, WeatherEventType.EVENT_TO_WARM_SEASON);

        if (w.isRaining() && !wasLastRaining(-1))
            addEvent(w, WeatherEventType.EVENT_START_RAINING);
        else if (!w.isRaining() && wasLastRaining(-1))
            addEvent(w, WeatherEventType.EVENT_STOP_RAINING);
    }

    /**
     * Returns a meta data entry for a chunk. Can create a new one if not existing. 
     * 
     * @param chunk the chunk
     * @param bCreateIfNotExisting if <code>true</code> then a new entry is created if not existing.
     * @return the chunk meta data for seasons.
     */
    public ChunkData getStoredChunkData(Chunk chunk, boolean bCreateIfNotExisting)
    {
        ChunkPos cpos = chunk.getPos();
        ChunkKey key = new ChunkKey(cpos, chunk.getWorld());
        ChunkData chunkData = managedChunks.get(key);
        if (chunkData != null)
        {
            Chunk curChunk = chunkData.getChunk();
            if (curChunk != null)
            {
                if (curChunk != chunk)
                {
                    if (curChunk.isLoaded())
                        SereneSeasons.logger.error("Chunk mismatching in SeasonSavedData.getStoredChunkData .");
                    curChunk = null;
                }
            }

            if (curChunk == null)
            {
                if (bCreateIfNotExisting)
                {
                    chunkData.attachLoadedChunk(chunk);
                }
                else
                    return null;
            }
            return chunkData;
        }
        if (!bCreateIfNotExisting)
            return null;

        long lastPatchTime = 0; // Initial time. Should be bigger than
                                // ActiveChunkMarker.getSmallerKey() value!

        chunkData = new ChunkData(key, chunk, lastPatchTime);
        managedChunks.put(key, chunkData);
        return chunkData;
    }

    /**
     * Returns a meta data entry for a chunk key. Can create a new one if not existing. 
     * 
     * @param key the chunk key.
     * @param bCreateIfNotExisting if <code>true</code> then a new entry is created if not existing.
     * @return the chunk meta data for seasons.
     */
    public ChunkData getStoredChunkData(ChunkKey key, boolean bCreateIfNotExisting)
    {
        ChunkData chunkData = managedChunks.get(key);
        if (chunkData == null && bCreateIfNotExisting)
        {
            long lastPatchTime = 0; // Initial time. Should be bigger than
                                    // ActiveChunkMarker.getSmallerKey() value!

            chunkData = new ChunkData(key, null, lastPatchTime);
            managedChunks.put(key, chunkData);
        }
        return chunkData;
    }

    /**
     * Returns a meta data entry for a chunk position. Can create a new one if not existing. 
     * 
     * @param world the world of the chunk.
     * @param pos the chunk position.
     * @param bCreateIfNotExisting if <code>true</code> then a new entry is created if not existing.
     * @return the chunk meta data for seasons.
     */
    public ChunkData getStoredChunkData(World world, ChunkPos pos, boolean bCreateIfNotExisting)
    {
        ChunkKey key = new ChunkKey(pos, world);
        return getStoredChunkData(key, bCreateIfNotExisting);
    }

    /**
     * Called from {@link sereneseasons.handler.season.SeasonHandler#onWorldUnloaded}. <br/>
     * Cleanup routine if a world got unloaded.
     * 
     * @param world the unloaded world.
     */
    public void onWorldUnload(World world)
    {
        // Clear managed chunk tags associated to the world
        managedChunks.clear(); // No BUG: managedChunks contains only chunks
                               // associated to the world.
    }

    /**
     * Called from {@link sereneseasons.handler.season.SeasonHandler#onChunkUnloaded}. <br/>
     * Cleanup routine if a chunk got unloaded. In this case the meta is detached from Chunk.
     *   
     * @param chunk the unloaded chunk.
     */
    public void onChunkUnloaded(Chunk chunk)
    {
        ChunkKey key = new ChunkKey(chunk.getPos(), chunk.getWorld());
        ChunkData chunkData = managedChunks.get(key);
        if (chunkData != null)
        {
            chunkData.detachLoadedChunk();
        }
    }
    
    //////////////////

    /**
     * Storage object for a chunk meta data {@link ChunkData}. 
     */
    public static class ChunkDataStorage implements IDataStorable
    {
        private ChunkKey key;
        private long lastPatchedTime;

        /**
         * The constructor. Used for streaming.
         */
        public ChunkDataStorage()
        {
            // For streaming
        }

        /**
         * The constructor.
         * 
         * @param key the chunk key identifying the chunk itself.
         * @param data
         */
        public ChunkDataStorage(ChunkKey key, ChunkData data)
        {
            this.key = key;
            this.lastPatchedTime = data.getLastPatchedTime();
        }

        /**
         * Returns the key identifying the chunk.
         * 
         * @return the chunk key.
         */
        public ChunkKey getKey()
        {
            return key;
        }

        /**
         * Returns the time stamp a chunk has been patched last time.
         * 
         * @return the time stamp.
         */
        public long getLastPatchedTime()
        {
            return lastPatchedTime;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void writeToStream(ObjectOutputStream os) throws IOException
        {
            os.writeInt(key.getPos().x);
            os.writeInt(key.getPos().z);
            os.writeInt(key.getDimension());
            os.writeLong(lastPatchedTime);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void readFromStream(ObjectInputStream is) throws IOException
        {
            int chunkXPos = is.readInt();
            int chunkZPos = is.readInt();
            int dimension = is.readInt();
            this.key = new ChunkKey(new ChunkPos(chunkXPos, chunkZPos), dimension);
            this.lastPatchedTime = is.readLong();
        }
    }
}
