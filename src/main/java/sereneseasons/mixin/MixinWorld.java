/*******************************************************************************
 * Copyright 2014-2020, the Serene Seasons Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.mixin;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.extensions.IForgeWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonHooks;

@Mixin(World.class)
public abstract class MixinWorld implements IWorld, AutoCloseable, IForgeWorld
{
    @Shadow
    abstract boolean isRaining();

    @Shadow
    abstract RegistryKey<World> dimension();

    @Overwrite
    public boolean isRainingAt(BlockPos position)
    {
        if (!this.isRaining()) return false;
        else if (!this.canSeeSky(position)) return false;
        else if (this.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, position).getY() > position.getY()) return false;
        else
        {
            Biome biome = this.getBiome(position);
            RegistryKey<Biome> biomeKey = this.getBiomeName(position).orElse(null);

            if (SeasonsConfig.isDimensionWhitelisted(this.dimension()) && BiomeConfig.enablesSeasonalEffects(biomeKey))
            {
                if (SeasonHooks.shouldRainInBiomeInSeason((World)(Object)this, biomeKey))
                {
                    if (BiomeConfig.usesTropicalSeasons(biomeKey)) return true;
                    else return SeasonHooks.getBiomeTemperature((World)(Object)this, biome, position) >= 0.15F;
                }
                else return false;
            }
            else
            {
                return biome.getPrecipitation() == Biome.RainType.RAIN && biome.getTemperature(position) >= 0.15F;
            }
        }
    }
}
