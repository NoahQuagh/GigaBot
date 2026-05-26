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

    @Override
    public String getName() {
        return "premier";
    }

    @Override
    public String getDescription() {
        return "Commande relative au mode Premier de Valorant";
    }

    @Override
    public String getUsage() {
        return "/premier <option> <arguments>";
    }

    @Override
    public HashMap<Integer, String> getVariation() {
        variation.put(0,"Créer sa team Premier_/premier créerTeam <nomTeam>");
        variation.put(1,"Supprimer sa team Premier_/premier supTeam");
        variation.put(2,"Inviter des joueurs dans sa team Premier_/premier inviteJoueur <joueur1> <joueur2> ... <joueur7>");
        variation.put(3,"Créer un événement pour la team Premier_/premier event <jour format jj> <mois format mm> <année format aaaa> <heure> <minutes>");
        variation.put(4,"Supprimer un joueurs de sa team Premier_/premier supJoueur <joueur>");
        return variation;
    }
}
