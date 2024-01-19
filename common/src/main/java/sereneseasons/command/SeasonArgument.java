/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import sereneseasons.api.season.Season;

public class SeasonArgument extends StringRepresentableArgument<Season.SubSeason>
{
    private SeasonArgument() {
        super(Season.SubSeason.CODEC, Season.SubSeason::values);
    }

    public static StringRepresentableArgument<Season.SubSeason> season()
    {
        return new SeasonArgument();
    }

    public static Season.SubSeason getSeason(CommandContext<CommandSourceStack> context, String s) {
        return context.getArgument(s, Season.SubSeason.class);
    }
}

