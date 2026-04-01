package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierRank extends CommandPremier {
    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {
        if (args.length != 2) {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ Code.SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Exemple syntaxe:","```!premier -rank GAYTEAM#6767```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }
        try {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ Code.COMMANDE_FERME);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Commande Indisponible", "```Code: "+ Code.COMMANDE_FERME+"```")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
            return;
            /*
            String pseudo = args[1];//pseudo
            String[] split = pseudo.split("#");

            String teamName = split[0];//nom
            String teamTag = split[1];//tag

            //String url = "https://api.henrikdev.xyz/valorant/v1/premier/" + teamName + "/" + teamTag;
            String url = "https://api.henrikdev.xyz/valorant/v1/premier/ningen/6967";

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
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("❌   Erreur :")
                        .addField("Joueur introuvable ou API indisponible.", "```Code: " + response.statusCode() + "```")
                        .setColor(Color.red);
                event.getChannel().sendMessage(embed);
                return;
            }

            JSONObject json = new JSONObject(response.body());

            if (!json.has("data") || json.isNull("data")) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("❌   Erreur :")
                        .addField("Impossible de lire les données du joueur.", "")
                        .setColor(Color.red);
                event.getChannel().sendMessage(embed);
                return;
            }

            JSONObject data = json.getJSONObject("data");
            JSONObject placement = data.getJSONObject("placement");
            JSONObject assets = data.getJSONObject("customization");
            String logoUrl = assets.getString("image");

            String text = "Division : "+ getDivisionName(placement.getInt("division"));
            String point = "Points: " + placement.getInt("points");

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Classement Team Premier : " + data.getString("name") + "#" + data.getString("tag"))
                    .setThumbnail(logoUrl)
                    .addField(text,"")
                    .addField(point,"")
                    .setColor(Color.GREEN);
            event.getChannel().sendMessage(embed);
        */
        }catch (ArrayIndexOutOfBoundsException e){
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Exemple syntaxe:","```!valo -rank GAYTEAM#6767```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        } catch (Exception e) {
            System.out.println(e);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible de joindre le serveur de Riot.","")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
        }
    }

    private String getDivisionName(int divisionId) {
        if (divisionId >= 1 && divisionId <= 5) return "Open " + divisionId;
        if (divisionId == 6) return "Intermediate";
        if (divisionId == 7) return "Advanced";
        if (divisionId == 8) return "Expert";
        if (divisionId == 9) return "Elite"; // Ton cas actuel !
        if (divisionId == 10) return "Contender";
        return "Inconnue (" + divisionId + ")";
    }
}
