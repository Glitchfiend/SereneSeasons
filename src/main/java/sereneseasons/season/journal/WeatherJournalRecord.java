package sereneseasons.season.journal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import sereneseasons.util.IDataStorable;

/**
 * Object holding a journal entry with a time stamp. 
 */
public class WeatherJournalRecord implements INBTSerializable<NBTTagCompound> //, IDataStorable
{
    private long timeStamp;
    private WeatherEventType eventType;

    /**
     * The constructor. Used for streaming only.
     */
    WeatherJournalRecord()
    {
    }

    /**
     * The constructor.
     * 
     * @param timeStamp the journal entry time stamp
     * @param eventType the event type for the weather change
     */
    public WeatherJournalRecord(long timeStamp, WeatherEventType eventType)
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
//    @Override
//    public void writeToStream(ObjectOutputStream os) throws IOException
//    {
//        os.writeLong(timeStamp);
//        os.writeInt(eventType.getCode());
//    }

    /**
     * {@inheritDoc}
     */
//    @Override
//    public void readFromStream(ObjectInputStream is) throws IOException
//    {
//        timeStamp = is.readLong();
//        eventType = WeatherEventType.fromCode(is.readInt());
//    }

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setLong("TimeStamp", timeStamp);
		nbt.setInteger("Type", eventType.getCode());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		timeStamp = nbt.getLong("TimeStamp");
		eventType = WeatherEventType.fromCode(nbt.getInteger("Type"));
	}
}