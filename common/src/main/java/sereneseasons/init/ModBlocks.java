package sereneseasons.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import sereneseasons.api.SSBlocks;
import sereneseasons.block.SeasonSensorBlock;
import sereneseasons.core.SereneSeasons;

import java.util.function.BiConsumer;

public class ModBlocks
{
    public static void registerBlocks(BiConsumer<ResourceLocation, Block> func)
    {
        SSBlocks.SEASON_SENSOR = register(func, new SeasonSensorBlock(Block.Properties.of().strength(0.2F).sound(SoundType.STONE)), "season_sensor");
    }

    private static Block register(BiConsumer<ResourceLocation, Block> func, Block block, String name)
    {
        func.accept(new ResourceLocation(SereneSeasons.MOD_ID, name), block);
        return block;
    }
}
