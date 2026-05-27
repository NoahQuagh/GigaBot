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


import java.awt.*;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;


public class CommandValo implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        try{
            if(args.length >0){
                if(args[0].equalsIgnoreCase("-rank")){
                    new CommandValoRank().run(ctx,command,args);
                }else if(args[0].equalsIgnoreCase("-stats")){
                    new CommandValoStats().run(ctx,command,args);
                }else if(args[0].equalsIgnoreCase("-settracker")){
                    new CommandValoTrack().run(ctx,command,args);
                }else if(args[0].equalsIgnoreCase("-deltracker")){
                    new CommandValoDeleteTrack().run(ctx,command,args);
                }
            }else throw new SyntaxeException(ctx,"/valorant <--here");
        }catch (SyntaxeException e){
            writeLogFile("logs.txt",ctx.getAuthorName()+" | Code : "+Code.SYNTAXE_INCORRECTE);
        }
    }
}
