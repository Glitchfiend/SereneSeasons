/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.handler.season;

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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import sereneseasons.api.SSGameRules;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonChangedEvent;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.ServerConfig;
import sereneseasons.handler.PacketHandler;
import sereneseasons.init.ModTags;
import sereneseasons.network.message.MessageSyncSeasonCycle;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

import java.util.HashMap;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SeasonHandler implements SeasonHelper.ISeasonDataProvider
{
    public static final HashMap<Level, Long> lastDayTimes = new HashMap<>();
    public static final HashMap<Level, Integer> updateTicks = new HashMap<>();

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event)
    {
        Level level = event.level;

        if (event.phase != TickEvent.Phase.START || level.isClientSide() || !ServerConfig.isDimensionWhitelisted(level.dimension()))
            return;

        long dayTime = level.getDayTime();
        long lastDayTime = lastDayTimes.getOrDefault(level, dayTime);
        lastDayTimes.put(level, dayTime);

        // Only tick seasons if the game rule is enabled
        if (!level.getGameRules().getBoolean(SSGameRules.RULE_DOSEASONCYCLE))
            return;

        if (!ServerConfig.progressSeasonWhileOffline.get())
        {
            MinecraftServer server = level.getServer();
            if (server != null && server.getPlayerList().getPlayerCount() == 0)
                return;
        }

        long difference = dayTime - lastDayTime;
        if (difference == 0)
            return;

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
    
    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event)
    {
        if (!(event.getEntity() instanceof ServerPlayer))
            return;

        ServerPlayer player = (ServerPlayer)event.getEntity();
        Level level = player.level();

        SeasonSavedData savedData = getSeasonSavedData(level);
        PacketHandler.HANDLER.send(new MessageSyncSeasonCycle(level.dimension(), savedData.seasonCycleTicks), PacketDistributor.PLAYER.with(player));
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
            MinecraftForge.EVENT_BUS.post(new SeasonChangedEvent.Standard(level, prevSeason, newSeason));

        // Fire an event on tropical season changes
        if (!prevTropicalSeason.equals(newTropicalSeason))
            MinecraftForge.EVENT_BUS.post(new SeasonChangedEvent.Tropical(level, prevTropicalSeason, newTropicalSeason));

        // Send the update packet
        PacketHandler.HANDLER.send(new MessageSyncSeasonCycle(level.dimension(), savedData.seasonCycleTicks), PacketDistributor.ALL.noArg());
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
