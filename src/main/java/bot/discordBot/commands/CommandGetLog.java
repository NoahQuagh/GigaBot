package bot.discordBot.commands;

import bot.discordBot.utils.Exception.CapitaineException;
import bot.discordBot.utils.Exception.NoDataFoundException;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.CommandExecutor;
import jdk.jfr.BooleanFlag;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.io.File;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandGetLog implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try{
            if (!ctx.isOwner()) throw new CapitaineException(ctx,"Cette commande est réservée à l'administrateur");

            File logFile = new File("logs.txt");

            if (logFile.exists()) {
                if (ctx.isSlash()) {
                    ctx.getSlashEvent()
                            .getSlashCommandInteraction()
                            .createFollowupMessageBuilder()
                            .addAttachment(logFile)
                            .send()
                            .join();
                } else {
                    new MessageBuilder()
                            .addAttachment(logFile)
                            .send(ctx.getChannel().get());
                }
            } else throw new NoDataFoundException(ctx);
        }catch (CapitaineException e){
            writeLogFile("logs.txt", ctx.getAuthorName()+" | Code : "+ Code.ACCEE_REFUSE);
        }catch (NoDataFoundException e){
            writeLogFile("logs.txt",ctx.getAuthorName()+" | Code : "+ Code.AUCUNE_DONNEE_TROUVER);
        }
    }

    @Override
    public String getName() {
        return "log";
    }

    @Override
    public String getDescription() {
        return "Obtenir les logs du bot  **(admin uniquement)**";
    }

    @Override
    public String getUsage() {
        return "/log";
    }

    public HashMap<Integer, String> variation = new HashMap<>();

    @Override
    public HashMap<Integer, String> getVariation() {
        return variation;
    }
}
