package sereneseasons.season.journal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import sereneseasons.api.season.Season;
import sereneseasons.core.SereneSeasons;

/**
 * Object representing the journal.
 */
public class SeasonJournal implements INBTSerializable<NBTTagCompound> {
    private boolean isLastColdState = false;
    private boolean isLastRainyState = false;
    private List<WeatherJournalRecord> journalEntries = new ArrayList<WeatherJournalRecord>();

    /**
     * The constructor.
     */
    public SeasonJournal() {
    }
    
    /**
     * {@inheritDoc}
     */
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		
		NBTTagList recordTagList = new NBTTagList();
		for( WeatherJournalRecord rec : journalEntries ) {
			recordTagList.appendTag(rec.serializeNBT());
		}
		nbt.setTag("Records", recordTagList);

		return nbt;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if( nbt != null ) {
			NBTTagList recordTagList = nbt.getTagList("Records", 10);
			journalEntries = new ArrayList<WeatherJournalRecord>(recordTagList.tagCount());
			for( int i = 0; i < recordTagList.tagCount(); i ++ ) {
				NBTTagCompound tag = recordTagList.getCompoundTagAt(i);
				
				WeatherJournalRecord record = new WeatherJournalRecord();
				record.deserializeNBT(tag);
				journalEntries.add(record);
			}
		}

        determineLastState();
	}
	
    /**
     * Determines {@link #isLastColdState} and {@link #isLastRainyState} states for the latest journal entry.
     */
    private void determineLastState()
    {
    	// IMPORTANT: Keep in synch, if first minecraft day is starting in a different season with a different weather.
    	
        int lastColdState = -1;
        int lastRainyState = -1;
        for (int i = journalEntries.size() - 1; i >= 0; i--)
        {
            WeatherJournalRecord je = journalEntries.get(i);
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
    
    public List<WeatherJournalRecord> getJournalEvents() {
    	return Collections.unmodifiableList(journalEntries);
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
            for (int i = atIdx; i < journalEntries.size(); i++)
            {
                WeatherJournalRecord je = journalEntries.get(i);
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
            for (int i = atIdx; i < journalEntries.size(); i++)
            {
                WeatherJournalRecord je = journalEntries.get(i);
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

        for (int i = 0; i < journalEntries.size(); i++)
        {
            if (journalEntries.get(i).getTimeStamp() >= timeStamp)
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

        journalEntries.add(new WeatherJournalRecord(w.getTotalWorldTime(), eventType));
    }

    /**
     * Is called from event handlers, like {@link sereneseasons.handler.season.SeasonHandler#onWorldTick(WorldTickEvent)}.
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
}
