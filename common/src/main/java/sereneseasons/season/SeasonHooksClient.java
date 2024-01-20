/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.season;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class SeasonHooksClient
{
    public static Biome.Precipitation getPrecipitationAtLevelRendererHook(Holder<Biome> biome, BlockPos pos)
    {
        Level level = Minecraft.getInstance().level;

        if (!SeasonHooks.hasPrecipitationSeasonal(level, biome))
        {
            return Biome.Precipitation.NONE;
        }
        else
        {
            return SeasonHooks.coldEnoughToSnowSeasonal(level, biome, pos) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
        }
    }
}
