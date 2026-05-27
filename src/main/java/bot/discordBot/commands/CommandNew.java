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
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static bot.discordBot.utils.Procedure.BotProcedure.*;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandNew implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try{
            ArrayList<StrucNew> toutesLesNews = DataManager.loadNew();
            if(toutesLesNews.isEmpty()){
                ctx.getEvent().getHook().sendMessage("📢 Aucune nouveauté enregistrée.").queue();
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
                .setTitle("**NOUVEAUTÉS :** v " + currentVersion.getVersion()) // Supposant que tu as getVersion()
                .setColor(Color.green);

        // 1. Les Nouveautés
        if(currentVersion.getNouveau() != null && !currentVersion.getNouveau().isEmpty()){
            for(Nouveaute n : currentVersion.getNouveau()){
                embed.addField("**" + n.getNomNouveaute() + "**", "- " + n.getDescription(),false);
            }
        }

        // 2. Les Bugs
        if(currentVersion.getBug() != null && !currentVersion.getBug().isEmpty()){
            embed.addField("\u200B", "\u200B",false);
            embed.addField("🪲 **Bug et Fixes :**", "\u200B",false);
            for(Bug b : currentVersion.getBug()){
                embed.addField(b.getNomBug() + " : ",
                        " - " + b.getDescription() + "\n" +
                                " __- Résolution :__ " + b.getResolution(),false);
            }
        }

        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
