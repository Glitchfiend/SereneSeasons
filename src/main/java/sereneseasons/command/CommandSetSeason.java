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
import net.minecraft.world.World;
import net.minecraftforge.server.command.EnumArgument;
import sereneseasons.api.season.Season;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

public class CommandSetSeason
{
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.literal("set")
            .then(Commands.argument("season", EnumArgument.enumArgument(Season.SubSeason.class))
            .executes(ctx -> {
                World world = ctx.getSource().getLevel();
                return setSeason(ctx.getSource(), world, ctx.getArgument("season", Season.SubSeason.class));
            }));
    }

    private static int setSeason(CommandSource cs, World world, Season.SubSeason season) throws CommandException
    {
        if (season != null)
        {
            SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
            seasonData.seasonCycleTicks = SeasonTime.ZERO.getSubSeasonDuration() * season.ordinal();
            seasonData.setDirty();
            SeasonHandler.sendSeasonUpdate(world);
            cs.sendSuccess(new TranslationTextComponent("commands.sereneseasons.setseason.success", season.toString()), true);
        }
        else
        {
            cs.sendFailure(new TranslationTextComponent("commands.sereneseasons.setseason.fail", season.toString()));
        }

        return 1;
    }
}
