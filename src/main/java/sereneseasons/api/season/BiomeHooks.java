package sereneseasons.api.season;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeHooks
{
    /**
     * An override of {@link Biome#getFloatTemperature(Biome, BlockPos)}
     */
    public static float getFloatTemperature(Biome biome, BlockPos pos)
    {
        try
        {
            return (float)Class.forName("sereneseasons.season.SeasonASMHelper").getMethod("getFloatTemperature", Biome.class, BlockPos.class).invoke(null, biome, pos);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred calling getFloatTemperature", e);
        }
    }
}
