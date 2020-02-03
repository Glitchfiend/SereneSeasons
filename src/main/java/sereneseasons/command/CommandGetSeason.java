/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.server.command.EnumArgument;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.Season;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

public class CommandGetSeason
{
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.literal("getseason")
            .executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                return getSeason(ctx.getSource(), player);
            });
    }

    private static int getSeason(CommandSource cs, ServerPlayerEntity player) throws CommandException
    {
        SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(player.level);
        SeasonTime time = new SeasonTime(seasonData.seasonCycleTicks);
        int subSeasonDuration = SyncedConfig.getIntValue(SeasonsOption.SUB_SEASON_DURATION);
        cs.sendSuccess(new TranslationTextComponent("commands.sereneseasons.getseason.success", time.getSubSeason().toString(), time.getDay() % subSeasonDuration, subSeasonDuration, time.getSeasonCycleTicks() % time.getDayDuration(), time.getDayDuration()), true);

        return 1;
    }
}
