package bot.discordBot.commands;

import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandExecutor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPing implements CommandExecutor {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Pings du GigaBot";
    }

    @Override
    public String getUsage() {
        return "!ping";
    }

    public HashMap<Integer,String> variation = new HashMap<>();

    @Override
    public HashMap<Integer, String> getVariation() {
        return variation;
    }

    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {


        if(args.length >=0) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Pinging...")
                    .setColor(Color.ORANGE);

            event.getChannel().sendMessage(builder).thenAccept(message -> {
                long unixBot = message.getCreationTimestamp().toEpochMilli();
                long unixUser = event.getMessage().getCreationTimestamp().toEpochMilli();
                long ping = unixBot - unixUser;

                builder.setColor(Color.GREEN)
                        .setDescription("Ping : " + ping + "ms")
                        .setTitle("Je suis actif !");

                message.edit(builder);

            });
        }else{
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ Code.SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Option Recommandé :","```!ping```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }
    }
}
