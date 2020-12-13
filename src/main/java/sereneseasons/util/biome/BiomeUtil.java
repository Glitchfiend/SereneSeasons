/*******************************************************************************
 * Copyright 2014-2019, the Serene Seasons Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.util.biome;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import sereneseasons.core.SereneSeasons;

import javax.annotation.Nullable;

public class BiomeUtil
{
    public static RegistryKey<Biome> getBiomeKey(Biome biome)
    {
        if (biome == null) throw new RuntimeException("Cannot get registry key for null biome");

        if (biome.delegate.name() == null)
        {
            if (FMLEnvironment.dist == Dist.CLIENT)
                return getClientKey(biome);
            else
                throw new RuntimeException("Failed to get registry key for biome!");
        }

        return RegistryKey.create(Registry.BIOME_REGISTRY, biome.delegate.name());
    }

    public static Biome getBiome(RegistryKey<Biome> key)
    {
        Biome biome = ForgeRegistries.BIOMES.getValue(key.location());
        if (biome == null)
        {
            if (FMLEnvironment.dist == Dist.CLIENT)
                return getClientBiome(key);
            else
                throw new RuntimeException("Attempted to get unregistered biome " + key);
        }
        return biome;
    }

    public static Biome getBiome(int id)
    {
        if (id == -1) throw new RuntimeException("Attempted to get biome with id -1");
        return getBiome(((ForgeRegistry<Biome>)ForgeRegistries.BIOMES).getKey(id));
    }

    public static int getBiomeId(Biome biome)
    {
        if (biome == null) throw new RuntimeException("Attempted to get id of null biome");
        int id = ((ForgeRegistry<Biome>)ForgeRegistries.BIOMES).getID(biome);
        if (id == -1) throw new RuntimeException("Biome id is -1 for biome " + biome.delegate.name());
        return id;
    }

    public static int getBiomeId(RegistryKey<Biome> key)
    {
        return getBiomeId(getBiome(key));
    }

    public static boolean exists(RegistryKey<Biome> key)
    {
        return ForgeRegistries.BIOMES.containsKey(key.location());
    }

    public static boolean exists(int id)
    {
        return getBiome(id) != null;
    }

    private static RegistryKey<Biome> getClientKey(Biome biome)
    {
        return Minecraft.getInstance().level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(biome).orElseThrow(() -> new RuntimeException("Failed to get client registry key for biome!"));
    }

    private static Biome getClientBiome(RegistryKey<Biome> key)
    {
        Biome biome = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).get(key);
        if (biome == null) new RuntimeException("Failed to get client biome for registry key!");
        return biome;
    }
}