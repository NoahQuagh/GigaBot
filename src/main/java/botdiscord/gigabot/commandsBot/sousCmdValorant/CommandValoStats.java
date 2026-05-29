package botdiscord.gigabot.commandsBot.sousCmdValorant;

import botdiscord.gigabot.commandsBot.cmd.CommandValo;
import botdiscord.gigabot.utils.API.ValorantApi;
import botdiscord.gigabot.utils.DB.Valo_Dis_DB;
import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.log_DB;
import botdiscord.gigabot.utils.exception.ApiException;
import botdiscord.gigabot.utils.exception.JoueurException;
import botdiscord.gigabot.utils.exception.SyntaxeException;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.net.http.HttpResponse;
import java.sql.SQLException;

import static botdiscord.gigabot.utils.exception.DefaultException.ExceptionDefault;

public class CommandValoStats extends CommandValo {
    public final static String syntaxe;
    public final static String description;

    static {
        syntaxe = "/valo stats <nom_du_joueur>#<tag>";
        description = "Permet d'obtenir les statistiques d'un joueur Valorant.";
    }

    @Override
    public void run(CommandContext ctx, Command command, String[] args) throws SQLException {
        if (ctx.isSlash()) ctx.defer();
        ValorantApi valorantApi=new ValorantApi();
        Valo_Dis_DB valoDisDb=new Valo_Dis_DB();
        log_DB logs=new log_DB();
        try {
            String pseudoRaw = ctx.isSlash()
                    ? ctx.getOptionString("pseudotag").orElseThrow(() -> new SyntaxeException(ctx, "/valo rank <pseudo#tag>"))
                    : (args.length >= 2 ? args[1] : null);

            if (pseudoRaw == null) throw new SyntaxeException(ctx, "/valo stats <pseudo#tag>");

            String[] tmp= pseudoRaw.split("#");
            String pseudo=tmp[0];
            String tag = tmp[1];

            String url = "https://api.henrikdev.xyz/valorant/v1/stored-matches/eu/" + pseudo + "/" + tag + "?mode=competitive";
            HttpResponse<String> response = valorantApi.request(ctx,url);

            if (response == null) throw new ApiException(ctx,"L'appel à l'API Riot a échoué");
            if (response.statusCode() != 200) throw new ApiException(ctx,"Joueur introuvable : "+response.statusCode());

            JSONObject json = new JSONObject(response.body());

            if (!json.has("data") || json.isNull("data")) throw new JoueurException(ctx,"Impossible de lire les données du joueur");

            JSONArray matchesList = json.getJSONArray("data");

            int totalKills = 0;
            int totalDeaths = 0;
            int totalAssists = 0;
            int totalHeadshots = 0;
            int totalShots = 0;
            int totalWins = 0;
            int totalMatches = matchesList.length();

            for (int i = 0; i < matchesList.length(); i++) {
                JSONObject match = matchesList.getJSONObject(i);
                JSONObject stats = match.getJSONObject("stats");
                JSONObject shots = stats.getJSONObject("shots");
                JSONObject teams = match.getJSONObject("teams");

                String playerTeam = stats.getString("team");
                int scoreRed = teams.getInt("red");
                int scoreBlue = teams.getInt("blue");

                if (playerTeam.equalsIgnoreCase("Red") && scoreRed > scoreBlue) {
                    totalWins++;
                } else if (playerTeam.equalsIgnoreCase("Blue") && scoreBlue > scoreRed) {
                    totalWins++;
                }

                totalKills += stats.getInt("kills");
                totalDeaths += stats.getInt("deaths");
                totalAssists += stats.getInt("assists");

                // Pour le HS%, on additionne tous les types de tirs
                totalHeadshots += shots.getInt("head");
                totalShots += shots.getInt("head") + shots.getInt("body") + shots.getInt("leg");
            }

            double kda = (double) (totalKills + totalAssists) / Math.max(1, totalDeaths);
            double hsPercentage = (totalShots > 0) ? ((double) totalHeadshots / totalShots) * 100 : 0;
            double winRate = (totalMatches > 0) ? ((double) totalWins / totalMatches) * 100 : 0;

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("📈   Stats de : " + pseudo + "#" + tag)
                    .addField("", "",false)
                    .addField("⚔️   KDA Moyen", String.format("%.2f", kda),false)
                    .addField("", "",false)
                    .addField("🎯   Headshot %", String.format("%.1f%%", hsPercentage),false)
                    .addField("", "",false)
                    .addField("💀   Total Kills", String.valueOf(totalKills),false)
                    .addField("", "",false)
                    .addField("🏆   Winrate", String.format("%.1f%%", winRate),true)
                    .setFooter("Basé sur les " + totalMatches + " derniers matchs")
                    .setColor(Color.GREEN);

            ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();

            if(!(valorantApi.valorantPseudoExiste(pseudoRaw))){
                valoDisDb.setValoDis(pseudoRaw);
            }

        }catch (ApiException | JoueurException e){
            logs.writeLog(LevelLog.ERR, CommandValoStats.class.getName(),ctx.getAuthorName() + " aucune données trouver : "+e);
        }catch (SyntaxeException e){
            logs.writeLog(LevelLog.ERR, CommandValoStats.class.getName(),ctx.getAuthorName() + " syntax incorrecte : "+e);
        } catch (Exception e) {
            logs.writeLog(LevelLog.ERR, CommandValoStats.class.getName(),ctx.getAuthorName() + " erreur API : "+e);
            ExceptionDefault(ctx,"Impossible de joindre les serveurs de Riot");
        }
    }
}
