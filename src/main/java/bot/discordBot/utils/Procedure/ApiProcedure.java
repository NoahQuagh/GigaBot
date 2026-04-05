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
}
