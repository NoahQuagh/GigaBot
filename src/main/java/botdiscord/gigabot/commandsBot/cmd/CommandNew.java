package botdiscord.gigabot.commandsBot.cmd;

import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.enumDB.TypeChangelog;
import botdiscord.gigabot.utils.DB.log_DB;
import botdiscord.gigabot.utils.DB.version_bot_DB;
import botdiscord.gigabot.utils.exception.NoDataFoundException;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import botdiscord.gigabot.utils.commands.CommandExecutor;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;


public class CommandNew implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) throws SQLException {
        if (ctx.isSlash()) ctx.defer();
        version_bot_DB versionBotDb = new version_bot_DB();
        log_DB logs = new log_DB();
        try{
            if(ctx.getOptionStringDirect("version").orElse("").isEmpty()){
                String version = versionBotDb.getLastVersionBot();
                if(versionBotDb.VersionBotExiste(version)){
                    ArrayList<String> listeChangelogNouveaute = versionBotDb.getInfoVersionBot(version, TypeChangelog.nouveaute);
                    ArrayList<String> listeChangelogBug = versionBotDb.getInfoVersionBot(version,TypeChangelog.bug);
                    if(!(listeChangelogNouveaute ==null) && !(listeChangelogBug ==null)){
                        afficherNouveate(ctx,version,listeChangelogNouveaute,listeChangelogBug);
                    }
                }else throw new NoDataFoundException(ctx,"Les infos de la dernière version du bot ne sont pas remplie");

            }else{
                String version = ctx.getOptionStringDirect("version").orElse("").toLowerCase();
                if(versionBotDb.VersionBotExiste(version)){
                    ArrayList<String> listeChangelogNouveaute = versionBotDb.getInfoVersionBot(version, TypeChangelog.nouveaute);
                    ArrayList<String> listeChangelogBug = versionBotDb.getInfoVersionBot(version,TypeChangelog.bug);
                    if(!(listeChangelogNouveaute ==null) && !(listeChangelogBug ==null)){
                        afficherNouveate(ctx,version,listeChangelogNouveaute,listeChangelogBug);
                    }
                }else throw new NoDataFoundException(ctx,"La version du bot précisé n'existe pas");
            }

        }catch (NoDataFoundException e){
            logs.writeLog(LevelLog.ERR,CommandNew.class.getName(),"Aucune données trouvé pour cette version du bot : "+e);
        }
    }

    private void afficherNouveate(CommandContext ctx,String currentVersion,ArrayList<String> infoNouveates,ArrayList<String> infoBugs){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("**NOUVEAUTÉS :** v " + currentVersion)
                .setColor(Color.green);

        if(infoNouveates != null && !(infoNouveates.isEmpty())){
            for(String n : infoNouveates){
                String[] liste = n.split("&&");
                embed.addField("**" + liste[0] + "**", "- " + liste[1],false);
            }
        }

        // 2. Les Bugs
        if(infoBugs != null && !(infoBugs.isEmpty())){
            embed.addField("\u200B", "\u200B",false);
            embed.addField("🪲 **Bug et Fixes :**", "\u200B",false);
            for(String b : infoBugs){
                String[] liste = b.split("&&");
                embed.addField(liste[0] + " : ",
                        " - " + liste[1] + "\n" +
                                " __- Résolution :__ " + liste[2],false);
            }
        }

        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
