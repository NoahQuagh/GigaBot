package bot.discordBot.commands;

import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.CommandExecutor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CommandEdtDev implements CommandExecutor {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String URL_ADE = "https://edtapi.antoninhuaut.fr/v3/univs/1/groups/1/1185/events";

    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        ctx.defer();

        fetchTodayEvents().thenAccept(events -> {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("📅 Emploi du temps du noaaaah_493 - Aujourd'hui")
                    .addField("","")
                    .setColor(Color.CYAN);

            if (events == null || events.isEmpty()) {
                embed.setDescription("Aucun cours prévu pour aujourd'hui.");
            } else {
                for (String line : events) {
                    embed.addField(line,"");
                }
            }

            ctx.replyDeferred(embed);
        });
    }

    public CompletableFuture<java.util.List<String>> fetchTodayEvents() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_ADE))
                .header("Accept", "application/json")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(jsonString -> {
                    JSONArray allEvents = new JSONArray(jsonString);
                    java.util.List<String> todayEvents = new java.util.ArrayList<>();
                    LocalDate today = LocalDate.now();

                    ZoneId parisZone = ZoneId.of("Europe/Paris");

                    for (int i = 0; i < allEvents.length(); i++) {
                        JSONObject event = allEvents.getJSONObject(i);

                        // 2. Parser la date UTC et la convertir immédiatement à l'heure de Paris
                        ZonedDateTime startUTC = ZonedDateTime.parse(event.getString("start"));
                        ZonedDateTime startParis = startUTC.withZoneSameInstant(parisZone);

                        // 3. Utiliser startParis pour la comparaison et l'affichage
                        if (startParis.toLocalDate().isEqual(LocalDate.now(parisZone))) {
                            String info = String.format("[%s] %s - %s",
                                    startParis.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                    event.getString("title"),
                                    event.getString("location"));
                            todayEvents.add(info);
                        }
                    }
                    return todayEvents;
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    public HashMap<Integer,String> variation = new HashMap<>();

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public HashMap<Integer, String> getVariation() {
        return null;
    }
}
