/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.api.season;

public interface ISeasonState
{
    /**
     * Get the duration of a single day. Normally this is
     * 24000 ticks.
     *
     * @return The duration in ticks
     */
    int getDayDuration();

    /**
     * Get the duration of a single sub season.
     *
     * @return The duration in ticks
     */
    int getSubSeasonDuration();

    /**
     * Get the duration of a single season.
     *
     * @return The duration in ticks
     */
    int getSeasonDuration();

    /**
     * Get the duration of an entire cycle (a 'year')
     *
     * @return The duration in ticks
     */
    int getCycleDuration();

    /**
     * The time elapsed in ticks for the current overall cycle.
     * A cycle can be considered equivalent to a year, and is comprised
     * of Summer, Autumn, Winter and Spring.
     *
     * @return The time in ticks
     */
    int getSeasonCycleTicks();

    default float getCycleProgress()
    {
        int duration = getSubSeasonDuration();
        if (duration <= 0)
            return 0.0F;

        int count = Season.SubSeason.VALUES.length;
        int index = (getSeasonCycleTicks() / duration) % count;
        double elapsed = (double)(getSeasonCycleTicks() % duration) / (double)duration;
        return clampProgress((float)((index + elapsed) / count));
    }

    default float getSubSeasonProgress()
    {
        int duration = getSubSeasonDuration();
        if (duration <= 0)
            return 0.0F;

        return clampProgress((float)((double)(getSeasonCycleTicks() % duration) / (double)duration));
    }

    private static float clampProgress(float progress)
    {
        return progress < 1.0F ? progress : Math.nextDown(1.0F);
    }

    /**
     * Get the number of days elapsed.
     *
     * @return The current day
     */
    int getDay();

    /**
     * Get the current sub season.
     *
     * @return The current sub season
     */
    Season.SubSeason getSubSeason();

    /**
     * Get the current season. This method is
     * mainly for convenience.
     *
     * @return The current season
     */
    Season getSeason();

    /**
     * Get the current tropical season. This method is
     * mainly for convenience.
     *
     * @return The current tropical season
     */
    Season.TropicalSeason getTropicalSeason();
}
