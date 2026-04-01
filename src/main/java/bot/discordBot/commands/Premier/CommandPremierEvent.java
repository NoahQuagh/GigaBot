package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Equipe;
import bot.discordBot.utils.commands.datamanager.DataStructure.Rappel;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static bot.discordBot.utils.commands.Code.AUCUNE_DONNEE_TROUVER;
import static bot.discordBot.utils.commands.Code.SYNTAXE_INCORRECTE;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierEvent extends CommandPremier {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    @SuppressWarnings("unchecked")
    @Override
    public void run(MessageCreateEvent event, Command command, String[] args) {
        netoyageRappel();
        if (args.length != 4) {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Exemple syntaxe:","```!premier -event dd/mm/yyyy hh:mm ningen```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }
        try {

            if (DataManager.loadEquipes().isEmpty()) {
                String name=event.getMessageAuthor().getDisplayName();
                writeLogFile("logs.txt",name+" | Code : "+ AUCUNE_DONNEE_TROUVER);
                return;
            }

            String date = args[1].replace("/", ":");
            String heure = args[2];
            String team = args[3].toLowerCase();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");
            LocalDateTime dateTimeEvent = LocalDateTime.parse(date + " " + heure, formatter);

            ZoneId parisZone = ZoneId.of("Europe/Paris");
            ZonedDateTime nowParis = ZonedDateTime.now(parisZone);

            ZonedDateTime matchTimeParis = dateTimeEvent.atZone(parisZone);

            if (matchTimeParis.isBefore(nowParis)) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("❌ Erreur de date")
                        .setDescription("Impossible de créer un événement dans le passé !")
                        .addField("Heure actuelle (Paris)", nowParis.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                        .setColor(Color.RED);
                event.getChannel().sendMessage(embed);
                writeLogFile("logs.txt", event.getMessageAuthor().getName()+SYNTAXE_INCORRECTE);
                return;
            }

            ArrayList<Equipe> equipes = DataManager.loadEquipes();


            for (Equipe equipe : equipes) {
                if (equipe.getEquipeId().equalsIgnoreCase(team)) {
                    for (String currentId : equipe.getJoueurIds()) {
                        event.getApi().getUserById(currentId).thenAccept(user -> {
                            sendInvitationAndListen(user, date, heure, dateTimeEvent, team);

                            event.getChannel().sendMessage("✅ Invitation envoyé à **" + user.getName() +
                                    "** pour la game du **" + dateTimeEvent.toString().replace("T", " à ").replace("-", "/") +
                                    "** dans la team **" + team+"**");

                        }).exceptionally(e -> {
                            writeLogFile("logs.txt", currentId + " | " + AUCUNE_DONNEE_TROUVER + " >> " + e);
                            return null;
                        });
                    }
                    return;
                }
            }
            event.getChannel().sendMessage("❌ L'équipe **" + team + "** n'existe pas dans la base de données.");


        } catch (Exception e) {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Problem occurred during the creation of a game Premier");
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Probleme survenu sur la creation d'une game premier.","```code : "+e+"```")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
        }

    }

    private void sendInvitationAndListen(User user, String date, String heure,LocalDateTime dateTimeEvent,String team) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Invitation Premier")
                .setDescription("Vous avez été invité à une game de Premier le **" + date.replace(":", "/") + "** à **" + heure + "**"+" dans la team **"+team+"**.")
                .setColor(Color.CYAN);
        new org.javacord.api.entity.message.MessageBuilder()
                .setEmbed(embed)
                .addComponents(
                        org.javacord.api.entity.message.component.ActionRow.of(
                                Button.create("event_yes"+ user.getId(), ButtonStyle.SUCCESS, "Participer"),
                                Button.create("event_no"+ user.getId(), ButtonStyle.DANGER, "Refuser")
                        )
                )
                .send(user).thenAccept(message -> {
                    message.addButtonClickListener(clickEvent -> {
                        if (!clickEvent.getButtonInteraction().getUser().equals(user)) return;

                        String customId = clickEvent.getButtonInteraction().getCustomId();
                        var updater = message.createUpdater();

                        if (customId.startsWith("event_yes")) {

                            ArrayList<Rappel> rappels = DataManager.loadRappels();
                            rappels.add(new Rappel(user.getIdAsString(), dateTimeEvent));
                            DataManager.saveRappels(rappels);

                            scheduleReminder(user, dateTimeEvent);

                            updater.setContent("✅ Ta participation est enregistrée ! Tu receveras un rappel **30min avant** le debut de la partie.")
                                    .removeAllEmbeds()
                                    .removeAllComponents()
                                    .applyChanges();

                            writeLogFile("logs.txt", user.getName()+"accept the invitation at "+dateTimeEvent+" in "+team);

                            String chefId = DataManager.loadEquipes().get(0).getChefId();
                            user.getApi().getUserById(chefId).thenAccept(chef -> {
                                chef.sendMessage("✅ **" + user.getName() + "** a accepté l'invitation pour le match du **"+dateTimeEvent.toString().replace("T"," à ").replace("-","/")+"** dans la team **"+team+"**");
                            });

                        } else {
                            updater.setContent("❌ Invitation déclinée.")
                                    .removeAllEmbeds()
                                    .removeAllComponents()
                                    .applyChanges();
                            String chefId = DataManager.loadEquipes().get(0).getChefId();
                            user.getApi().getUserById(chefId).thenAccept(chef -> {
                                chef.sendMessage("❌ **" + user.getName() + "** a refusé l'invitation pour le match du **"+dateTimeEvent.toString().replace("T"," à ").replace("-","/")+"** dans la team **"+team+"**");
                            });
                            writeLogFile("logs.txt", user.getName()+"declined the invitation at "+dateTimeEvent+" in "+team);
                        }
                    }).removeAfter(1, TimeUnit.DAYS);
                });
    }

    public static void scheduleReminder(User user,LocalDateTime eventTime) {
        LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Europe/Paris"));

        LocalDateTime reminderTime = eventTime.minusMinutes(30);

        long delay = Duration.between(now, reminderTime).getSeconds();

        if (delay > 0) {
            scheduler.schedule(() -> {
                sendReminderEmbed(user);
            }, delay, TimeUnit.SECONDS);
        } else {
            sendReminderEmbed(user);
        }
    }

    private static void sendReminderEmbed(User user){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("⏰ Rappel :")
                .setDescription("Ton match Premier commence dans moins de **30 minutes** !")
                .setColor(Color.CYAN);
        new org.javacord.api.entity.message.MessageBuilder()
                .setEmbed(embed)
                .send(user);
    }

    public static void netoyageRappel(){
        ArrayList<Rappel> rappels = DataManager.loadRappels();
        if (rappels == null || rappels.isEmpty()) return;

        ZonedDateTime nowParis = ZonedDateTime.now(ZoneId.of("Europe/Paris"));

        boolean aEteModifie = rappels.removeIf(rappel -> {
            ZonedDateTime dateMatch = rappel.getDate().atZone(ZoneId.of("Europe/Paris"));
            return dateMatch.plusMinutes(10).isBefore(nowParis);
        });

        if (aEteModifie) {
            DataManager.saveRappels(rappels);
            writeLogFile("logs.txt", "Nettoyage automatique : Rappels expirés supprimés.");
        }
    }
}