package sereneseasons.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SeasonCommands
{
    @SubscribeEvent
    public static void onCommandsRegistered(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSource>literal("season")
                        .requires(cs -> cs.hasPermission(2))
                        .then(CommandSetSeason.register())
                        .then(CommandGetSeason.register())
        );
    }
}
