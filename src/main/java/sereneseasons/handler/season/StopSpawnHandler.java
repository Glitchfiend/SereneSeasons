/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;

public class StopSpawnHandler 
{
    //Animals shouldn't spawn during winter
    @SubscribeEvent
    public void onCheckEntitySpawn(LivingSpawnEvent.CheckSpawn event)
    {
        Season season = SeasonHelper.getSeasonState(event.getWorld()).getSubSeason().getSeason();
        Biome biome = event.getWorld().getBiome(new BlockPos(event.getX(), event.getY(), event.getZ()));

        if (!BiomeConfig.usesTropicalSeasons(biome) && season == Season.WINTER && event.getEntity() instanceof EntityAnimal)
        {
            event.setResult(Result.DENY);
        }
    }
    
    @SubscribeEvent
    public void onChunkPopulate(PopulateChunkEvent.Populate event)
    {
        World world = event.getWorld();
        Season season = SeasonHelper.getSeasonState(world).getSubSeason().getSeason();
        Biome biome = world.getBiome(new BlockPos(event.getChunkX() * 16, 0, event.getChunkZ() * 16));

        //Prevent animals from spawning in new chunks during the winter
        if (event.getType() == EventType.ANIMALS && season == Season.WINTER && !BiomeConfig.usesTropicalSeasons(biome))
        {
            event.setResult(Result.DENY);
        }
    }
}
