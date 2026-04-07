package bot.discordBot.commands;

import bot.discordBot.commands.Valo.CommandValoDeleteTrack;
import bot.discordBot.commands.Valo.CommandValoRank;
import bot.discordBot.commands.Valo.CommandValoStats;
import bot.discordBot.commands.Valo.CommandValoTrack;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.CommandExecutor;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;


public class CommandValo implements CommandExecutor {
    @Override
    public String getName() {
        return "valorant";
    }

    @Override
    public String getDescription() {return "Commande relative a valorant";}

    @Override
    public String getUsage() {
        return "/valorant <option> <arguments>";
    }

    public HashMap<Integer,String> variation = new HashMap<>();

    @Override
    public HashMap<Integer, String> getVariation() {
        variation.put(0,"Obtenir le rang d'un(e) joueur/joueuse_/valo rank <pseudo#tag>");
        variation.put(1,"Obtenir les stats d'un(e) joueur/joueuse_/valo stats <pseudo#tag>");
        return variation;
    }


    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        try{
            if(args.length >0){
                if(args[0].equalsIgnoreCase("-rank")){
                    new CommandValoRank().run(ctx,command,args);
                }else if(args[0].equalsIgnoreCase("-stats")){
                    new CommandValoStats().run(ctx,command,args);
                }else if(args[0].equalsIgnoreCase("-setTracker")){
                    new CommandValoTrack().run(ctx,command,args);
                }else if(args[0].equalsIgnoreCase("-delTracker")){
                    new CommandValoDeleteTrack().run(ctx,command,args);
                }
            }else throw new SyntaxeException(ctx,"/"+getName()+" <--here");
        }catch (SyntaxeException e){
            writeLogFile("logs.txt",ctx.getAuthorName()+" | Code : "+Code.SYNTAXE_INCORRECTE);
        }
    }
}
