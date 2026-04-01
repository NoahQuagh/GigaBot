package bot.discordBot.commands.Valo;

import bot.discordBot.Main;
import bot.discordBot.commands.CommandValo;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandValoRank extends CommandValo {
    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {
        if (args.length != 2) {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ Code.SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Exemple syntaxe:","```!valo -rank bouffeur2pieds#6767```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }



        try {
            String pseudo = args[1];//pseudo
            String[] split = pseudo.split("#");

            String name = split[0];//nom
            String tag = split[1];//tag

            String url = "https://api.henrikdev.xyz/valorant/v1/mmr/eu/" + name + "/" + tag;

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
                        .addField("Joueur introuvable ou API indisponible.", "```Code: " + response.statusCode() + "```")
                        .setColor(Color.red);
                event.getChannel().sendMessage(embed);
                return;
            }
            JSONObject json = new JSONObject(response.body());

            if (!json.has("data") || json.isNull("data")) {
                writeLogFile("logs.txt",name+" | Code : "+ Code.AUCUNE_DONNEE_TROUVER);
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("❌   Erreur :")
                        .addField("Impossible de lire les données du joueur.", "")
                        .setColor(Color.red);
                event.getChannel().sendMessage(embed);
                return;
            }

            JSONObject data = json.getJSONObject("data");

            String rank = data.optString("currenttierpatched", "Inconnu");
            int mmr = data.optInt("ranking_in_tier", 0);

            String iconUrl = "";
            if (data.has("images")) {
                iconUrl = data.getJSONObject("images").optString("small", "");
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(name + "#" + tag)
                    .setTitle("Rank :")
                    .setDescription("## " + rank + " : " + mmr + "  RR")
                    .setColor(Color.GREEN);

            if (!iconUrl.isEmpty()) embed.setThumbnail(iconUrl);

            event.getChannel().sendMessage(embed);
        }catch (ArrayIndexOutOfBoundsException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Exemple syntaxe:","```!valo -rank bouffeur2pieds#6767```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        } catch (Exception e) {
            writeLogFile("logs.txt","Code : "+ Code.ERREUR_API);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible de joindre le serveur de Riot.","")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
        }
    }
}