package sereneseasons.api.season;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeHooks
{
    /**
     * An override of {@link Biome#getFloatTemperature(BlockPos)}
     */
    public static float getFloatTemperature(World world, Biome biome, BlockPos pos)
    {
        try
        {
            return (float)Class.forName("sereneseasons.season.SeasonASMHelper").getMethod("getFloatTemperature", World.class, Biome.class, BlockPos.class).invoke(null, world, biome, pos);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred calling getFloatTemperature", e);
        }
    }

    /**
     * An override of {@link Biome#getFloatTemperature(BlockPos)}
     */
    public static float getFloatTemperature(Season.SubSeason subSeason, Biome biome, BlockPos pos)
    {
        try
        {
            return (float)Class.forName("sereneseasons.season.SeasonASMHelper").getMethod("getFloatTemperature", Season.SubSeason.class, Biome.class, BlockPos.class).invoke(null, subSeason, biome, pos);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred calling getFloatTemperature", e);
        }
    }
}
