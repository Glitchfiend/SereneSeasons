/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.season;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.util.biome.BiomeUtil;

public class SeasonHooks
{
    //
    // Hooks called by ASM
    //

    public static float getBiomeTemperatureCachedHook(RegistryKey<Biome> key, BlockPos pos, IWorldReader world)
    {
        Biome biome = BiomeUtil.getBiome(key);

        if (!(world instanceof World))
        {
            return biome.getTemperature(pos);
        }

        return getBiomeTemperature((World)world, key, pos);
    }

    //
    // General utilities
    //

    public static float getBiomeTemperature(World world, RegistryKey<Biome> key, BlockPos pos)
    {
        Biome biome = BiomeUtil.getBiome(key);

        if (!SeasonsConfig.isDimensionWhitelisted(world.dimension()))
        {
            return biome.getTemperature(pos);
        }

        return getBiomeTemperatureInSeason(new SeasonTime(SeasonHelper.getSeasonState(world).getSeasonCycleTicks()).getSubSeason(), key, pos);
    }

    public static float getBiomeTemperatureInSeason(Season.SubSeason subSeason, RegistryKey<Biome> key, BlockPos pos)
    {
        Biome biome = BiomeUtil.getBiome(key);
        boolean tropicalBiome = BiomeConfig.usesTropicalSeasons(key);
        float biomeTemp = biome.getTemperature(pos);

        if (!tropicalBiome && biome.getBaseTemperature() <= 0.8F && BiomeConfig.enablesSeasonalEffects(key))
        {
            switch (subSeason)
            {
                default:
                    break;

                case LATE_SPRING: case EARLY_AUTUMN:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.1F, -0.5F, 2.0F);
                break;

                case MID_SPRING: case MID_AUTUMN:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.2F, -0.5F, 2.0F);
                break;

                case EARLY_SPRING: case LATE_AUTUMN:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.4F, -0.5F, 2.0F);
                break;

                case EARLY_WINTER: case MID_WINTER: case LATE_WINTER:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.8F, -0.5F, 2.0F);
                break;
            }
        }

        return biomeTemp;
    }

//    public boolean doesWaterFreeze(IWorldReader worldIn, BlockPos water, boolean mustBeAtEdge) {
//        if (this.func_225486_c(water) >= 0.15F) {
//            return false;
//        } else {
//            if (water.getY() >= 0 && water.getY() < worldIn.getDimension().getHeight() && worldIn.getLightFor(LightType.BLOCK, water) < 10) {
//                BlockState blockstate = worldIn.getBlockState(water);
//                IFluidState ifluidstate = worldIn.getFluidState(water);
//                if (ifluidstate.getFluid() == Fluids.WATER && blockstate.getBlock() instanceof FlowingFluidBlock) {
//                    if (!mustBeAtEdge) {
//                        return true;
//                    }
//
//                    boolean flag = worldIn.hasWater(water.west()) && worldIn.hasWater(water.east()) && worldIn.hasWater(water.north()) && worldIn.hasWater(water.south());
//                    if (!flag) {
//                        return true;
//                    }
//                }
//            }
//
//            return false;
//        }
//    }
//
//    public boolean doesSnowGenerate(IWorldReader worldIn, BlockPos pos) {
//        if (this.func_225486_c(pos) >= 0.15F) {
//            return false;
//        } else {
//            if (pos.getY() >= 0 && pos.getY() < 256 && worldIn.getLightFor(LightType.BLOCK, pos) < 10) {
//                BlockState blockstate = worldIn.getBlockState(pos);
//                if (blockstate.isAir(worldIn, pos) && Blocks.SNOW.getDefaultState().isValidPosition(worldIn, pos)) {
//                    return true;
//                }
//            }
//
//            return false;
//        }
//    }
}
