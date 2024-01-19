package sereneseasons.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import glitchcore.event.server.RegisterCommandsEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TemplateMirrorArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import sereneseasons.core.SereneSeasons;

import java.util.function.BiConsumer;

public class SeasonCommands
{
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("season")
                        .requires(cs -> cs.hasPermission(2))
                        .then(CommandSetSeason.register())
                        .then(CommandGetSeason.register())
        );
    }

    public static void registerArguments(BiConsumer<ResourceLocation, ArgumentTypeInfo<?, ?>> func)
    {
        register(func, "season", SeasonArgument.class, SingletonArgumentInfo.contextFree(SeasonArgument::season));
    }

    private static ArgumentTypeInfo<?, ?> register(BiConsumer<ResourceLocation, ArgumentTypeInfo<?, ?>> func, String name, Class<?> clazz, ArgumentTypeInfo<?, ?> typeInfo)
    {
        func.accept(new ResourceLocation(SereneSeasons.MOD_ID, name), typeInfo);
        ArgumentTypeInfos.BY_CLASS.put(clazz, typeInfo);
        return typeInfo;
    }
}
