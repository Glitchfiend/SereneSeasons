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
import net.minecraftforge.server.command.EnumArgument;
import sereneseasons.api.season.Season;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

public class CommandSetSeason
{
    static ArgumentBuilder<CommandSourceStack, ?> register()
    {
        return Commands.literal("set")
            .then(Commands.argument("season", EnumArgument.enumArgument(Season.SubSeason.class))
            .executes(ctx -> {
                Level world = ctx.getSource().getLevel();
                return setSeason(ctx.getSource(), world, ctx.getArgument("season", Season.SubSeason.class));
            }));
    }

    private static int setSeason(CommandSourceStack cs, Level world, Season.SubSeason season) throws CommandRuntimeException
    {
        if (season != null)
        {
            SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
            seasonData.seasonCycleTicks = SeasonTime.ZERO.getSubSeasonDuration() * season.ordinal();
            seasonData.setDirty();
            SeasonHandler.sendSeasonUpdate(world);
            cs.sendSuccess(new TranslatableComponent("commands.sereneseasons.setseason.success", season.toString()), true);
        }
        else
        {
            cs.sendFailure(new TranslatableComponent("commands.sereneseasons.setseason.fail", season.toString()));
        }

        return 1;
    }
}
