package botdiscord.gigabot;

import botdiscord.gigabot.system.RankScheduler;
import botdiscord.gigabot.utils.API.ValorantApi;
import botdiscord.gigabot.utils.DB.DataBaseManager;
import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.event_DB;
import botdiscord.gigabot.utils.DB.log_DB;
import botdiscord.gigabot.utils.DB.structure.Rappel;
import botdiscord.gigabot.utils.DB.structure.TrackedPlayer;
import botdiscord.gigabot.utils.DB.tracker_joueur_DB;
import botdiscord.gigabot.utils.ConfigManager;
import botdiscord.gigabot.utils.commands.MessageManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static botdiscord.gigabot.utils.success.success.sendRankupMessage;


public class Main {

    private static ConfigManager configManager;
    public static String version ="1.0.6";
    public static JDA jda;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private DataBaseManager db;
    private tracker_joueur_DB tracker;
    private event_DB rappel;
    private ValorantApi valorantApi;

    /**
     * Point de démarrage du bot
     * @param args argument de démarrage
     * @throws InterruptedException erreur de connexion
     */
    public void main(String[] args) throws InterruptedException, SQLException {
        configManager = new ConfigManager(new File(System.getProperty("user.dir"), "config.toml"));
        String token = configManager.getToml().getString("bot.token");
        this.db = new log_DB();
        this.tracker = new tracker_joueur_DB();
        this.rappel = new event_DB();
        this.valorantApi = new ValorantApi();

        // Initialisation JDA
        jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageManager())
                .build();

        jda.awaitReady();

        registerCommands();

        db.getLogs().writeLog(LevelLog.OK, Main.class.getName(),"Bot démarré");

        RankScheduler rankScheduler = new RankScheduler();

        rankScheduler.startUpdating(jda);
        restoreReminders();
        db.getLogs().writeLog(LevelLog.OK, Main.class.getName(),"Restauration des rappels en mémoire réussie");

        startTracking();
        db.getLogs().writeLog(LevelLog.OK, Main.class.getName(),"Mise à jour du tracking des joueurs réussie");
    }

    /**
     * Crée la liste des commandes du bot
     */
    private void registerCommands() {
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
    public void startTracking() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                ArrayList<TrackedPlayer> players = tracker.getTrackedPlayer();
                if (players == null || players.isEmpty()) return;

                for (TrackedPlayer player : players) {
                    String[] pseudoRaw = player.getValo_pseudo().split("#");
                    HttpResponse<String> response = valorantApi.request("https://api.henrikdev.xyz/valorant/v2/mmr/eu/" + pseudoRaw[0] + "/" + pseudoRaw[1]);

                    if (response.statusCode() == 200) {
                        JSONObject data = new JSONObject(response.body()).getJSONObject("data");
                        int currentTier = data.getJSONObject("current_data").getInt("currenttier");

                        if (currentTier > player.getRank_peak()) {
                            sendRankupMessage(jda, player, valorantApi.getRankTxtByInt(currentTier), data.getJSONObject("current_data").getJSONObject("images").getString("large"));
                            tracker.updatePeakRank(player.getValo_pseudo(),currentTier);
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                db.getLogs().writeLog(LevelLog.ERR, Main.class.getName(),"Erreur tracking : " + e.getMessage());
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
    private void restoreReminders() {
        try {
            rappel.netoyageRappel();
            ArrayList<Rappel> rappels = rappel.getEvent();

            for (Rappel rappel : rappels) {
                User user = jda.retrieveUserById(rappel.getJoueur()).complete();
                if (user == null) continue;

                long delay = rappel.getDate_match().getTime() - System.currentTimeMillis();
                long reminderDelay = delay - (30 * 60 * 1000); // 30 mins avant

                if (reminderDelay > 0) {
                    scheduler.schedule(() -> sendReminder(user), reminderDelay, TimeUnit.MILLISECONDS);
                }
            }
        } catch (Exception e) {
            db.getLogs().writeLog(LevelLog.ERR, Main.class.getName(),"Erreur reminders : " + e.getMessage());
        }
    }

    /**
     * Si lors de la restoration des rappels un rappel doit être envoyé, car on est dans les -30min.
     * @param user Objet de JDA désignant un utilisateur discord
     */
    private void sendReminder(User user) {
        user.openPrivateChannel().queue(channel -> {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⏰ Rappel :")
                    .setDescription("Ton match Premier commence dans moins de 30 minutes !")
                    .setColor(Color.CYAN);
            channel.sendMessageEmbeds(embed.build()).queue();
        });
    }
}

