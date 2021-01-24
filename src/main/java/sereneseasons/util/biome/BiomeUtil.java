/*******************************************************************************
 * Copyright 2014-2019, the Serene Seasons Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.util.biome;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import sereneseasons.core.SereneSeasons;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BiomeUtil
{
    private static List<World> worldList = Lists.newArrayList();

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
            {
                try
                {
                    // In extremely rare circumstances this may fail due to a world being corrupted.
                    // This is likely due to failure on the part of a dimension mod.
                    // Sadly, we will have to attempt to accommodate for this.
                    biome = getClientBiome(key);
                }
                catch (Exception e)
                {
                    SereneSeasons.logger.error(e.getMessage());
                }

                // No more fallbacks. If we fail here it's game over.
                if (biome == null)
                    biome = getBiomeFromWorlds(key);

                return biome;
            }
            else if (FMLEnvironment.dist == Dist.DEDICATED_SERVER)
            {
                return getBiomeFromWorlds(key);
            }
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

    @OnlyIn(Dist.CLIENT)
    private static Registry<Biome> getClientBiomeRegistry()
    {
        Minecraft minecraft = Minecraft.getInstance();
        World world = minecraft.level;
        if (world == null) throw new RuntimeException("Cannot acquire biome registry when the world is null.");
        return world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
    }

    @OnlyIn(Dist.CLIENT)
    private static RegistryKey<Biome> getClientKey(Biome biome)
    {
        return getClientBiomeRegistry().getResourceKey(biome).orElseThrow(() -> new RuntimeException("Failed to get client registry key for biome!"));
    }

    @OnlyIn(Dist.CLIENT)
    private static Biome getClientBiome(RegistryKey<Biome> key)
    {
        Biome biome = getClientBiomeRegistry().get(key);
        if (biome == null) new RuntimeException("Failed to get client biome for registry key " + key.location().toString() + "!");
        return biome;
    }

    private static Biome getBiomeFromWorlds(RegistryKey<Biome> key)
    {
        for (World world : worldList)
        {
            Biome biome = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).get(key);

            if (biome != null)
            {
                return biome;
            }
        }

        throw new RuntimeException("Failed to get biome for registry key " + key.location().toString() + " !");
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        worldList.add((World)event.getWorld());
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event)
    {
        worldList.remove((World)event.getWorld());
    }
}