package bot.discordBot;

import bot.discordBot.commands.Premier.CommandPremierEvent;
import bot.discordBot.utils.ConfigManager;
import bot.discordBot.utils.commands.MessageManager;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Bug;
import bot.discordBot.utils.commands.datamanager.DataStructure.Nouveaute;
import bot.discordBot.utils.commands.datamanager.DataStructure.Rappel;
import bot.discordBot.utils.commands.datamanager.DataStructure.StrucNew;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static bot.discordBot.commands.Premier.CommandPremierEvent.netoyageRappel;
import static bot.discordBot.commands.Premier.CommandPremierEvent.scheduleReminder;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class Main {

    public static DiscordApi api;
    private static ConfigManager configManager;
    public static String version ="1.0.2";

    public static void main(String[] args) {
        entrerNew();

        configManager = new ConfigManager(new File(System.getProperty("user.dir"),"config.toml"));

        api = new DiscordApiBuilder()
                .setToken(configManager.getToml().getString("bot.token"))
                .addIntents(Intent.GUILD_MESSAGES, Intent.MESSAGE_CONTENT)
                .login().join();

        api.addMessageCreateListener(MessageManager::create);
        writeLogFile("logs.txt","bot start");
        restoreReminders(api);

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
        n1.add(new Nouveaute("Nouvelle commande !new :","Cette commande permet de découvrir les dernières nouveautés du GigaBot."));
        n1.add(new Nouveaute("Améliorations architecturales du système de sauvegarde du bot :","Le système de stockage est passé d'un format texte brut (.txt) " +
                "à une structure JSON via la bibliothèque Google Gson. Ce changement abandonne la gestion par lignes concaténées au profit d'une sérialisation d'objets Java." +
                "De plus cette structure assure une prévention de la corruption de donnée et une auto-réparation en cas d'erreur"));

        ArrayList<Bug> b1 = new ArrayList<>();
        b1.add(new Bug("Conflit de Fuseau Horaire","Décalage de 2 heures entre l'heure de programmation du rappel d'une game premier et l'exécution réelle du rappel." +
                "La cause est que le serveur Oracle Cloud est configuré par défaut sur le fuseau UTC (Londres), tandis que l'utilisateur est en UTC+2 (Paris/Heure d'été).","Synchronisation du serveur en UTC+2 (Paris/Heure d'été)"));
        b1.add(new Bug("Corruption et Explosion des fichiers de sauvegarde","Apparition de milliers de lignes identiques ou de caractères de contrôle parasites (type ^M) dans le fichier de rappel, entraînant un ralentissement critique du bot."+
                "Les causes viennent d'abord d'une boucle d'écriture infinie : Présence de processus fantômes écrivant simultanément dans le même fichier." +
                "La seconde Cause viens d'un I/O Inefficient : Appel de la méthode de sauvegarde à l'intérieur d'une boucle, provoquant des accès disque trop fréquents et des corruptions de flux.","La sauvegarde est désormais effectuée une seule fois après la complétion de la boucle de traitement en mémoire."));

        ArrayList<StrucNew> paquet = new ArrayList<>();
        paquet.add(new StrucNew(version,n1,b1));

        DataManager.saveNew(paquet);
        */
    }
}
