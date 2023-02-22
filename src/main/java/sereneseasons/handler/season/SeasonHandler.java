/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.handler.season;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import sereneseasons.api.SSGameRules;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.ServerConfig;
import sereneseasons.handler.PacketHandler;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;
import sereneseasons.network.message.MessageSyncSeasonCycle;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

import java.util.HashMap;
import java.util.function.Supplier;

public class SeasonHandler implements SeasonHelper.ISeasonDataProvider
{
    @SubscribeEvent
    public void onWorldTick(TickEvent.LevelTickEvent event)
    {
        Level world = event.level;

        if (event.phase == TickEvent.Phase.END && !world.isClientSide)
        {
            if (!ServerConfig.progressSeasonWhileOffline.get())
            {
                MinecraftServer server = world.getServer();
                if (server != null && server.getPlayerList().getPlayerCount() == 0)
                    return;
            }

            // Only tick seasons if the game rule is enabled
            if (!world.getGameRules().getBoolean(SSGameRules.RULE_DOSEASONCYCLE))
                return;
                
            SeasonSavedData savedData = getSeasonSavedData(world);

            // Clamp season cycle ticks to prevent a bad state occurring
            savedData.seasonCycleTicks = Mth.clamp(savedData.seasonCycleTicks, 0, SeasonTime.ZERO.getCycleDuration());

            if (++savedData.seasonCycleTicks > SeasonTime.ZERO.getCycleDuration())
            {
                savedData.seasonCycleTicks = 0;
            }
            
            if (savedData.seasonCycleTicks % 20 == 0)
            {
                sendSeasonUpdate(world);
            }

            savedData.setDirty();
        }
    }
    
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        Player player = event.getEntity();
        Level world = player.level;
        
        sendSeasonUpdate(world);
    }

    private Season.SubSeason lastSeason = null;
    public static final HashMap<ResourceKey<Level>, Integer> clientSeasonCycleTicks = new HashMap<>();
    public static SeasonTime getClientSeasonTime() {
        Integer i = clientSeasonCycleTicks.get(Minecraft.getInstance().level.dimension());
    	return new SeasonTime(i == null ? 0 : i);
    }

    @SubscribeEvent
    public void onWorldLoaded(LevelEvent.Load event)
    {
        clientSeasonCycleTicks.clear();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) 
    {
        //Only do this when in the world
        if (Minecraft.getInstance().player == null) return;
        ResourceKey<Level> dimension = Minecraft.getInstance().player.level.dimension();

        if (event.phase == TickEvent.Phase.END && ServerConfig.isDimensionWhitelisted(dimension))
        {
            clientSeasonCycleTicks.compute(dimension, (k, v) -> v == null ? 0 : v + 1);
        	
            //Keep ticking as we're synchronized with the server only every second
            if (clientSeasonCycleTicks.get(dimension) > SeasonTime.ZERO.getCycleDuration())
            {
                clientSeasonCycleTicks.put(dimension, 0);
            }
            
            SeasonTime calendar = new SeasonTime(clientSeasonCycleTicks.get(dimension));
            
            if (calendar.getSubSeason() != lastSeason)
            {
                Minecraft.getInstance().levelRenderer.allChanged();
                lastSeason = calendar.getSubSeason();
            }
        }
    }
    
    public static void sendSeasonUpdate(Level world)
    {
        if (!world.isClientSide)
        {
            SeasonSavedData savedData = getSeasonSavedData(world);
            PacketHandler.HANDLER.send(PacketDistributor.ALL.noArg(), new MessageSyncSeasonCycle(world.dimension(), savedData.seasonCycleTicks));
        }
    }
    
    public static SeasonSavedData getSeasonSavedData(Level w)
    {
        if (w.isClientSide() || !(w instanceof ServerLevel))
        {
            return null;
        }

        ServerLevel world = (ServerLevel)w;
        DimensionDataStorage saveDataManager = world.getChunkSource().getDataStorage();

        Supplier<SeasonSavedData> defaultSaveDataSupplier = () ->
        {
            SeasonSavedData savedData = new SeasonSavedData();

            int startingSeason = ServerConfig.startingSubSeason.get();

            if (startingSeason == 0)
            {
                savedData.seasonCycleTicks = (world.random.nextInt(12)) * SeasonTime.ZERO.getSubSeasonDuration();
            }

            if (startingSeason > 0)
            {
                savedData.seasonCycleTicks = (startingSeason - 1) * SeasonTime.ZERO.getSubSeasonDuration();
            }

            savedData.setDirty(); //Mark for saving
            return savedData;
        };

        return saveDataManager.computeIfAbsent(SeasonSavedData::load, defaultSaveDataSupplier, SeasonSavedData.DATA_IDENTIFIER);
    }
    
    //
    // Used to implement getSeasonState in the API
    //

    @Override
    public ISeasonState getServerSeasonState(Level world)
    {
        SeasonSavedData savedData = getSeasonSavedData(world);
        return new SeasonTime(savedData.seasonCycleTicks);
    }

    @Override
    public ISeasonState getClientSeasonState()
    {
        Integer i = clientSeasonCycleTicks.get(Minecraft.getInstance().level.dimension());
    	return new SeasonTime(i == null ? 0 : i);
    }

    @Override
    public boolean usesTropicalSeasons(Holder<Biome> biome)
    {
        return biome.is(ModTags.Biomes.TROPICAL_BIOMES);
    }
}
