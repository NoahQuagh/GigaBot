package bot.discordBot.commands;

import bot.discordBot.commands.Premier.*;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.CommandExecutor;


import java.awt.*;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremier implements CommandExecutor {

    public HashMap<Integer,String> variation = new HashMap<>();

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
