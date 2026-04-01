package bot.discordBot.commands;

import bot.discordBot.utils.commands.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.HashMap;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandMan implements CommandExecutor {
    @Override
    public String getName() {
        return "man";
    }

    @Override
    public String getDescription() {
        return "Manuelle utilisateur des commandes du bot";
    }

    @Override
    public String getUsage() {
        return "!man <commande>";
    }

    public HashMap<Integer,String> variation = new HashMap<>();

    @Override
    public HashMap<Integer, String> getVariation() {
        variation.put(0,"ex: !man valo");
        return variation;
    }

    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {
        if (args.length != 1) {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ Code.SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("syntaxe correcte:","```!man <commande>```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }
        String targetAlias = args[0].toLowerCase();

        MessageManager.getRegistry().getByAlias(targetAlias).ifPresentOrElse(cmd -> {
            CommandExecutor exec = cmd.getExecutor();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Manuel :   !" + exec.getName())
                    .setDescription("```         ,..........   ..........,\n" +
                            "     ,..,'          '.'          ',..,\n" +
                            "    ,' ,'            :            ', ',\n" +
                            "   ,' ,'    M  A  N  :  U  E  L    ', ',\n" +
                            "  ,' ,'              :              ', ',\n" +
                            " ,' ,'............., : ,.............', ',\n" +
                            ",'  '............   '.'   ............'  ',\n" +
                            " '''''''''''''''''';''';''''''''''''''''''\n" +
                            "                    '''\n```")
                    .addField("Description",exec.getDescription())
                    .addField("","")
                    .addField("🛠️ Usage", "```" + exec.getUsage() + "```")
                    .addField("", "")
                    .setColor(Color.green);
                    //.setFooter("Demandé par " + event.getMessageAuthor().getDisplayName());


            if(!exec.getVariation().isEmpty()){
                embed.addField("Variation disponible :", "");
                embed.addField("", "");
                for(int i = 0; i<exec.getVariation().size();i++){
                    String[] text = exec.getVariation().get(i).split("_");
                    embed.addField(text[0],"```"+text[1]+"```");
                }
            }

            event.getChannel().sendMessage(embed);
        }, () -> {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ Code.SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("La commande `" + targetAlias + "` est inconnue.", "")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
        });
    }
}
