package bot.discordBot;

import bot.discordBot.System.RankScheduler;
import bot.discordBot.utils.ConfigManager;
import bot.discordBot.utils.commands.MessageManager;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Games;
import bot.discordBot.utils.commands.datamanager.DataStructure.Rappel;
import bot.discordBot.utils.commands.datamanager.DataStructure.TrackedPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static bot.discordBot.commands.Premier.CommandPremierEvent.netoyageRappel;
import static bot.discordBot.utils.Procedure.ApiProcedure.ApiRiotRequete;
import static bot.discordBot.utils.Procedure.ApiProcedure.getRankTxtByInt;
import static bot.discordBot.utils.Success.success.sendRankupMessage;
import static bot.discordBot.utils.commands.datamanager.DataManager.loadGamesList;
import static bot.discordBot.utils.commands.datamanager.DataManager.saveGamesList;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class Main {

    private static ConfigManager configManager;
    public static String version ="1.0.6";
    public static JDA jda;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * Point de démarrage du bot
     * @param args argument de démarrage
     * @throws InterruptedException erreur de connexion
     */
    public static void main(String[] args) throws InterruptedException {
        entrerNew();

        configManager = new ConfigManager(new File(System.getProperty("user.dir"), "config.toml"));
        String token = configManager.getToml().getString("bot.token");

        // Initialisation JDA
        jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageManager())
                .build();

        jda.awaitReady();

        registerCommands();

        writeLogFile("logs.txt", "bot start");

        ArrayList<Games> gamesList = loadGamesList();
        if (gamesList == null) gamesList = new ArrayList<>();

        for (Guild serve : jda.getGuilds()) {
            final String serverId = serve.getId();
            if (gamesList.stream().noneMatch(g -> g.getServeurId().equals(serverId))) {
                gamesList.add(new Games(serverId));
                writeLogFile("logs.txt","Nouveau serveur détecté : " + serve.getName());
            }
        }
        saveGamesList(gamesList);

        RankScheduler rankScheduler = new RankScheduler();
        rankScheduler.startUpdating(jda);
        restoreReminders();
        startTracking();
    }

    /**
     * Crée la liste des commandes du bot
     */
    private static void registerCommands() {
        jda.updateCommands().addCommands(
                // Commande PREMIER et ses sous-commandes
                Commands.slash("premier", "Commandes relatives au mode Premier de Valorant")
                        .addSubcommands(
                                new SubcommandData("event", "Créer un événement de game Premier")
                                    .addOptions(new OptionData(OptionType.STRING, "type", "Type d'évènement", true)
                                        .addChoice("Compétitif", "competitif")
                                        .addChoice("Entraînement", "entrainement")
                                    ),
                                new SubcommandData("créerteam", "Créer sa team Premier")
                                        .addOption(OptionType.STRING, "nom", "Nom de la team", true),
                                new SubcommandData("supteam", "Supprimer sa team Premier"),
                                new SubcommandData("invitejoueur", "Inviter des joueurs dans votre équipe Premier")
                                        .addOption(OptionType.USER, "joueur1", "Joueur à inviter", true)
                                        .addOption(OptionType.USER, "joueur2", "Joueur à inviter", false)
                                        .addOption(OptionType.USER, "joueur3", "Joueur à inviter", false)
                                        .addOption(OptionType.USER, "joueur4", "Joueur à inviter", false)
                                        .addOption(OptionType.USER, "joueur5", "Joueur à inviter", false)
                                        .addOption(OptionType.USER, "joueur6", "Joueur à inviter", false),
                                new SubcommandData("supjoueur", "Supprimer un joueur")
                                        .addOption(OptionType.USER, "joueur", "Joueur à supprimer", true),
                                new SubcommandData("nouveaucapitaine", "Définir un nouveau capitaine")
                                        .addOption(OptionType.USER, "joueur", "Pseudo du nouveau capitaine", true),
                                new SubcommandData("cancelevent","Annuler un événement de game Premier")
                                        .addOption(OptionType.STRING, "jour", "Format jj", true)
                                        .addOption(OptionType.STRING, "mois", "Format mm", true)
                                        .addOption(OptionType.STRING, "année", "Format aaaa", true)
                                        .addOption(OptionType.STRING, "heure", "Format hh", true)
                                        .addOption(OptionType.STRING, "minute", "Format mm", true)
                        ),

                // Commande VALORANT et ses sous-commandes
                Commands.slash("valorant", "Commandes relatives au jeu Valorant")
                        .addSubcommands(
                                new SubcommandData("rank", "Obtenir le rang d'un joueur singe")
                                        .addOptions(new OptionData(OptionType.STRING, "pseudotag", "Pseudo#tag", true, true)
                                            .setAutoComplete(true)),
                                new SubcommandData("stats", "Obtenir les stats d'un joueur")
                                        .addOptions(new OptionData(OptionType.STRING, "pseudotag", "Pseudo#tag", true, true)
                                            .setAutoComplete(true)),
                                new SubcommandData("settracker", "Affiche le peak rank dans ce salon")
                                        .addOptions(new OptionData(OptionType.STRING, "pseudotag", "Pseudo#tag", true, true)
                                                .setAutoComplete(true)),
                                new SubcommandData("deltracker", "Supprime le tracking du joueur dans ce salon")
                                        .addOptions(new OptionData(OptionType.STRING, "pseudotag", "Pseudo#tag", true, true)
                                                .setAutoComplete(true))
                        ),

                // Commandes simples
                Commands.slash("bot", "Info sur le bot"),
                Commands.slash("help", "Obtenir une aide")
                        .addOption(OptionType.STRING, "option", "all = liste tout", false),
                Commands.slash("report", "Signaler un bug"),
                Commands.slash("edtdev","Emploi du temps du développeur"),
                Commands.slash("nouveauté","Nouveauté du bot")
                        .addOption(OptionType.STRING,"version","Précision d'une version du bot",false),
                Commands.slash("documentation","Documentation du bot"),
                Commands.slash("registery", "S'enregistrer sur le bot")
        ).queue();
    }

    /**
     * Boucle de surveillance du joueur Valorant enregistrer dans trackerValo.json
     */
    public static void startTracking() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                ArrayList<TrackedPlayer> players = DataManager.loadTrackedPlayer();
                if (players == null || players.isEmpty()) return;

                for (TrackedPlayer player : players) {
                    // Logique de tracking identique à ton code original
                    String[] pseudoRaw = player.getPseudoRaw().split("#");
                    HttpResponse<String> response = ApiRiotRequete("https://api.henrikdev.xyz/valorant/v2/mmr/eu/" + pseudoRaw[0] + "/" + pseudoRaw[1]);

                    if (response.statusCode() == 200) {
                        JSONObject data = new JSONObject(response.body()).getJSONObject("data");
                        int currentTier = data.getJSONObject("current_data").getInt("currenttier");

                        if (currentTier > player.getPeakTier()) {
                            sendRankupMessage(jda, player, getRankTxtByInt(currentTier), data.getJSONObject("current_data").getJSONObject("images").getString("large"));
                            player.setPeakTier(currentTier);
                            DataManager.saveTrackedPlayer(players);
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                writeLogFile("logs.txt", "Erreur tracking : " + e.getMessage());
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * obtenir la configuration du projet
     * @return l'objet configManager du projet
     */
    public static ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Recrée les rappels pour l'utilisateur qui a un évènement de game Première
     */
    private static void restoreReminders() {
        try {
            netoyageRappel();
            ArrayList<Rappel> rappels = DataManager.loadRappels();
            LocalDateTime now = LocalDateTime.now();

            for (Rappel rappel : rappels) {
                User user = jda.retrieveUserById(rappel.getUserId()).complete();
                if (user == null) continue;

                long delay = rappel.getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis();
                long reminderDelay = delay - (30 * 60 * 1000); // 30 mins avant

                if (reminderDelay > 0) {
                    scheduler.schedule(() -> sendReminder(user), reminderDelay, TimeUnit.MILLISECONDS);
                }
            }
        } catch (Exception e) {
            writeLogFile("logs.txt", "Error reminders: " + e.getMessage());
        }
    }

    /**
     * Si lors de la restoration des rappels un rappel doit être envoyé, car on est dans les -30min.
     * @param user Objet de JDA désignant un utilisateur discord
     */
    private static void sendReminder(User user) {
        user.openPrivateChannel().queue(channel -> {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⏰ Rappel :")
                    .setDescription("Ton match Premier commence dans moins de 30 minutes !")
                    .setColor(Color.CYAN);
            channel.sendMessageEmbeds(embed.build()).queue();
        });
    }

    /**
     * methode tmp pour entreé un changelog ==> automatisation via interface web a implémenté
     */
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
