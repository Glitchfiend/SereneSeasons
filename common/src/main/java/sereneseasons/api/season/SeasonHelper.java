/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.api.season;

import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class SeasonHelper 
{
    public static ISeasonDataProvider dataProvider;

    /** 
     * Obtains data about the state of the season cycle in the world. This works both on
     * the client and the server.
     */
    public static ISeasonState getSeasonState(Level level)
    {
        ISeasonState data;

        if (!level.isClientSide())
        {
            data = dataProvider.getServerSeasonState(level);
        }
        else
        {
            data = dataProvider.getClientSeasonState(level);
        }

        return data;
    }

    /**
     * Check whether a biome uses tropical seasons.
     * @param biome the biome to check.
     * @return whether the biome uses tropical seasons.
     */
    public static boolean usesTropicalSeasons(Holder<Biome> biome)
    {
        return dataProvider.usesTropicalSeasons(biome);
    }

    public interface ISeasonDataProvider
    {
        ISeasonState getServerSeasonState(Level level);
        ISeasonState getClientSeasonState(Level level);
        boolean usesTropicalSeasons(Holder<Biome> key);
    }
}
