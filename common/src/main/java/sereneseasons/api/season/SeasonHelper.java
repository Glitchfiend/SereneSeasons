/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.api.season;

import net.minecraft.core.BlockPos;
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

    public static boolean usesTropicalSeasons(Level level, BlockPos pos)
    {
        return dataProvider.usesTropicalSeasons(level.getBiome(pos));
    }

    public static boolean hasSeasons(Level level)
    {
        return dataProvider.hasSeasons(level);
    }

    public static boolean usesStandardSeasons(Level level, BlockPos pos)
    {
        return hasSeasons(level) && !usesTropicalSeasons(level, pos);
    }

    public static boolean changesGrassColor(Level level)
    {
        return dataProvider.changesGrassColor(level);
    }

    public static boolean changesFoliageColor(Level level)
    {
        return dataProvider.changesFoliageColor(level);
    }

    public static boolean changesBirchColor(Level level)
    {
        return dataProvider.changesBirchColor(level);
    }

    public interface ISeasonDataProvider
    {
        ISeasonState getServerSeasonState(Level level);
        ISeasonState getClientSeasonState(Level level);
        boolean usesTropicalSeasons(Holder<Biome> key);

        default boolean hasSeasons(Level level)
        {
            return true;
        }

        default boolean changesGrassColor(Level level)
        {
            return true;
        }

        default boolean changesFoliageColor(Level level)
        {
            return true;
        }

        default boolean changesBirchColor(Level level)
        {
            return true;
        }
    }
}
