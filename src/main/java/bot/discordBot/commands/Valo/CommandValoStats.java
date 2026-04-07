package bot.discordBot.commands.Valo;

import bot.discordBot.Main;
import bot.discordBot.commands.CommandValo;
import bot.discordBot.utils.Exception.ApiException;
import bot.discordBot.utils.Exception.JoueurException;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.CompteValoDiscord;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;

import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.Procedure.ApiProcedure.ApiRiotRequete;
import static bot.discordBot.utils.Procedure.ValoDisProcedure.pseudoValoExist;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandValoStats extends CommandValo {
    public final static String syntaxe;
    public final static String description;

    static {
        syntaxe = "/valo stats <nom_du_joueur>#<tag>";
        description = "Permet d'obtenir les statistiques d'un joueur Valorant.";
    }

    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try {
            String pseudoRaw = ctx.isSlash()
                    ? ctx.getOptionString("pseudotag").orElseThrow(() -> new SyntaxeException(ctx, "/valo rank <pseudo#tag>"))
                    : (args.length >= 2 ? args[1] : null);

            if (pseudoRaw == null) throw new SyntaxeException(ctx, "/valo stats <pseudo#tag>");

            String[] tmp= pseudoRaw.split("#");
            String pseudo=tmp[0];
            String tag = tmp[1];

            String url = "https://api.henrikdev.xyz/valorant/v1/stored-matches/eu/" + pseudo + "/" + tag + "?mode=competitive";
            HttpResponse<String> response = ApiRiotRequete(ctx, url);

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
                    .addField("", "")
                    .addField("⚔️   KDA Moyen", String.format("%.2f", kda))
                    .addField("", "")
                    .addField("🎯   Headshot %", String.format("%.1f%%", hsPercentage))
                    .addField("", "")
                    .addField("💀   Total Kills", String.valueOf(totalKills))
                    .addField("", "")
                    .addInlineField("🏆   Winrate", String.format("%.1f%%", winRate))
                    .setFooter("Basé sur les " + totalMatches + " derniers matchs")
                    .setColor(Color.GREEN);

            ctx.replyDeferred(embed);

            if(!(pseudoValoExist(pseudoRaw))){
                ArrayList<CompteValoDiscord> compte = DataManager.loadValoDis();
                if(compte.isEmpty()){
                    compte = new ArrayList<>();
                }
                compte.add(new CompteValoDiscord("","",pseudoRaw));
                DataManager.saveValoDis(compte);
            }

        }catch (ApiException | JoueurException e){
                writeLogFile("logs.txt", ctx.getAuthorName() + " | Code : " + Code.AUCUNE_DONNEE_TROUVER);
        }catch (SyntaxeException e){
            writeLogFile("logs.txt",ctx.getAuthorName()+" | Code : "+ Code.SYNTAXE_INCORRECTE);
        } catch (Exception e) {
            writeLogFile("logs.txt", ctx.getAuthorName()+" | Code : "+ Code.ERREUR_API+" >> "+e);
            ExceptionDefault(ctx,"Impossible de joindre le serveur de Riot");
        }
    }
}
