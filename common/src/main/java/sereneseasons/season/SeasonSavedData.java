/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.season;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.saveddata.SavedData;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModConfig;

import java.util.List;

public class SeasonSavedData extends SavedData
{
    public static final Codec<SeasonSavedData> CODEC = RecordCodecBuilder.create(
        builder -> builder.group(
                Codec.INT.fieldOf("SeasonCycleTicks").orElse(0).forGetter(o -> o.seasonCycleTicks)
        ).apply(builder, SeasonSavedData::new)
    );

    public static final Identifier DATA_IDENTIFIER = Identifier.fromNamespaceAndPath(SereneSeasons.MOD_ID, "seasons");
    public static final int VERSION = 0;

    public int seasonCycleTicks;

    public SeasonSavedData()
    {
        this(calculateDefaultSeasonCycleTicks());
    }

    public SeasonSavedData(int seasonCycleTicks)
    {
        this.seasonCycleTicks = seasonCycleTicks;
    }

    private static int calculateDefaultSeasonCycleTicks() {
        int startingSeason = ModConfig.seasons.startingSubSeason;
        int seasonCycleTicks = 0;

        if (startingSeason > 0)
        {
            seasonCycleTicks = (startingSeason - 1) * SeasonTime.ZERO.getSubSeasonDuration();
        }

        return seasonCycleTicks;
    }
}
