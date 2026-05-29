package botdiscord.gigabot.utils.commands;

import botdiscord.gigabot.Main;
import botdiscord.gigabot.commandsBot.cmd.*;
import botdiscord.gigabot.system.ServeurDs;
import bot.discordBot.commandsBot.*;
import botdiscord.gigabot.commandsBot.sousCmdPremierValorant.CommandPremierEvent;
import botdiscord.gigabot.commandsBot.sousCmdPremierValorant.CommandPremierTeamInvite;
import botdiscord.gigabot.utils.DB.Valo_Dis_DB;
import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.log_DB;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static bot.discordBot.System.GameConfigRole.getColorForGame;
import static bot.discordBot.System.GameConfigRole.getGameNameByServeurId;
import static botdiscord.gigabot.utils.exception.DefaultException.ExceptionDefault;

/**
 * Écouteur principal des interactions et des événements du serveur Discord (Listener).
 * Cette classe centralise la réception et l'aiguillage des commandes Slash, de l'auto-complétion,
 * des sélections de menus (rôles de jeux), des soumissions de formulaires (Modals).
 */
public class MessageManager extends ListenerAdapter{

    private log_DB logs = new log_DB();
    private Valo_Dis_DB valoDisDb = new Valo_Dis_DB();

    private static CommandRegistry registry = new CommandRegistry();

    //creation de nouvelle commande ici puis cree ca class pour les actions qu'elle realisera
    static {
        registry.addCommand(new Command(
                "bot",
                new CommandBot(),
                "bot"
        ));
        registry.addCommand(new Command(
                "help",
                new CommandHelp(),
                "help"

        ));
        registry.addCommand(new Command(
                "valorant",
                new CommandValo(),
                "valorant"
        ));
        registry.addCommand(new Command(
                "premier",
                new CommandPremier(),
                "premier"
        ));
        registry.addCommand(new Command(
                "nouveauté",
                new CommandNew(),
                "nouveauté"
        ));
        registry.addCommand(new Command(
                "edtdev",
                new CommandEdtDev(),
                "edtdev"
        ));
        registry.addCommand(new Command(
                "documentation",
                new CommandDoc(),
                "documentation"
        ));
        registry.addCommand(new Command(
                "report",
                new CommandReport(),
                "report"
        ));
        registry.addCommand(new Command(
                "registery",
                new CommandRegistery(),
                "registery"
        ));
    }

    private static final String PREFIX = Main.getConfigManager().getToml().getString("bot.prefix");

    public MessageManager() throws SQLException {
    }

    /**
     * Intercepte et gère l'exécution des commandes Slash (`/`) tapées par les utilisateurs.
     * Authentifie la commande, vérifie les permissions requises, et route l'événement vers
     * le sous-système ou la classe de commande adéquate (ex: commandes d'équipes, statistiques, etc.).
     *
     * @param event L'événement d'interaction de commande Slash fourni par JDA.
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();

        List<String> argsList = new ArrayList<>();

        if (event.getSubcommandName() != null) {
            argsList.add("-" + event.getSubcommandName());
            event.getOptions().forEach(option -> {
                argsList.add(option.getAsString());
            });
        } else {
            event.getOptions().forEach(option -> {
                argsList.add(option.getAsString());
            });
        }

        String[] args = argsList.toArray(new String[0]);

        registry.getByAlias(commandName).ifPresent(cmd -> {
            CommandContext ctx = new CommandContext(event);
            cmd.getExecutor().run(ctx, cmd, args);
        });
    }

    /**
     * Obtenir le register des commandes
     * @return la liste des commandes
     */
    public static CommandRegistry getRegistry() {
        return registry;
    }

    /**
     * Intercepte l'activation d'un bouton par les utilisateurs.
     *
     * @param event L'événement d'activation du bouton.
     */
    @Override
    public void onButtonInteraction(@NotNull net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent event) {
        String[] parts = event.getComponentId().split("&");
        if (parts[0].equals("invite_accept") || parts[0].equals("invite_refuse")) {
            new CommandPremierTeamInvite().validerInvitation(event.getUser(), parts[1], parts[0].equals("invite_accept"), event, null);
        }
        if (parts[0].equals("inviteEv_ac") || parts[0].equals("inviteEv_ref")) {
            long timestamp = Long.parseLong(parts[3]);
            LocalDateTime dateTimeEvent = LocalDateTime.ofInstant(java.time.Instant.ofEpochSecond(timestamp), ZoneId.of("Europe/Paris"));
            new CommandPremierEvent().validationInvitation(parts[1],parts[2],dateTimeEvent,parts[4],parts[5],parts[0].equals("inviteEv_ac"),event);
        }
        if (event.getComponentId().equals("start_onboarding")) {
            event.getMessage().delete().queue();

            String guildId = event.getGuild().getId();


            List<String> serverGames = getGameNameByServeurId(guildId);

            StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("choose_games")
                    .setPlaceholder("Choisis tes jeux...");

            if (serverGames.isEmpty()) {
                menuBuilder.addOption("Aucun jeu configuré", "none");
                menuBuilder.setDisabled(true);
            } else {
                for (String gameName : serverGames) {
                    menuBuilder.addOption(gameName, gameName);
                }
                int maxChoices = Math.min(serverGames.size(), 5);
                menuBuilder.setRequiredRange(1, maxChoices);
            }

            event.reply("À quels jeux joues-tu ?")
                    .addActionRow(menuBuilder.build())
                    .setEphemeral(true)
                    .queue();
        }
        if(event.getComponentId().equals("dont_start_onboarding")){
            event.getMessage().delete().queue(); // On nettoie le salon arrivée

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Configuration annulée")
                    .setDescription("Pas de souci ! Tu peux parcourir le serveur librement.")
                    .addField("Note", "Certains salons de jeux pourraient être masqués tant que tu n'as pas choisi tes rôles.", false)
                    .addField("Comment faire plus tard ?", "Tu peux utiliser la commande `/registery` si besoin.", false)
                    .setColor(Color.CYAN);

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
        }
    }

    /**
     * Intercepte la validation et la soumission des formulaires fenêtrés (Modals) par les utilisateurs.
     * Traite notamment les données du formulaire d'onboarding Valorant pour enregistrer
     * et associer le Riot ID du joueur à son compte Discord dans la base locale.
     *
     * @param event L'événement de soumission du formulaire Modal.
     */
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        String customId = event.getModalId();
        if (event.getModalId().equals("report_modal")) {
            String subject = event.getValue("subject").getAsString();
            String body = event.getValue("body").getAsString();
            String user = event.getUser().getName();

            logs.writeLog(LevelLog.OK, MessageManager.class.getName(),"REPORT par " + user + " | Sujet: " + subject);

            TextChannel adminChannel = event.getJDA().getTextChannelById("1495129101155958916");

            if (adminChannel != null) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("🚩 Nouveau Report")
                        .addField("Auteur", user, true)
                        .addField("Sujet", subject, true)
                        .addField("Description", body, false)
                        .setColor(Color.ORANGE)
                        .setTimestamp(OffsetDateTime.now());
                adminChannel.sendMessageEmbeds(embed.build()).queue();
            }


            event.reply("✅ Merci ! Votre signalement a été transmis au développeur.").setEphemeral(true).queue();
        }else if (customId.startsWith("create_event_modal&")) {
            String[] parts = customId.split("&");
            String teamName = parts[1];
            String salonID = parts[2];
            String type = parts[3];

            String jour = event.getValue("event_jour").getAsString();
            String moisRaw = event.getValue("event_mois").getAsString();
            String anneeRaw = event.getValue("event_annee").getAsString();
            String heurem = event.getValue("event_heure").getAsString();
            String minute = event.getValue("event_minute").getAsString();

            LocalDateTime now = LocalDateTime.now();

            String mois = (moisRaw == null || moisRaw.isEmpty())
                    ? String.format("%02d", now.getMonthValue())
                    : moisRaw;

            // Année
            String annee = (anneeRaw == null || anneeRaw.isEmpty())
                    ? String.valueOf(now.getYear())
                    : anneeRaw;

            String date = jour+":"+mois+":"+annee;
            String heure = heurem+":"+minute;

            CommandContext newCtx = new CommandContext(event);
            try{
                new CommandPremierEvent().execute(newCtx, date.replace("/",":"), heure, teamName,salonID,type);
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Invitation envoyé :")
                        .setDescription("Game du **" + date.replace(":","/") +" à "+heure+"** dans la team **" + teamName.toUpperCase() +"**")
                        .addField("Mode de jeu :","**"+type.toUpperCase()+"**",true)
                        .setColor(Color.GREEN);
                event.replyEmbeds(embed.build()).queue();
            }catch (Exception e){
                ExceptionDefault(newCtx,"Problème rencontrer lors de la création des invitations");
            }
        }
    }

    /**
     * Gère les requêtes d'auto-complétion en temps réel lorsqu'un utilisateur saisit des arguments
     * dans une commande Slash (par exemple, la recherche dynamique d'un pseudo Valorant enregistré).
     *
     * @param event L'événement d'interaction d'auto-complétion.
     */
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("valorant") && event.getFocusedOption().getName().equals("pseudotag")) {
            String input = event.getFocusedOption().getValue();

            List<String> suggestions = valoDisDb.getPseudosAutocomplete(input);

            List<net.dv8tion.jda.api.interactions.commands.Command.Choice> choices = suggestions.stream()
                    .map(pseudo -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(pseudo, pseudo))
                    .collect(Collectors.toList());

            event.replyChoices(choices).queue();
        }
    }

    /**
     * Gère l'événement d'arrivée d'un nouveau membre sur le serveur Discord.
     * Configure automatiquement les permissions temporaires de salon (ex: salon d'accueil)
     * pour forcer ou guider l'utilisateur à travers le processus d'onboarding (choix de rôles).
     *
     * @param event L'événement d'intégration d'un nouveau membre (Join).
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Role roles = ServeurDs.getOrCreateRole(event.getGuild(),"membre",Color.GRAY);//donne le role membre des l'arrive sur le serve
        event.getGuild().addRoleToMember(event.getMember(), roles).queue();
    }

    /**
     * Traite les interactions avec les menus de sélection déroulants (StringSelectMenu).
     * Principalement utilisé pour le système d'onboarding permettant aux membres de choisir leurs rôles de jeux.
     * Si le choix inclut Valorant, ouvre automatiquement un formulaire (Modal) pour lier le Riot ID.
     *
     * @param event L'événement d'interaction avec le menu de sélection.
     */
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("choose_games")) {
            List<String> values = event.getValues();
            Member member = event.getMember();
            Guild guild = event.getGuild();
            if (guild == null || member == null) return;

            if (values.contains("Valorant") || values.contains("valo")) {
                TextInput pseudo = TextInput.create("valo_pseudo", "Pseudo Valorant (avec #tag)", TextInputStyle.SHORT)
                        .setPlaceholder("Pseudo#1234")
                        .setRequired(true)
                        .build();

                Modal modal = Modal.create("onboarding_valo", "Ton Pseudo Valorant")
                        .addComponents(ActionRow.of(pseudo))
                        .build();

                event.replyModal(modal).queue();
            }

            for (String gameName : values) {
                Color gameColor =getColorForGame(guild.getId(), gameName);

                Role role = ServeurDs.getOrCreateRole(guild, gameName, gameColor);

                guild.addRoleToMember(member, role).queue();
            }

            if (!event.isAcknowledged()) {
                event.reply("Tes rôles ont été mis à jour !").setEphemeral(true).queue();
            }

            TextChannel welcomeChannel = guild.getTextChannelsByName("accueil-gigabot", true)
                    .stream().findFirst().orElse(null);

            if (welcomeChannel != null && welcomeChannel.getPermissionOverride(member) != null) {
                welcomeChannel.getPermissionOverride(member).delete().queue();
            }
        }
    }
}

