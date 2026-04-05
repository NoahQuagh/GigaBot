package bot.discordBot.commands;

import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;

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
        return "/man <commande>";
    }

    public HashMap<Integer,String> variation = new HashMap<>();

    @Override
    public HashMap<Integer, String> getVariation() {
        variation.put(0,"ex: /man valorant");
        return variation;
    }

    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        try{
            if (args.length != 1) throw new SyntaxeException(ctx,"/man <commande>");

            String targetAlias = ctx.getOptionStringDirect("commande").orElse("").toLowerCase();

            MessageManager.getRegistry().getByAlias(targetAlias).ifPresentOrElse(cmd -> {
                CommandExecutor exec = cmd.getExecutor();

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Manuel :   /" + exec.getName())
                        .setDescription("```         ,..........   ..........,\n" +
                                "     ,..,'          '.'          ',..,\n" +
                                "    ,' ,'            :            ', ',\n" +
                                "   ,' ,'    M  A  N  :  U  E  L    ', ',\n" +
                                "  ,' ,'              :              ', ',\n" +
                                " ,' ,'............., : ,.............', ',\n" +
                                ",'  '............   '.'   ............'  ',\n" +
                                " '''''''''''''''''';''';''''''''''''''''''\n" +
                                "                    '''\n```")
                        .addField("Description", exec.getDescription())
                        .addField("", "")
                        .addField("🛠️ Usage", "``" + exec.getUsage() + "``")
                        .addField("", "")
                        .setColor(Color.green);
                //.setFooter("Demandé par " + event.getMessageAuthor().getDisplayName());


                if (!exec.getVariation().isEmpty()) {
                    embed.addField("Variation disponible :", "");
                    embed.addField("", "");
                    for (int i = 0; i < exec.getVariation().size(); i++) {
                        String[] text = exec.getVariation().get(i).split("_");
                        embed.addField(text[0], "``" + text[1] + "``");
                    }
                }

                ctx.reply(embed);
            }, () -> {
                throw new SyntaxeException(ctx,"La commande '" + targetAlias + "' n'est pas reconnue par le Bot");
            });
        }catch (SyntaxeException e){
            writeLogFile("logs.txt", ctx.getAuthorName()+" | Code : "+ Code.SYNTAXE_INCORRECTE);
        }

    }
}
