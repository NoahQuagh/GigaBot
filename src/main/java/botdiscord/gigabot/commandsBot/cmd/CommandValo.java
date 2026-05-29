package botdiscord.gigabot.commandsBot.cmd;

import botdiscord.gigabot.commandsBot.sousCmdValorant.CommandValoDeleteTrack;
import botdiscord.gigabot.commandsBot.sousCmdValorant.CommandValoRank;
import botdiscord.gigabot.commandsBot.sousCmdValorant.CommandValoStats;
import botdiscord.gigabot.commandsBot.sousCmdValorant.CommandValoTrack;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import botdiscord.gigabot.utils.commands.CommandExecutor;


import java.sql.SQLException;


public class CommandValo implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) throws SQLException {
        if(args[0].equalsIgnoreCase("-rank")){
            new CommandValoRank().run(ctx,command,args);
        }else if(args[0].equalsIgnoreCase("-stats")){
            new CommandValoStats().run(ctx,command,args);
        }else if(args[0].equalsIgnoreCase("-settracker")){
            new CommandValoTrack().run(ctx,command,args);
        }else if(args[0].equalsIgnoreCase("-deltracker")){
            new CommandValoDeleteTrack().run(ctx,command,args);
        }
    }
}
