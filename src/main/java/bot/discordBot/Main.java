package bot.discordBot;

import bot.discordBot.utils.ConfigManager;
import bot.discordBot.utils.commands.MessageManager;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.*;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.List;

import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bot.discordBot.commands.Premier.CommandPremierEvent.*;
import static bot.discordBot.utils.Procedure.ApiProcedure.ApiRiotRequete;
import static bot.discordBot.utils.Procedure.ApiProcedure.getRankTxtByInt;
import static bot.discordBot.utils.Procedure.ValoDisProcedure.*;
import static bot.discordBot.utils.Success.success.sendRankupMessage;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;
import static java.util.Arrays.stream;

public class Main {

    public static DiscordApi api;
    private static ConfigManager configManager;
    public static String version ="1.0.4";

    public static void main(String[] args) {
        entrerNew();

        configManager = new ConfigManager(new File(System.getProperty("user.dir"),"config.toml"));

        api = new DiscordApiBuilder()
                .setToken(configManager.getToml().getString("bot.token"))
                .addIntents(Intent.GUILD_MESSAGES, Intent.MESSAGE_CONTENT)
                .login().join();

        api.addMessageCreateListener(MessageManager::create);
        api.addSlashCommandCreateListener(MessageManager::handleSlash);
        api.addAutocompleteCreateListener(event -> {
            String focused = event.getAutocompleteInteraction()
                    .getFocusedOption()
                    .getStringValue()
                    .orElse("");

            List<SlashCommandOptionChoice> toutes = new ArrayList<>();
            for (int i = 0; i < nombreCompteEnregistrer(); i++) {
                toutes.add(SlashCommandOptionChoice.create(
                        proposerAutoCompleteValue(i),
                        proposerAutoCompleteValue(i)
                ));
            }

            List<SlashCommandOptionChoice> choices = toutes.stream()
                    .filter(c -> c.getName().toLowerCase().startsWith(focused.toLowerCase()))
                    .limit(25)
                    .collect(Collectors.toList());

            event.getAutocompleteInteraction().respondWithChoices(choices);
        });

        long serverId = Long.parseLong(configManager.getToml().getString("bot.test_server"));
        new SlashCommandBuilder()
                .setName("premier")
                .setDescription("Commandes relatives au mode Premier de Valorant")
                .addOption(SlashCommandOption.createWithOptions(
                        SlashCommandOptionType.SUB_COMMAND, "event", "Créer un événement de game Premier",
                        Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "jour", "Format jj", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "mois", "Format mm", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "année", "Format aaaa", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "heure", "Format hh", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "minute", "Format mm", true)
                        )
                ))
                .addOption(SlashCommandOption.createWithOptions(
                        SlashCommandOptionType.SUB_COMMAND, "créerTeam", "Créer sa team Premier",
                        Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "nom", "Nom de la team Premier", true)
                        )
                ))
                .addOption(SlashCommandOption.createWithOptions(
                        SlashCommandOptionType.SUB_COMMAND, "supTeam", "Supprimer sa team Premier",
                        Arrays.asList()
                ))
                .addOption(SlashCommandOption.createWithOptions(
                        SlashCommandOptionType.SUB_COMMAND, "inviteJoueur", "Inviter des joueurs dans sa team Premier",
                        Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.USER, "joueur1", "joueur à inviter", true),
                                SlashCommandOption.create(SlashCommandOptionType.USER, "joueur2", "Joueur à inviter", false),
                                SlashCommandOption.create(SlashCommandOptionType.USER, "joueur3", "Joueur à inviter", false),
                                SlashCommandOption.create(SlashCommandOptionType.USER, "joueur4", "Joueur à inviter", false),
                                SlashCommandOption.create(SlashCommandOptionType.USER, "joueur5", "Joueur à inviter", false),
                                SlashCommandOption.create(SlashCommandOptionType.USER, "joueur6", "Joueur à inviter", false)
                        )
                ))
                .addOption(SlashCommandOption.createWithOptions(
                        SlashCommandOptionType.SUB_COMMAND, "supJoueur", "Supprimer un joueur de votre team Premier",
                        Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.USER, "joueur", "joueur à supprimé", true)
                        )
                ))
                .addOption(SlashCommandOption.createWithOptions(
                        SlashCommandOptionType.SUB_COMMAND, "cancelEvent", "Annuler un événement de game Premier",
                        Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "jour", "Format jj", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "mois", "Format mm", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "année", "Format aaaa", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "heure", "Format hh", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "minute", "Format mm", true)
                        )
                ))
                //.createForServer(api.getServerById(serverId).get())
                .createGlobal(api)
                .join();
        new SlashCommandBuilder()
                .setName("valorant")
                .setDescription("Commandes relatives au jeu Valorant")
                .addOption(SlashCommandOption.createWithOptions(
                        SlashCommandOptionType.SUB_COMMAND, "rank", "Obtenir le rang d'un(e) joueur/joueuse",
                        Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("pseudotag")
                                        .setDescription("Pseudo#tag du joueur Valorant")
                                        .setRequired(true)
                                        .setAutocompletable(true)
                                        .build()
                        )
                ))
                .addOption(SlashCommandOption.createWithOptions(
                        SlashCommandOptionType.SUB_COMMAND, "stats", "Obtenir les stats d'un(e) joueur/joueuse",
                        Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("pseudotag")
                                        .setDescription("Pseudo#tag du joueur Valorant")
                                        .setRequired(true)
                                        .setAutocompletable(true)
                                        .build()
                        )
                ))
                .addOption(SlashCommandOption.createWithOptions(
                        SlashCommandOptionType.SUB_COMMAND, "setTracker", "Affiche le peak rank du joueur indiquer dans ce salon",
                        Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("pseudotag")
                                        .setDescription("Pseudo#tag du joueur Valorant")
                                        .setRequired(true)
                                        .setAutocompletable(true)
                                        .build()
                        )
                ))
                .addOption(SlashCommandOption.createWithOptions(
                        SlashCommandOptionType.SUB_COMMAND, "delTracker", "Arrête de suivre le peak rank du joueur indiquer dans ce salon",
                        Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("pseudotag")
                                        .setDescription("Pseudo#tag du joueur Valorant")
                                        .setRequired(true)
                                        .setAutocompletable(true)
                                        .build()
                        )
                ))
                .createGlobal(api)
                .join();
        new SlashCommandBuilder()
                .setName("bot")
                .setDescription("Info sur le au bot")
                .createGlobal(api)
                .join();
        new SlashCommandBuilder()
                .setName("log")
                .setDescription("Obtenir les logs du bot")
                .createForServer(api.getServerById(serverId).get())
                .join();
        new SlashCommandBuilder()
                .setName("help")
                .setDescription("Obtenir une aide sur le bot")
                .addOption(SlashCommandOption.create(
                        SlashCommandOptionType.STRING,
                        "option",
                        "all = liste toutes les commandes",
                        false
                ))
                .createGlobal(api)
                .join();
        new SlashCommandBuilder()
                .setName("man")
                .setDescription("Obtenir le manuel d'utilisation des commandes du bot")
                .addOption(SlashCommandOption.create(
                        SlashCommandOptionType.STRING,
                        "commande",
                        "Nom de la commande",
                        true
                ))
                .createGlobal(api)
                .join();
        new SlashCommandBuilder()
                .setName("nouveauté")
                .setDescription("Obtenir les nouveautés du bot")
                .addOption(SlashCommandOption.create(
                        SlashCommandOptionType.STRING,
                        "version",
                        "version souhaité du bot",
                        false
                ))
                .createGlobal(api)
                .join();

        writeLogFile("logs.txt","bot start");
        restoreReminders(api);
        startTracking(api);
    }

    public static void startTracking(DiscordApi api) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                ArrayList<TrackedPlayer> players = DataManager.loadTrackedPlayer();
                if (players == null || players.isEmpty()) return;

                for (TrackedPlayer player : players) {
                    try {
                        String[] pseudoRaw = player.getPseudoRaw().split("#");

                        Thread.sleep(2000);

                        HttpResponse<String> response = ApiRiotRequete("https://api.henrikdev.xyz/valorant/v2/mmr/eu/" + pseudoRaw[0] + "/" + pseudoRaw[1]);

                        if (response.statusCode() != 200) continue;

                        JSONObject json = new JSONObject(response.body());
                        if (!json.has("data")) continue;

                        JSONObject data = json.getJSONObject("data");
                        JSONObject current = data.getJSONObject("current_data");

                        int currentTier = current.getInt("currenttier");
                        String rankName = getRankTxtByInt(currentTier);
                        String rankImage = current.getJSONObject("images").getString("large");

                        if (currentTier > player.getPeakTier()) {
                            sendRankupMessage(api, player, rankName, rankImage);
                            System.out.println("pass");
                            player.setPeakTier(currentTier);
                            DataManager.saveTrackedPlayer(players);

                            writeLogFile("logs.txt", "Rankup détecté pour " + player.getPseudoRaw() + " -> " + rankName);
                        }
                    } catch (Exception e) {
                        writeLogFile("logs.txt", "Erreur tracking joueur " + player.getPseudoRaw() + " : " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                writeLogFile("logs.txt", "Erreur globale boucle tracking : " + e.getMessage());
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    private static void restoreReminders(DiscordApi api) {
        try {
            netoyageRappel();
            ArrayList<Rappel> rappels = DataManager.loadRappels();

            if (rappels.isEmpty()) return;

            LocalDateTime now = LocalDateTime.now();

            for (int i=0;i<rappels.size();i++) {
                String currentUser = rappels.get(i).getUserId();
                LocalDateTime eventTime = rappels.get(i).getDate();

                api.getUserById(currentUser).thenAccept(user -> {
                    LocalDateTime reminderTime = eventTime.minusMinutes(30);

                    if (reminderTime.isAfter(now)) {
                        scheduleReminder(user, eventTime);
                    } else if (eventTime.isAfter(now)) {
                        user.sendMessage(new EmbedBuilder()
                                .setTitle("⏰ Rappel :")
                                .setDescription("Ton match Premier commence dans moins de 30 minutes !")
                                .setColor(Color.CYAN));
                    }
                });
            }
            writeLogFile("logs.txt","reminders performed");
        } catch (Exception e) {
            writeLogFile("logs.txt","Error restoring reminders :"+ e.getMessage());
        }
    }

    private static void entrerNew(){
/*
        ArrayList<Nouveaute> n1 = new ArrayList<>();
        n1.add(new Nouveaute("Nouvelle commande : /premier cancelEvent","La commande identifie l'équipe du capitaine et supprime le rappel prévu à la date indiquée."));
        n1.add(new Nouveaute("Nouvelle command : /premier supJoueur","Permet au capitaine de supprimé de son équipe le joueur mentionné dans la commande."));
        n1.add(new Nouveaute("Nouvelle command : /premier setTracker","Un système proactif qui surveille les performances des joueurs et annonce les nouveaux records (Peak Rank) dans le salon sur lequel la commande fut envoyée."));
        n1.add(new Nouveaute("Nouvelle command : /premier delTracker","Suppime le tracking du Peak Rank du joueur indiqué."));


        ArrayList<Bug> b1 = new ArrayList<>();


        b1.add(new Bug("titre","probleme","solution"));




        ArrayList<StrucNew> paquet = DataManager.loadNew();
        paquet.add(new StrucNew(version,n1,b1));

        DataManager.saveNew(paquet);

*/
    }
}
