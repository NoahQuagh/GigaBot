package botdiscord.gigabot.commandsBot.sousCmdPremierValorant;

import botdiscord.gigabot.commandsBot.cmd.CommandPremier;
import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.equipe_DB;
import botdiscord.gigabot.utils.DB.event_DB;
import botdiscord.gigabot.utils.DB.log_DB;
import botdiscord.gigabot.utils.DB.structure.JoueurPremier;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import botdiscord.gigabot.utils.exception.DateException;
import botdiscord.gigabot.utils.exception.EquipeException;
import botdiscord.gigabot.utils.exception.SyntaxeException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static botdiscord.gigabot.Main.jda;
import static botdiscord.gigabot.utils.exception.DefaultException.ExceptionDefault;


public class CommandPremierEvent extends CommandPremier {

    public static Map<String, java.util.concurrent.ScheduledFuture<?>> tachesActives = new java.util.concurrent.ConcurrentHashMap<>();
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    public int nbRefu = 0;//a revoir car pas unique par chaque team et event
    private log_DB logs;
    private equipe_DB equipes;
    private event_DB rappels;

    public CommandPremierEvent(){
        this.logs=new log_DB();
        this.equipes=new equipe_DB();
        this.rappels=new event_DB();
    }


    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        rappels.netoyageRappel();
        try {
            String type = ctx.isSlash()
                    ? ctx.getOptionString("type").orElseThrow(() -> new SyntaxeException(ctx, "/premier event <>"))
                    : (args.length >= 2 ? args[1] : null);

            String channelId = ctx.getChannelId();
            String teamName = null;

            if (!equipes.estCapitaine(ctx.getAuthorId())) {
                teamName = equipes.getTeamNameByCapitaineId(ctx.getAuthorId());
            } else if (!equipes.estAdjoint(ctx.getAuthorId())) {
                teamName = equipes.getTeamNameByAdjointId(ctx.getAuthorId());
            }

            if (teamName == null) {
                throw new EquipeException(ctx, "Il n'existe aucune team Premier dont vous êtes le capitaine ou l'adjoint.");
            }

            ArrayList<JoueurPremier> joueurs = equipes.getJoueurPremierListByTeamName(teamName);
            if (joueurs == null) {
                throw new EquipeException(ctx, "Les joueurs de l'équipe '" + teamName + "' sont introuvable.");
            }

            if (joueurs.size() < 5) {
                throw new EquipeException(ctx, "Il n'y a pas assez de joueurs dans votre team (minimum 5) pour un événement Premier.");
            }

            TextInput JourInput = TextInput.create("event_jour", "Jour", TextInputStyle.SHORT)
                    .setPlaceholder("Numéro du jour")
                    .setMinLength(2)
                    .setMaxLength(2)
                    .setRequired(true)
                    .build();

            TextInput MoisInput = TextInput.create("event_mois", "Mois", TextInputStyle.SHORT)
                    .setPlaceholder("Numéro du mois (Vide = mois actuelle)")
                    .setMinLength(2)
                    .setMaxLength(2)
                    .setRequired(false)
                    .build();

            TextInput AnneeInput = TextInput.create("event_annee", "Année", TextInputStyle.SHORT)
                    .setPlaceholder("Année (Vide = année actuelle)")
                    .setMinLength(4)
                    .setMaxLength(4)
                    .setRequired(false)
                    .build();

            TextInput heureInput = TextInput.create("event_heure", "Heure", TextInputStyle.SHORT)
                    .setPlaceholder("Heure")
                    .setMinLength(2)
                    .setMaxLength(2)
                    .setRequired(true)
                    .build();

            TextInput minuteInput = TextInput.create("event_minute", "Minute", TextInputStyle.SHORT)
                    .setPlaceholder("Minute")
                    .setMinLength(2)
                    .setMaxLength(2)
                    .setRequired(true)
                    .build();


            Modal modal = Modal.create("create_event_modal&"+teamName+"&"+channelId+"&"+type, "Création d'Évènement Premier")
                    .addComponents(ActionRow.of(JourInput),ActionRow.of(MoisInput),ActionRow.of(AnneeInput),ActionRow.of(heureInput), ActionRow.of(minuteInput))
                    .build();


            if (ctx.getEvent() instanceof net.dv8tion.jda.api.interactions.callbacks.IModalCallback callback) {
                callback.replyModal(modal).queue();
            }

        }catch (SyntaxeException e){
            logs.writeLog(LevelLog.ERR,CommandPremierEvent.class.getName(), ctx.getAuthorName()+" erreur de syntaxe : "+e);
        }catch (DateException e){
            logs.writeLog(LevelLog.ERR,CommandPremierEvent.class.getName(), ctx.getAuthorName()+" erreur de syntaxe horaire : "+e);
        }catch (EquipeException e){
            logs.writeLog(LevelLog.ERR,CommandPremierEvent.class.getName(),args[3].toLowerCase()+" aucune données trouver : "+e);
        } catch (Exception e) {
            logs.writeLog(LevelLog.ERR,CommandPremierEvent.class.getName(), " Échec de la commande : "+e);
            ExceptionDefault(ctx,"Impossible de crée un évènement pour la team Premier");
        }

    }

    private void sendInvitation(CommandContext ctx,User user, String date, String heure,LocalDateTime dateTimeEvent,String team,String salonid,String type) {

        long timestamp = dateTimeEvent.atZone(ZoneId.of("Europe/Paris")).toEpochSecond();

        user.openPrivateChannel().queue(pc -> {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Invitation Premier")
                    .setDescription("Vous avez été invité à une game de Premier le **" + date.replace(":", "/") + "** à **" + heure + "**"+" dans la team **"+team+"**.")
                    .addField("Mode de jeu :","**"+type.toUpperCase()+"**",true)
                    .setColor(Color.CYAN);

            pc.sendMessageEmbeds(embed.build())
                    .addActionRow(
                            net.dv8tion.jda.api.interactions.components.buttons.Button.success("inviteEv_ac&"+date+"&"+heure+"&"+timestamp+"&"+team+"&"+salonid, "Accepter"),
                            net.dv8tion.jda.api.interactions.components.buttons.Button.danger("inviteEv_ref&"+date+"&"+heure+"&"+timestamp+"&"+team+"&"+salonid, "Refuser")
                    )
                    .queue();
        }, throwable -> {
            logs.writeLog(LevelLog.ERR,CommandPremierEvent.class.getName(), "Impossible d'envoyer un MP à " + user.getName());
        });
    }

    public static void scheduleReminder(User user, LocalDateTime eventTime) {
        LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Europe/Paris"));

        LocalDateTime reminderTime = eventTime.minusMinutes(30);

        long delay = Duration.between(now, reminderTime).getSeconds();

        if (delay > 0) {
            java.util.concurrent.ScheduledFuture<?> task = scheduler.schedule(() -> {
                sendReminderEmbed(user);
                tachesActives.remove(user.getId() + ":" + eventTime);
            }, delay, java.util.concurrent.TimeUnit.SECONDS);

            tachesActives.put(user.getId() + ":" + eventTime, task);

        } else {
            sendReminderEmbed(user);
        }
    }

    private static void sendReminderEmbed(User user){
        user.openPrivateChannel().queue(channel -> {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⏰ Rappel :")
                    .setDescription("Ton match Premier commence dans moins de 30 minutes !")
                    .setColor(Color.CYAN);
            channel.sendMessageEmbeds(embed.build()).queue();
        });
    }

    public void validationInvitation(String date, String heure,LocalDateTime dateTimeEvent,String team,String salonid,boolean accepte, net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent event){
        ArrayList<JoueurPremier> listejoueurs = equipes.getJoueurPremierListByTeamName(team);
        if(accepte){
            if(nbRefu>=listejoueurs.size()-4){
                event.editMessage("✅ Merci pour ta participation mais l'évènement est annulé dû à un manque de joueurs.")
                        .setEmbeds(new ArrayList<>())
                        .setComponents(new ArrayList<>())
                        .queue();
                return;
            }
            rappels.setEvent(equipes.getTeamIdByTeamName(team),dateTimeEvent,event.getUser().getId());

            scheduleReminder(event.getUser(), dateTimeEvent);

            event.editMessage("✅ Ta participation est enregistrée ! Tu receveras un rappel **30min** avant le debut de la partie.")
                    .setComponents(new ArrayList<>())
                    .queue();

            logs.writeLog(LevelLog.OK,getClass().getName(),event.getUser().getName()+"accept the invitation at "+dateTimeEvent+" in "+team);

            String chefId=equipes.getCapitaineIdByTeamName(team);

            jda.retrieveUserById(chefId).queue(user -> {
                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessage("✅ **" + event.getUser().getName() + "** a accepté l'invitation pour le match du **"+dateTimeEvent.toString().replace("T"," à ").replace("-","/")+"** dans la team **"+team+"**").queue();
                }, throwable -> {
                    logs.writeLog(LevelLog.ERR,getClass().getName(),"Impossible d'envoyer le MP : l'utilisateur a bloqué le bot");
                });
            }, throwable -> {
                logs.writeLog(LevelLog.ERR,getClass().getName(),"Utilisateur introuvable pour l'ID : " + event.getUser().getId());
            });

            GuildMessageChannel channel = jda.getChannelById(GuildMessageChannel.class, salonid);
            channel.sendMessage("✅ **" + event.getUser().getName() + "** a accepté l'invitation pour le match du **"+dateTimeEvent.toString().replace("T"," à ").replace("-","/")+"** dans la team **"+team+"**").queue();
        }else{
            if(nbRefu>=listejoueurs.size()-4){
                event.editMessage("❌ Invitation déclinée.")
                        .setEmbeds(new ArrayList<>())
                        .setComponents(new ArrayList<>())
                        .queue();
                return;
            }

            String chefId=equipes.getCapitaineIdByTeamName(team);

            event.editMessage("❌ Invitation déclinée.")
                    .setEmbeds(new ArrayList<>())
                    .setComponents(new ArrayList<>())
                    .queue();

            jda.retrieveUserById(chefId).queue(user -> {
                user.openPrivateChannel().queue(channel -> {
                    channel.sendMessage("❌ **" + event.getUser().getName() + "** a refusé l'invitation pour le match du **"+dateTimeEvent.toString().replace("T"," à ").replace("-","/")+"** dans la team **"+team+"**").queue();
                }, throwable -> {
                    logs.writeLog(LevelLog.ERR,getClass().getName(),"Impossible d'envoyer le MP : l'utilisateur a bloqué le bot.");
                });
            }, throwable -> {
                logs.writeLog(LevelLog.ERR,getClass().getName(),"Utilisateur introuvable pour l'ID : " + event.getUser().getId());
            });

            GuildMessageChannel channel = jda.getChannelById(GuildMessageChannel.class, salonid);
            channel.sendMessage("❌ **" + event.getUser().getName() + "** a refusé l'invitation pour le match du **"+dateTimeEvent.toString().replace("T"," à ").replace("-","/")+"** dans la team **"+team+"**").queue();
            nbRefu++;
            if(nbRefu==listejoueurs.size()-4){
                new CommandPremierCancelEvent().execute(null,date.replace("/",":").trim(),heure.trim(),team,1,salonid);
            }
            logs.writeLog(LevelLog.OK,getClass().getName(),event.getUser().getName()+"declined the invitation at "+dateTimeEvent+" in "+team);
        }
    }

    public void execute(CommandContext ctx, String date, String heure, String team, String salonId, String type) throws DateException, EquipeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");
        LocalDateTime dateTimeEvent = LocalDateTime.parse(date + " " + heure, formatter);

        ZoneId parisZone = ZoneId.of("Europe/Paris");
        ZonedDateTime nowParis = ZonedDateTime.now(parisZone);

        ZonedDateTime matchTimeParis = dateTimeEvent.atZone(parisZone);

        if (matchTimeParis.isBefore(nowParis)) {
            throw new DateException(ctx, "Erreur de date", "Impossible de créer un événement dans le passé", "Heure actuelle (Paris)", nowParis.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }

        ArrayList<JoueurPremier> listeJoueurs = equipes.getJoueurPremierListByTeamName(team);
        if (listeJoueurs.isEmpty()) return;

        for (JoueurPremier joueur : listeJoueurs) {
            String currentId = joueur.getDiscordId();
            if (currentId == null || currentId.trim().isEmpty()) continue;

            jda.retrieveUserById(currentId).queue(user -> {
                sendInvitation(ctx, user, date, heure, dateTimeEvent, team, salonId, type);
                logs.writeLog(LevelLog.OK, getClass().getName(), "Invitation envoyée à : " + currentId);
            }, throwable -> {
                logs.writeLog(LevelLog.ERR, getClass().getName(), "Aucune donnée trouvée pour ce joueur : " + currentId);
                ExceptionDefault(ctx, "Impossible de créer un événement pour la team Premier " + team);
            });
        }
    }
}