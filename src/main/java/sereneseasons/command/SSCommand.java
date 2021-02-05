package sereneseasons.command;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.ArrayUtils;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

import java.util.Arrays;
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
        } else if ("getseason".equals(args[0])){
            getSeason(sender, args);
        } else {
            sender.sendMessage(new TextComponentTranslation("commands.sereneseasons.usage"));
        }
    }


    private void getSeason(ICommandSender sender, String[] args) throws  CommandException{
        EntityPlayer player = getCommandSenderAsPlayer(sender);

        SeasonSavedData data = SeasonHandler.getSeasonSavedData(player.world);

        //Current "time"
        int seasonCycleTicks = data.seasonCycleTicks;
        SeasonTime time = new SeasonTime(seasonCycleTicks);
        Season.SubSeason season = time.getSubSeason();

        int subSeasonDuration = time.getSubSeasonDuration();

        //One second contains 20 ticks (if the game runs smoothly, lags kick that out of the window)
        final int ticksPerSecond = 20;

        int index = ArrayUtils.indexOf(SubSeason.VALUES, season);

        if(index == 11){
            index = -1;
        }

        int ticksTillNext = subSeasonDuration * (index + 1) - seasonCycleTicks;

        int days = ticksTillNext / ticksPerSecond / 60 / 60 / 24;
        int hours = (ticksTillNext - (days * ticksPerSecond * 60 * 60 * 24)) / ticksPerSecond / 60 / 60;


        sender.sendMessage(new TextComponentTranslation("commands.sereneseasons.getseason", season, days, hours));
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
        else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, Arrays.stream(SubSeason.values()).map(e -> e.toString().toLowerCase()).toArray(String[]::new));
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}
