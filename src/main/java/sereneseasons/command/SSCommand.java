package sereneseasons.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class SSCommand
{
    public SSCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            LiteralArgumentBuilder.<CommandSource>literal("ss")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(CommandSetSeason.register())
        );
    }
}
