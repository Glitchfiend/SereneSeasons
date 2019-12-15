package squeek.applecore.api.plants;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import static cpw.mods.fml.common.eventhandler.Event.Result;
import static cpw.mods.fml.common.eventhandler.Event.HasResult;

public class PlantGrowthEvent extends Event
{
    /**
     * Fired each plant update tick to determine whether or not growth is allowed
     * for the {@link #block}.
     * 
     * This event is fired in various {@link Block#updateTick} overrides.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event uses the {@link Result}. {@link HasResult}<br>
     * {@link Result#DEFAULT} will use the vanilla conditionals.
     * {@link Result#ALLOW} will allow the growth tick without condition.
     * {@link Result#DENY} will deny the growth tick without condition.
     */
    @HasResult
    public static class AllowGrowthTick extends PlantGrowthEvent
    {
        public final Block block;
        public final World world;
        public final int x;
        public final int y;
        public final int z;
        public final Random random;

        public AllowGrowthTick(Block block, World world, int x, int y, int z, Random random)
        {
            this.block = block;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.random = random;
        }
    }

    /**
     * Fired after a plant grows from a growth tick.
     * 
     * This event is fired in various {@link Block#updateTick} overrides.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    public static class GrowthTick extends PlantGrowthEvent
    {
        public final Block block;
        public final World world;
        public final int x;
        public final int y;
        public final int z;
        public final int previousMetadata;

        public GrowthTick(Block block, World world, int x, int y, int z, int previousMetadata)
        {
            this.block = block;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.previousMetadata = previousMetadata;
        }

        public GrowthTick(Block block, World world, int x, int y, int z)
        {
            this(block, world, x, y, z, world.getBlockMetadata(x, y, z));
        }
    }
}