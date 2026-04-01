package bot.discordBot.commands;

import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandExecutor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPaul implements CommandExecutor {
    @Override
    public String getName() {
        return "paul";
    }

    @Override
    public String getDescription() {
        return "Info sur paul";
    }

    @Override
    public String getUsage() {
        return "!paul";
    }

    public HashMap<Integer,String> variation = new HashMap<>();

    @Override
    public HashMap<Integer, String> getVariation() {
        return variation;
    }

    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {
        if(args.length >=0) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("**PAUL**")
                    .setDescription("Ce mec est homosexuel !");
            event.getChannel().sendMessage(embed);
        }else{
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ Code.SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Option Recommandé :","```!paul```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }
    }
}
