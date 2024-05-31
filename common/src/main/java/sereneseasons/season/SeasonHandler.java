/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.season;

import glitchcore.event.EventManager;
import glitchcore.event.TickEvent;
import glitchcore.event.player.PlayerEvent;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import sereneseasons.api.SSGameRules;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonChangedEvent;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModPackets;
import sereneseasons.init.ModTags;
import sereneseasons.network.SyncSeasonCyclePacket;

import java.util.HashMap;
import java.util.function.Supplier;

public class SeasonHandler implements SeasonHelper.ISeasonDataProvider
{
    public static final HashMap<Level, Long> lastDayTimes = new HashMap<>();
    public static final HashMap<Level, Integer> updateTicks = new HashMap<>();

    public static void onLevelTick(TickEvent.Level event)
    {
        Level level = event.getLevel();

        if (event.getPhase() != TickEvent.Phase.START || level.isClientSide() || !ModConfig.seasons.isDimensionWhitelisted(level.dimension()))
            return;

        long dayTime = level.getDayTime();
        long lastDayTime = lastDayTimes.getOrDefault(level, dayTime);
        lastDayTimes.put(level, dayTime);

        // Only tick seasons if the game rule is enabled
        if (!level.getGameRules().getBoolean(SSGameRules.RULE_DOSEASONCYCLE))
            return;

        if (!ModConfig.seasons.progressSeasonWhileOffline)
        {
            MinecraftServer server = level.getServer();
            if (server != null && server.getPlayerList().getPlayerCount() == 0)
                return;
        }

        long difference = dayTime - lastDayTime;
        if (difference == 0)
            return;

        if (difference < 0)
        {
            difference += 24000L;
        }

        SeasonSavedData savedData = getSeasonSavedData(level);
        savedData.seasonCycleTicks = Mth.positiveModulo(savedData.seasonCycleTicks + (int)difference, SeasonTime.ZERO.getCycleDuration());

        int ticks = updateTicks.getOrDefault(level, 0);
        if (ticks >= 20)
        {
            sendSeasonUpdate(level);
            ticks %= 20;
        }
        updateTicks.put(level, ticks + 1);
        savedData.setDirty();
    }

    public static void onJoinLevel(PlayerEvent.JoinLevel event)
    {
        if (!(event.getPlayer() instanceof ServerPlayer player))
            return;

        Level level = player.level();
        SeasonSavedData savedData = getSeasonSavedData(level);
        ModPackets.HANDLER.sendToPlayer(new SyncSeasonCyclePacket(level.dimension(), savedData.seasonCycleTicks), player);
    }

    public static final HashMap<ResourceKey<Level>, Integer> prevServerSeasonCycleTicks = new HashMap<>();

    public static void sendSeasonUpdate(Level level)
    {
        if (level.isClientSide())
            return;

        SeasonSavedData savedData = getSeasonSavedData(level);

        // NOTE: The previous tick time is not necessary the current tick time - 1. This is why we have to store it in a map.
        SeasonTime newTime = new SeasonTime(savedData.seasonCycleTicks);
        SeasonTime prevTime = new SeasonTime(prevServerSeasonCycleTicks.computeIfAbsent(level.dimension(), (key) -> newTime.getSeasonCycleTicks()));

        Season.SubSeason prevSeason = prevTime.getSubSeason();
        Season.TropicalSeason prevTropicalSeason = prevTime.getTropicalSeason();
        Season.SubSeason newSeason = newTime.getSubSeason();
        Season.TropicalSeason newTropicalSeason = newTime.getTropicalSeason();

        // Update the previous time
        prevServerSeasonCycleTicks.put(level.dimension(), newTime.getSeasonCycleTicks());

        // Fire an event on standard season changes
        if (!prevSeason.equals(newSeason))
            EventManager.fire(new SeasonChangedEvent.Standard(level, prevSeason, newSeason));

        // Fire an event on tropical season changes
        if (!prevTropicalSeason.equals(newTropicalSeason))
            EventManager.fire(new SeasonChangedEvent.Tropical(level, prevTropicalSeason, newTropicalSeason));

        // Send the update packet
        ModPackets.HANDLER.sendToAll(new SyncSeasonCyclePacket(level.dimension(), savedData.seasonCycleTicks), ((ServerLevel)level).getServer());
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

            int startingSeason = ModConfig.seasons.startingSubSeason;

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

        return saveDataManager.computeIfAbsent(new SavedData.Factory<>(defaultSaveDataSupplier, SeasonSavedData::load, DataFixTypes.LEVEL), SeasonSavedData.DATA_IDENTIFIER);
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
    public ISeasonState getClientSeasonState(Level level)
    {
        int time = level != null ? SeasonHandlerClient.clientSeasonCycleTicks.getOrDefault(level.dimension(), 0) : 0;
    	return new SeasonTime(time);
    }

    @Override
    public boolean usesTropicalSeasons(Holder<Biome> biome)
    {
        return biome.is(ModTags.Biomes.TROPICAL_BIOMES);
    }
}
