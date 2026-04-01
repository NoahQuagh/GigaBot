package bot.discordBot.commands.Valo;

import bot.discordBot.Main;
import bot.discordBot.commands.CommandValo;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandValoStats extends CommandValo {
    public final static String syntaxe;
    public final static String description;

    static {
        syntaxe = "!valo -stats <nom_du_joueur>#<tag>";
        description = "Permet d'obtenir les statistiques d'un joueur Valorant.";
    }

    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {
        if (args.length != 2) {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ Code.SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Exemple syntaxe:","```!valo -stats bouffeur2pieds#6767```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }


        String pseudo = args[1];//pseudo
        String[] split = pseudo.split("#");

        String name = split[0];//nom
        String tag = split[1];//tag

        try {
            String url = "https://api.henrikdev.xyz/valorant/v1/stored-matches/eu/"+name+"/"+tag+"?mode=competitive";

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            String apiKey = Main.getConfigManager().getToml().getString("api.valorant_key");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", apiKey)
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                writeLogFile("logs.txt",name+" | Code : "+ Code.ERREUR_API+" : "+response.statusCode());
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("❌   Erreur :")
                        .addField("Joueur introuvable ou API indisponible.","```Code: " + response.statusCode()+"```" )
                        .setColor(Color.red);
                event.getChannel().sendMessage(embed);
                return;
            }
            JSONObject json = new JSONObject(response.body());

            if (!json.has("data") || json.isNull("data")) {
                writeLogFile("logs.txt",name+" | Code : "+ Code.AUCUNE_DONNEE_TROUVER);
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("❌   Erreur :")
                        .addField("Impossible de lire les données du joueur.","" )
                        .setColor(Color.red);
                event.getChannel().sendMessage(embed);
                return;
            }

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
                    .setTitle("📈   Stats de : "+name + "#" + tag)
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

            event.getChannel().sendMessage(embed);


        } catch (Exception e) {
            writeLogFile("logs.txt",name+" | Code : "+ Code.ERREUR_API);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible de joindre le serveur de Riot.","")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
            e.printStackTrace();
        }
    }
}
