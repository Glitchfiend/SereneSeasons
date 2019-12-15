package sereneseasons.asm.crops;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import sereneseasons.handler.season.SeasonalCropGrowthHandler;

public class Hooks
{
    public static Result fireAllowPlantGrowthEvent(Block block, World world, int x, int y, int z, Random random)
    {
        return SeasonalCropGrowthHandler.onCropGrowth(block, world, x, y, z);
    }
}
