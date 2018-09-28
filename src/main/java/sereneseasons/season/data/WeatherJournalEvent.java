package sereneseasons.season.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import sereneseasons.util.IDataStorable;

/**
 * Object holding a journal entry with a time stamp. 
 */
public class WeatherJournalEvent implements IDataStorable
{
    private long timeStamp;
    private WeatherEventType eventType;

    /**
     * The constructor. Used for streaming only.
     */
    public WeatherJournalEvent()
    {
    }

    /**
     * The constructor.
     * 
     * @param timeStamp the journal entry time stamp
     * @param eventType the event type for the weather change
     */
    public WeatherJournalEvent(long timeStamp, WeatherEventType eventType)
    {
        this.timeStamp = timeStamp;
        this.eventType = eventType;
    }

    /**
     * Returns the time stamp of the journal entry.
     * 
     * @return the time stamp.
     */
    public long getTimeStamp()
    {
        return timeStamp;
    }

    /**
     * Returns the weather change event of the journal entry. 
     * 
     * @return the weather change event.
     */
    public WeatherEventType getEventType()
    {
        return eventType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToStream(ObjectOutputStream os) throws IOException
    {
        os.writeLong(timeStamp);
        os.writeInt(eventType.getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readFromStream(ObjectInputStream is) throws IOException
    {
        timeStamp = is.readLong();
        eventType = WeatherEventType.fromCode(is.readInt());
    }
}