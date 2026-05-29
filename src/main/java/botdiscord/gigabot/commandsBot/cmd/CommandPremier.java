package botdiscord.gigabot.commandsBot.cmd;

import bot.discordBot.commandsBot.Premier.*;
import botdiscord.gigabot.commandsBot.sousCmdPremierValorant.*;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import botdiscord.gigabot.utils.commands.CommandExecutor;

public class CommandPremier implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if(args[0].equalsIgnoreCase("-event")) {
            new CommandPremierEvent().run(ctx, command, args);
        }else if(args[0].equalsIgnoreCase("-invitejoueur")) {
            new CommandPremierTeamInvite().run(ctx, command, args);
        }else if(args[0].equalsIgnoreCase("-créerteam")) {
            new CommandPremierAddTeam().run(ctx, command, args);
        }else if(args[0].equalsIgnoreCase("-supteam")) {
            new CommandPremierSuprimerTeam().run(ctx, command, args);
        }else if(args[0].equalsIgnoreCase("-supjoueur")) {
            new CommandPremierSuprimerJoueur().run(ctx, command, args);
        }else if(args[0].equalsIgnoreCase("-cancelevent")) {
            new CommandPremierCancelEvent().run(ctx, command, args);
        }else if(args[0].equalsIgnoreCase("-nouveaucapitaine")) {
            new CommandPremierNewCapitaine().run(ctx, command, args);
        }else if(args[0].equalsIgnoreCase("-stratégieagent")) {
            new CommandPremierNewCapitaine().run(ctx, command, args);
        }
    }
}
