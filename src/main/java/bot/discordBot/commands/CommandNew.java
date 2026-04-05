package bot.discordBot.commands;

import bot.discordBot.utils.Exception.NoDataFoundException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.CommandExecutor;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Bug;
import bot.discordBot.utils.commands.datamanager.DataStructure.Nouveaute;
import bot.discordBot.utils.commands.datamanager.DataStructure.StrucNew;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static bot.discordBot.utils.Procedure.BotProcedure.*;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandNew implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        try{
            ArrayList<StrucNew> toutesLesNews = DataManager.loadNew();
            if(toutesLesNews.isEmpty()){
                ctx.reply("📢 Aucune nouveauté enregistrée.");
                return;
            }

            if(ctx.getOptionStringDirect("version").orElse("").isEmpty()){
                String version = DerniereVersionBot();
                if(cetteVersionDuBotExiste(version)){
                    StrucNew currentVersion=rechercherInfoVersionBot(version);
                    if(!(currentVersion ==null)){
                        afficherNouveate(ctx,currentVersion);
                    }
                }else throw new NoDataFoundException(ctx,"Les infos de la dernière version du bot ne sont pas encore remplie");

            }else{
                String version = ctx.getOptionStringDirect("version").orElse("").toLowerCase();
                if(cetteVersionDuBotExiste(version)){
                    StrucNew currentVersion=rechercherInfoVersionBot(version);
                    if(!(currentVersion ==null)){
                        afficherNouveate(ctx,currentVersion);
                    }
                }else throw new NoDataFoundException(ctx,"La version du bot précisé n'existe pas");
            }

        }catch (NoDataFoundException e){
            writeLogFile("logs.txt", ctx.getAuthorName()+" | Code : "+ Code.AUCUNE_DONNEE_TROUVER);
        }
    }

    private void afficherNouveate(CommandContext ctx,StrucNew currentVersion){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("**Nouveautés :** v " + currentVersion.getVersion()) // Supposant que tu as getVersion()
                .setColor(Color.green);

        // 1. Les Nouveautés
        if(currentVersion.getNouveau() != null && !currentVersion.getNouveau().isEmpty()){
            embed.addField("\u200B", "\u200B"); // Espace vide propre
            for(Nouveaute n : currentVersion.getNouveau()){
                embed.addField("**" + n.getNomNouveaute() + "**", "- " + n.getDescription());
            }
        }

        // 2. Les Bugs
        if(currentVersion.getBug() != null && !currentVersion.getBug().isEmpty()){
            embed.addField("\u200B", "\u200B");
            embed.addField("🪲 **Bug et Fixes :**", "\u200B");
            for(Bug b : currentVersion.getBug()){
                embed.addField(b.getNomBug() + " : ",
                        " - " + b.getDescription() + "\n" +
                                " __- Résolution :__ " + b.getResolution());
            }
        }

        ctx.reply(embed);
    }

    @Override
    public String getName() {
        return "nouveauté";
    }

    @Override
    public String getDescription() {
        return "Affiche toutes les nouveautés de la nouvelle version du bot";
    }

    @Override
    public String getUsage() {
        return "/nouveauté <version>";
    }

    @Override
    public HashMap<Integer, String> getVariation() {
        return null;
    }
}
