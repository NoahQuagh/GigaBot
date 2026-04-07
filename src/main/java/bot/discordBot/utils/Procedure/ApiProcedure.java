package bot.discordBot.utils.Procedure;

import bot.discordBot.Main;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.CommandContext;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class ApiProcedure {
    public static HttpResponse<String> ApiRiotRequete(CommandContext ctx,String url){
        try{
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
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }catch (Exception e){
            writeLogFile("logs.txt", ctx.getAuthorName()+" | Code : "+ Code.ERREUR_API);
        }
        return null;
    }
    public static HttpResponse<String> ApiRiotRequete(String url){
        try{
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
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }catch (Exception e){
            writeLogFile("logs.txt", "TrackerValo | Code : "+ Code.ERREUR_API);
        }
        return null;
    }

    public static String getRankTxtByInt(int nb){
        return switch (nb) {
            case 0 -> "Unranked";
            case 3 -> "Iron 1";
            case 4 -> "Iron 2";
            case 5 -> "Iron 3";
            case 6 -> "Bronze 1";
            case 7 -> "Bronze 2";
            case 8 -> "Bronze 3";
            case 9 -> "Silver 1";
            case 10 -> "Silver 2";
            case 11 -> "Silver 3";
            case 12 -> "Gold 1";
            case 13 -> "Gold 2";
            case 14 -> "Gold 3";
            case 15 -> "Platinum 1";
            case 16 -> "Platinum 2";
            case 17 -> "Platinum 3";
            case 18 -> "Diamond 1";
            case 19 -> "Diamond 2";
            case 20 -> "Diamond 3";
            case 21 -> "Ascendant 1";
            case 22 -> "Ascendant 2";
            case 23 -> "Ascendant 3";
            case 24 -> "Immortal 1";
            case 25 -> "Immortal 2";
            case 26 -> "Immortal 3";
            case 27 -> "Radiant";
            default -> "Inconnu (" + nb + ")";
        };
    }
}
