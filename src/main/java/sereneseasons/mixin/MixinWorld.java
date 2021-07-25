/*******************************************************************************
 * Copyright 2014-2020, the Serene Seasons Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.extensions.IForgeWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonHooks;

@Mixin(Level.class)
public abstract class MixinWorld implements LevelAccessor, AutoCloseable, IForgeWorld
{
    @Shadow
    abstract boolean isRaining();

    @Shadow
    abstract ResourceKey<Level> dimension();

    @Overwrite
    public boolean isRainingAt(BlockPos position)
    {
        if (!this.isRaining()) return false;
        else if (!this.canSeeSky(position)) return false;
        else if (this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, position).getY() > position.getY()) return false;
        else
        {
            Biome biome = this.getBiome(position);
            ResourceKey<Biome> biomeKey = this.getBiomeName(position).orElse(null);

            if (SeasonsConfig.isDimensionWhitelisted(this.dimension()) && BiomeConfig.enablesSeasonalEffects(biomeKey))
            {
                if (SeasonHooks.shouldRainInBiomeInSeason((Level)(Object)this, biomeKey))
                {
                    if (BiomeConfig.usesTropicalSeasons(biomeKey)) return true;
                    else return SeasonHooks.getBiomeTemperature((Level)(Object)this, biome, position) >= 0.15F;
                }
                else return false;
            }
            else
            {
                return biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.getTemperature(position) >= 0.15F;
            }
        }
    }
}
