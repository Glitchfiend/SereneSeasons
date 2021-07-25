/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

public class CommandGetSeason
{
    static ArgumentBuilder<CommandSourceStack, ?> register()
    {
        return Commands.literal("get")
            .executes(ctx -> {
                Level world = ctx.getSource().getLevel();
                return getSeason(ctx.getSource(), world);
            });
    }

    private static int getSeason(CommandSourceStack cs, Level world) throws CommandRuntimeException
    {
        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
        SeasonTime time = new SeasonTime(seasonData.seasonCycleTicks);
        int subSeasonDuration = SyncedConfig.getIntValue(SeasonsOption.SUB_SEASON_DURATION);
        cs.sendSuccess(new TranslatableComponent("commands.sereneseasons.getseason.success", time.getSubSeason().toString(), time.getDay() % subSeasonDuration, subSeasonDuration, time.getSeasonCycleTicks() % time.getDayDuration(), time.getDayDuration()), true);

        return 1;
    }
}
