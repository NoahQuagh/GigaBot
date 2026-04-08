package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.Exception.DateException;
import bot.discordBot.utils.Exception.EquipeException;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Equipe;
import bot.discordBot.utils.commands.datamanager.DataStructure.Rappel;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static bot.discordBot.Main.api;
import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.commands.Code.AUCUNE_DONNEE_TROUVER;
import static bot.discordBot.utils.commands.Code.SYNTAXE_INCORRECTE;
import static bot.discordBot.utils.commands.datamanager.DataStructure.Equipe.*;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierEvent extends CommandPremier {

    public static java.util.Map<String, java.util.concurrent.ScheduledFuture<?>> tachesActives = new java.util.HashMap<>();
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    public int nbRefu = 0;

    @SuppressWarnings("unchecked")
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        netoyageRappel();
        if (ctx.isSlash()) ctx.defer();
        try {
            String teamName = null;

            if (!CapitaineNaPasDEquipe(ctx.getAuthorId())) {
                teamName = getTeamNameByIdCapitaine(ctx.getAuthorId());
            } else if (!AdjointNaPasDEquipe(ctx.getAuthorId())) {
                teamName = getTeamNameByIdAdjoint(ctx.getAuthorId());
            }

            if (teamName == null) {
                throw new EquipeException(ctx, "Il n'existe aucune team Premier dont vous êtes le capitaine ou l'adjoint.");
            }

            Equipe equipe = getEquipeByEquipeName(teamName);
            if (equipe == null) {
                throw new EquipeException(ctx, "Erreur interne : l'équipe '" + teamName + "' est introuvable.");
            }

            if (equipe.getJoueurIds().size() < 5) {
                throw new EquipeException(ctx, "Il n'y a pas assez de joueurs dans votre team (minimum 5).");
            }

            execute(ctx, args[1] + ":" + args[2] + ":" + args[3], args[4] + ":" + args[5], teamName);
        }catch (SyntaxeException e){
            writeLogFile("logs.txt",ctx.getAuthorName()+" | Code : "+ SYNTAXE_INCORRECTE);
        }catch (DateException e){
            writeLogFile("logs.txt", ctx.getAuthorName() + SYNTAXE_INCORRECTE);
        }catch (EquipeException e){
            writeLogFile("logs.txt",args[3].toLowerCase()+" | Code : "+ AUCUNE_DONNEE_TROUVER);
        } catch (Exception e) {
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
            ExceptionDefault(ctx,"Impossible de crée un évènement pour la team Premier "+getTeamNameByIdCapitaine(ctx.getAuthorId()));
        }

    }

    private void sendInvitationAndListen(CommandContext ctx,User user, String date, String heure,LocalDateTime dateTimeEvent,String team,String salonid) {

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

                        Equipe equipe = getEquipeByEquipeName(team);


                        if (customId.startsWith("event_yes")) {
                            if(nbRefu==equipe.getJoueurIds().size()-4){
                                updater.setContent("✅ Merci pour ta participation mais l'évènement est annulé dù à un manque de joueur.")
                                        .removeAllEmbeds()
                                        .removeAllComponents()
                                        .applyChanges();
                                return;
                            }

                            ArrayList<Rappel> rappels = DataManager.loadRappels();
                            rappels.add(new Rappel(user.getIdAsString(), dateTimeEvent));
                            DataManager.saveRappels(rappels);

                            scheduleReminder(user, dateTimeEvent);

                            updater.setContent("✅ Ta participation est enregistrée ! Tu receveras un rappel **30min** avant le debut de la partie.")
                                    .removeAllEmbeds()
                                    .removeAllComponents()
                                    .applyChanges();

                            writeLogFile("logs.txt", user.getName()+"accept the invitation at "+dateTimeEvent+" in "+team);

                            String chefId = getEquipeByEquipeName(team).getChefId();

                            api.getTextChannelById(salonid).ifPresentOrElse(channel -> {
                                channel.sendMessage("✅ **" + user.getName() + "** a accepté l'invitation pour le match du **"+dateTimeEvent.toString().replace("T"," à ").replace("-","/")+"** dans la team **"+team+"**");
                            }, () -> {
                                writeLogFile("logs.txt", " problème rencontrer pour l'envoie dans le salon  "+salonid+" pour "+team);
                            });

                            user.getApi().getUserById(chefId).thenAccept(chef -> {
                                chef.sendMessage("✅ **" + user.getName() + "** a accepté l'invitation pour le match du **"+dateTimeEvent.toString().replace("T"," à ").replace("-","/")+"** dans la team **"+team+"**");
                            });

                        } else {

                            updater.setContent("❌ Invitation déclinée.")
                                    .removeAllEmbeds()
                                    .removeAllComponents()
                                    .applyChanges();
                            String chefId = DataManager.loadEquipes().get(0).getChefId();

                            api.getTextChannelById(salonid).ifPresentOrElse(channel -> {
                                channel.sendMessage("❌ **" + user.getName() + "** a refusé l'invitation pour le match du **"+dateTimeEvent.toString().replace("T"," à ").replace("-","/")+"** dans la team **"+team+"**");
                            }, () -> {
                                writeLogFile("logs.txt", " problème rencontrer pour l'envoie dans le salon  "+salonid+" pour "+team);
                            });

                            user.getApi().getUserById(chefId).thenAccept(chef -> {
                                chef.sendMessage("❌ **" + user.getName() + "** a refusé l'invitation pour le match du **"+dateTimeEvent.toString().replace("T"," à ").replace("-","/")+"** dans la team **"+team+"**");
                            });
                            nbRefu++;
                            if(nbRefu==equipe.getJoueurIds().size()-4){
                                new CommandPremierCancelEvent().execute(ctx,heure.replace("/",":"),date,team,DataManager.loadRappels(),equipe,1);
                            }
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
            java.util.concurrent.ScheduledFuture<?> task = scheduler.schedule(() -> {
                sendReminderEmbed(user);
                tachesActives.remove(user.getIdAsString() + ":" + eventTime);
            }, delay, java.util.concurrent.TimeUnit.SECONDS);

            tachesActives.put(user.getIdAsString() + ":" + eventTime, task);

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

    public void execute(CommandContext ctx,String date,String heure,String team) throws DateException,EquipeException{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");
        LocalDateTime dateTimeEvent = LocalDateTime.parse(date + " " + heure, formatter);

        ZoneId parisZone = ZoneId.of("Europe/Paris");
        ZonedDateTime nowParis = ZonedDateTime.now(parisZone);

        ZonedDateTime matchTimeParis = dateTimeEvent.atZone(parisZone);

        if (matchTimeParis.isBefore(nowParis))
            throw new DateException(ctx, "Erreur de date", "Impossible de créer un événement dans le passé", "Heure actuelle (Paris)", nowParis.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        ArrayList<Equipe> equipes = DataManager.loadEquipes();

        String channelId = ctx.getChannel()
                .map(channel -> channel.getIdAsString())
                .orElse("");


        for (Equipe equipe : equipes) {
            if (equipe.getEquipeId().equalsIgnoreCase(team)) {
                for (String currentId : equipe.getJoueurIds()) {

                    if (currentId == null || currentId.trim().isEmpty()) continue;

                    ctx.getApi().getUserById(currentId).thenAccept(user -> {
                        sendInvitationAndListen(ctx,user, date, heure, dateTimeEvent, team,channelId);
                        writeLogFile("logs.txt", currentId + " | invitatiion envoyé à "+currentId);

                    }).exceptionally(e -> {
                        writeLogFile("logs.txt", currentId + " | " + AUCUNE_DONNEE_TROUVER + " >> " + e);
                        ExceptionDefault(ctx, "Impossible de crée un évènement pour la team Premier " + team);
                        return null;
                    });
                }
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Invitation envoyé :")
                        .setDescription("Game du **" + date.replace(":","/") +" à "+heure+"** dans la team **" + team.toUpperCase() +"**")
                        .setColor(Color.GREEN);
                ctx.replyDeferred(embed);
                return;
            }
        }
    }
}