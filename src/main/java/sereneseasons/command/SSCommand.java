package sereneseasons.command;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import sereneseasons.api.season.Season;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

public class SSCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "sereneseasons";
    }

    @Override
    public List getCommandAliases()
    {
        return Lists.newArrayList("ss");
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.sereneseasons.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    String[] getSeasons()
    {
        return new String[]
        { "early_spring", "mid_spring", "late_spring", "early_summer", "mid_summer", "late_summer", "early_autum", "mid_autumn", "late_autumn", "early_winter", "mid_winter", "late_winter" };
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.sereneseasons.usage");
        }
        else if ("setseason".equals(args[0]))
        {
            if (args.length < 2)
            {
                sender.addChatMessage(new ChatComponentText("Available seasons:"));
                sender.addChatMessage(new ChatComponentText(String.join(" ", getSeasons())));
                return;
            }
            setSeason(sender, args);
        }
    }

    private void setSeason(ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        Season.SubSeason newSeason = null;

        for (Season.SubSeason season : Season.SubSeason.VALUES)
        {
            if (season.toString().toLowerCase().equals(args[1].toLowerCase()))
            {
                newSeason = season;
                break;
            }
        }

        if (newSeason != null)
        {
            SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(player.worldObj);
            seasonData.seasonCycleTicks = SeasonTime.ZERO.getSubSeasonDuration() * newSeason.ordinal();
            seasonData.markDirty();
            SeasonHandler.sendSeasonUpdate(player.worldObj);
            sender.addChatMessage(new ChatComponentTranslation("commands.sereneseasons.setseason.success", args[1]));
        }
        else
        {
            sender.addChatMessage(new ChatComponentTranslation("commands.sereneseasons.setseason.fail", args[1]));
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "setseason");
        }

        if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, getSeasons());
        }

        return null;
    }
}
