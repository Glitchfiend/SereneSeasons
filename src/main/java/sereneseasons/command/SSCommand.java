package sereneseasons.command;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.Season;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

import java.util.List;

public class SSCommand extends CommandBase
{
    @Override
    public String getName()
    {
        return "sereneseasons";
    }

    @Override
    public List getAliases()
    {
        return Lists.newArrayList("ss");
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.sereneseasons.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.sereneseasons.usage");
        }
        else if ("setseason".equals(args[0]))
        {
            setSeason(sender, args);
        }
    }

    private void setSeason(ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        Season.SubSeason newSeason = null;

        for (Season.SubSeason season : Season.SubSeason.values())
        {
            if (season.toString().toLowerCase().equals(args[1].toLowerCase()))
            {
                newSeason = season;
                break;
            }
        }

        if (newSeason != null)
        {
            SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(player.world);
            seasonData.seasonCycleTicks = SeasonTime.ZERO.getSubSeasonDuration() * newSeason.ordinal();
            seasonData.markDirty();
            SeasonHandler.sendSeasonUpdate(player.world);
            sender.sendMessage(new TextComponentTranslation("commands.sereneseasons.setseason.success", args[1]));
        }
        else
        {
            sender.sendMessage(new TextComponentTranslation("commands.sereneseasons.setseason.fail", args[1]));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "setseason");
        }

        return null;
    }
}
