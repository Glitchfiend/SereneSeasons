package sereneseasons.api.season;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldHooks
{
    /**
     * An override of {@link World#canSnowAt(BlockPos, boolean)}
     */
    public static boolean canSnowAtInSeason(World world, BlockPos pos, boolean checkLight, ISeasonState seasonState)
    {
        try
        {
            return (Boolean)Class.forName("sereneseasons.season.SeasonASMHelper").getMethod("canSnowAtInSeason", World.class, BlockPos.class, Boolean.class, ISeasonState.class).invoke(null, world, pos, checkLight, seasonState);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred calling canSnowAtInSeason", e);
        }
    }

    /**
     * An override of {@link World#canBlockFreeze(BlockPos, boolean)}
     */
    public static boolean canBlockFreezeInSeason(World world, BlockPos pos, boolean noWaterAdj, ISeasonState seasonState)
    {
        try
        {
            return (Boolean)Class.forName("sereneseasons.season.SeasonASMHelper").getMethod("canBlockFreezeInSeason", World.class, BlockPos.class, Boolean.class, ISeasonState.class).invoke(null, world, pos, noWaterAdj, seasonState);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred calling canBlockFreezeInSeason", e);
        }
    }

    /**
     * An override of {@link World#isRainingAt(BlockPos)}
     */
    public static boolean isRainingAtInSeason(World world, BlockPos pos, ISeasonState seasonState)
    {
        try
        {
            return (Boolean)Class.forName("sereneseasons.season.SeasonASMHelper").getMethod("isRainingAtInSeason", World.class, BlockPos.class, ISeasonState.class).invoke(null, world, pos, seasonState);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred calling isRainingAtInSeason", e);
        }
    }
}
