/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.config;

import java.io.File;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeConfig
{
	public static void init(File configDir)
	{

	}

	public static boolean enablesSeasonalEffects(BiomeGenBase biome)
	{
		return true;
	}

	public static boolean usesTropicalSeasons(BiomeGenBase biome)
	{
		return BiomeDictionary.isBiomeOfType(biome, Type.SANDY);
	}

	public static boolean disablesCrops(BiomeGenBase biome)
	{
		return BiomeDictionary.isBiomeOfType(biome, Type.DEAD) || BiomeDictionary.isBiomeOfType(biome, Type.WASTELAND);
	}
}
