package sereneseasons.season.journal;

/**
 * Weather change event type as recorded in journal. <br/>
 * <br/>
 * <b>Important</b>: To maintain backwards compatibility the stored code should not be changed
 * for an event type or reused for a different type.  
 */
public enum WeatherEventType
{
    EVENT_UNKNOWN(0),	// Unknown should be always 0 due to NBT reading!
    EVENT_TO_COLD_SEASON(1),
    EVENT_TO_WARM_SEASON(2),
    EVENT_START_RAINING(3),
    EVENT_STOP_RAINING(4);

    private int code;

    WeatherEventType(int code)
    {
        this.code = code;
    }

    /**
     * Returns enumeration item by code (as stored in NBT)
     * 
     * @param code an integer identifying the event type.
     * @return the event type.
     */
    public static WeatherEventType fromCode(int code)
    {
        for (WeatherEventType etype : values())
        {
            if (etype.code == code)
                return etype;
        }
        return EVENT_UNKNOWN;
    }

    /**
     * Returns a code from event type.
     * 
     * @return a code to be stored in NBT.
     */
    public int getCode()
    {
        return code;
    }
}
