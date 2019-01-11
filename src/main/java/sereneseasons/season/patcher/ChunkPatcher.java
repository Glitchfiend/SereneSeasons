package sereneseasons.season.patcher;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.journal.SeasonJournal;
import sereneseasons.season.journal.WeatherJournalRecord;
import sereneseasons.world.chunk.SeasonChunkData;

/**
 * The actual patcher for chunks.
 */
public class ChunkPatcher {
	/**
	 * Fixed point threshold for probability. THR_PROB_MAX means probability is 100%.
	 */
    private static final int THR_PROB_MAX = 1000;
    
    /**
     * Amount of ticks to look back in the past at the journal
     * to avoid replaying whole journal when patching generated chunks.
     */
    private static final long RETROSPECTIVE_WINDOW_TICKS = 24000 * 9;
    
    public ChunkPatcher() {
    }
    
    /**
     * Patches the actual chunk by replaying the journal on it
     * depending on the passed time this chunk has been patched before, if ever. 
     * 
     * @param chunkData the actual chunk.
     */
    public void patchChunkTerrain(Chunk chunk, SeasonChunkData chunkData)
    {
        World world = chunk.getWorld();

        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
        SeasonJournal journal = seasonData.getJournal();

        long lastPatchedTime = chunkData.getLastPatchedTime();
        boolean bFastForward = false;
        long windowBorder = world.getTotalWorldTime() - RETROSPECTIVE_WINDOW_TICKS;
        if (lastPatchedTime < windowBorder)
        {
            // Old entries have no effect. Considering it by reseting chunk snow
            // states and patch from newer journal entries only
            lastPatchedTime = windowBorder;
            bFastForward = true;
        }
        int fromIdx = journal.getJournalIndexAfterTime(lastPatchedTime);

        // determine initial state
        boolean bWasRaining = journal.wasLastRaining(fromIdx);
        boolean bWasCold = journal.wasLastCold(fromIdx);

        long rainingTrackTicks = 0;
        long coldTrackTicks = 0;

        long intervalRainingTrackStart = lastPatchedTime;
        long intervalColdTrackStart = lastPatchedTime;

        // initialize in case of fast forward
        if (bFastForward)
        {
            if (bWasCold)
                executePatchCommand(4, 0, chunk);  // 4 = set all snow/frozen
            else
                executePatchCommand(5, 0, chunk);  // 5 = set all molten
        }

        // Replay latest journal entries
        if (fromIdx != -1)
        {
            int command = 0; // 0 = NOP

            // Apply events from journal
            List<WeatherJournalRecord> journalEntries = journal.getJournalEvents();
            for (int curEntry = fromIdx; curEntry < journalEntries.size(); curEntry++)
            {
                WeatherJournalRecord wevt = journalEntries.get(curEntry);

                rainingTrackTicks = wevt.getTimeStamp() - intervalRainingTrackStart;
                coldTrackTicks = wevt.getTimeStamp() - intervalColdTrackStart;

                switch (wevt.getEventType())
                {
                    case EVENT_START_RAINING:
                        if (!bWasRaining)
                        {
                            intervalRainingTrackStart = wevt.getTimeStamp();
                            command = 0;  // 0 = NOP
                            bWasRaining = true;
                        }
                        break;
                    case EVENT_STOP_RAINING:
                        if (bWasRaining)
                        {
                            intervalRainingTrackStart = wevt.getTimeStamp();
                            if (bWasCold)
                                command = 2; // 2 = simulate chunk snow (requires duration parameter in ticks)
                            else
                                command = 0; // 0 = NOP
                            bWasRaining = false;
                        }
                        break;
                    case EVENT_TO_COLD_SEASON:
                        if (!bWasCold)
                        {
                            intervalColdTrackStart = wevt.getTimeStamp();
                            command = 3; // 3 = simulate melting (requires duration parameter in ticks)
                            bWasCold = true;
                        }
                        break;
                    case EVENT_TO_WARM_SEASON:
                        if (bWasCold)
                        {
                            intervalColdTrackStart = wevt.getTimeStamp();
                            if (bWasRaining)
                                command = 2; // 2 = simulate chunk snow (requires duration parameter in ticks).
                            else
                                command = 1; // 1 = simulate freeze only (requires duration parameter in ticks).
                            bWasCold = false;
                        }
                        break;
                    default:
                        // Do nothing
                        command = 0;  // 0 = NOP
                }

                executePatchCommand(command, coldTrackTicks, rainingTrackTicks, chunk);
            }
        }

        // Post update for running events
        rainingTrackTicks = world.getTotalWorldTime() - intervalRainingTrackStart;
        coldTrackTicks = world.getTotalWorldTime() - intervalColdTrackStart;

        if (journal.wasLastRaining(-1) && journal.wasLastCold(-1))
        {
            executePatchCommand(2, coldTrackTicks, rainingTrackTicks, chunk);
        }
        else if (!journal.wasLastCold(-1))
        {
            executePatchCommand(3, coldTrackTicks, rainingTrackTicks, chunk);
        }
        else
        {
            executePatchCommand(1, coldTrackTicks, rainingTrackTicks, chunk);
        }

        chunkData.setPatchTimeTo(world.getTotalWorldTime()); // set patch time up-to-date
    }
    
    /**
     * Executes a patching action on a chunk.<br/>
     * Commands have following meaning:<br/>
     * <ul>
     *  <li>0 = NOP. Do nothing.</li>
     *  <li>1 = simulate freeze only.</li>
     *  <li>2 = simulate chunk snow (requires duration parameter in ticks).</li>
     *  <li>3 = simulate melting (requires duration parameter in ticks).</li>
     *  <li>4 = set all snow/frozen.</li>
     *  <li>5 = set all molten.</li>
     * </ul>
     * 
     * @param command the action itself
     * @param coldTrackTicks amount of ticks the weather was cold (resulting to freezing)
     * @param rainingTrackTicks amount of ticks the weather was rainy/snowy (resulting to snowing if cold)
     * @param chunk the patched chunk
     */
    private void executePatchCommand(int command, long coldTrackTicks, long rainingTrackTicks, Chunk chunk)
    {
        if (command != 0)
        {
            int threshold = 0;
            if (command == 2)
            {
                long dur = rainingTrackTicks;
                if (dur > coldTrackTicks)
                    dur = coldTrackTicks;
                threshold = evalProbPerBlockUpdateForTicks((int) dur);
            }
            else if (command == 1 || command == 3)
                threshold = evalProbPerBlockUpdateForTicks((int) coldTrackTicks);
            executePatchCommand(command, threshold, chunk);
        }
    }
    
    /**
     * executes a patching action on a chunk.<br/>
     * Commands have following meaning:<br/>
     * <ul>
     *  <li>0 = NOP. Do nothing.</li>
     *  <li>1 = simulate freeze only.</li>
     *  <li>2 = simulate chunk snow (requires duration parameter in ticks).</li>
     *  <li>3 = simulate melting (requires duration parameter in ticks).</li>
     *  <li>4 = set all snow/frozen.</li>
     *  <li>5 = set all molten.</li>
     * </ul>
     * 
     * @param command the action itself
     * @param probability threshold in ticks, if needed
     * @param chunk the patched chunk
     */
    private void executePatchCommand(int command, int threshold, Chunk chunk)
    {
        // TODO: Maybe improve client notification on block changes at setBlockState calls if performance issues are occurring!

        ChunkPos chunkPos = chunk.getPos();
        World world = chunk.getWorld();

        if (command == 4 || command == 5)
        {
            threshold = THR_PROB_MAX;
        }

        MutableBlockPos pos = new MutableBlockPos();
        for (int iX = 0; iX < 16; iX++)
        {
            for (int iZ = 0; iZ < 16; iZ++)
            {
                int height = chunk.getHeightValue(iX, iZ);
                pos.setPos(chunkPos.getXStart() + iX, height, chunkPos.getZStart() + iZ);

                BlockPos below = pos.down();

                if ((command == 1 || command == 2 || command == 4))
                {
                    // Apply snow in dependence of last rain time and apply ice
                    // in dependence of last time the season changed to cold
                    // (where canSnowAtTempInSeason have returned false before).
                    if (world.rand.nextInt(THR_PROB_MAX) < threshold)
                    {
                        if (SeasonHelper.canBlockFreezeInSeason(world, below, false, false, SubSeason.EARLY_WINTER.getDefaultState(), false) )
                        {
                            // NOTE: Is a simplified freeze behavior
                            world.setBlockState(below, Blocks.ICE.getDefaultState(), 2);
                        }
                        else if (command != 1 && SeasonHelper.canSnowAtInSeason(world, pos, true, false, SubSeason.EARLY_WINTER.getDefaultState(), false))
                        {
                            world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState(), 2);
                        }
                    }
                    // TODO: Simulate crop death
                }
                else if (command == 3 || command == 5)
                {
                    // Remove snow and ice in dependence of last time the season
                    // changed to cold (where canSnowAtTempInSeason have
                    // returned true before).
                    if (world.rand.nextInt(THR_PROB_MAX) <= threshold * 2)
                    {
                        IBlockState blockState = world.getBlockState(pos);
                        if (blockState.getBlock() == Blocks.SNOW_LAYER)
                        {
                        	if (!SeasonHelper.canSnowAtInSeason(world, pos, true, true, SubSeason.EARLY_SPRING.getDefaultState(), false))
                        		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                        }
                        else
                        {
                            blockState = world.getBlockState(below);
                            if (blockState.getBlock() == Blocks.ICE)
                            {
                            	if (!SeasonHelper.canBlockFreezeInSeason(world, below, false, true, SubSeason.EARLY_SPRING.getDefaultState(), false)) {
                                    world.setBlockState(below, Blocks.WATER.getDefaultState(), 2);
                                    world.neighborChanged(below, Blocks.WATER, below);
                            	}
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Evals probability per block, an action is performed.
     * 
     * @param duringTicks duration in ticks. More means, higher probability.
     * @return the probability in the interval. (0, THR_PROB_MAX]
     */
    private int evalProbPerBlockUpdateForTicks(int duringTicks)
    {
        final double fieldHitProb = 1.0 / (16.0 * 16.0);
        final double snowUpdateProbInTick = 1.0 / 16.0;
        final double correctionFactor = 0.75;
        final double hitProb = correctionFactor * fieldHitProb * snowUpdateProbInTick;
        final double missProb = 1.0 - hitProb;
        double prob = hitProb * (1.0 - Math.pow(missProb, duringTicks + 1)) / (1.0 - missProb);

        return (int) ((double) THR_PROB_MAX * prob + 0.5);
    }

}
