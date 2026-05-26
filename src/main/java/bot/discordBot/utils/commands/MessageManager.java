package bot.discordBot.utils.commands;

import bot.discordBot.Main;
import bot.discordBot.System.ServeurDs;
import bot.discordBot.commands.*;
import bot.discordBot.commands.Premier.CommandPremierEvent;
import bot.discordBot.commands.Premier.CommandPremierTeamInvite;
import bot.discordBot.utils.Exception.ApiException;
import bot.discordBot.utils.Exception.JoueurException;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.CompteValoDiscord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.Route;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.awt.*;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static bot.discordBot.System.GameConfigRole.getColorForGame;
import static bot.discordBot.System.GameConfigRole.getGameNameByServeurId;
import static bot.discordBot.System.ServeurDs.getOrCreateWelcomeChannel;
import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.Procedure.ApiProcedure.ApiRiotRequete;
import static bot.discordBot.utils.Procedure.ApiProcedure.getColorRankByRankTxt;
import static bot.discordBot.utils.Procedure.ValoDisProcedure.getPseudosAutocomplete;
import static bot.discordBot.utils.Procedure.ValoDisProcedure.pseudoValoExist;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

/**
 * Écouteur principal des interactions et des événements du serveur Discord (Listener).
 * Cette classe centralise la réception et l'aiguillage des commandes Slash, de l'auto-complétion,
 * des sélections de menus (rôles de jeux), des soumissions de formulaires (Modals).
 */
public class MessageManager extends ListenerAdapter{

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

    /**
     * methode non utilisable
     * @param event événement
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();
        if (message.startsWith("!")) { // Remplace par ton prefix si différent
            String[] split = message.substring(1).split("\\s+");
            String alias = split[0].toLowerCase();
            String[] args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, args.length);

            registry.getByAlias(alias).ifPresent(cmd -> {
                CommandContext ctx = new CommandContext(event);
                cmd.getExecutor().run(ctx, cmd, args);
            });
        }
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

            // 1. Log l'info
            writeLogFile("reports.txt", "REPORT par " + user + " | Sujet: " + subject);

            // 2. Envoyer une copie dans un salon spécifique pour les admins
            // Remplace par l'ID de ton salon de logs/admin
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
        if (event.getModalId().equals("onboarding_valo")) {
            String inputPseudo = event.getValue("valo_pseudo").getAsString();


            if (!inputPseudo.contains("#")) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Format invalide ! (Ex: Pseudo#1234)")
                                .addField("pour terminé la configuration :","Tu peux utiliser la commande `/registery` dans un salon dédié au commande bot",false)
                        .setColor(Color.CYAN);
                event.replyEmbeds(embed.build())
                        .setEphemeral(true).queue();
                return;
            }

            String pseudo = inputPseudo.substring(0, inputPseudo.indexOf("#"));
            String tag = inputPseudo.substring(inputPseudo.indexOf("#") + 1);

            CommandContext newCtx = new CommandContext(event);

            HttpResponse<String> response = ApiRiotRequete(newCtx,"https://api.henrikdev.xyz/valorant/v1/mmr/eu/" + pseudo + "/" + tag);
            if (response == null) throw new ApiException(newCtx,"L'appel à l'API Riot a échoué");
            if (response.statusCode() != 200) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Joueur introuvable !")
                        .setDescription("Vérifie l'orthographe de ton pseudo et de ton tag.")
                        .addField("pour terminé la configuration :","Tu peux utiliser la commande `/registery` dans un salon dédié au commande bot",false)
                        .setColor(Color.CYAN);
                event.replyEmbeds(embed.build())
                        .setEphemeral(true).queue();
                return;
            }

            JSONObject json = new JSONObject(response.body());

            if (!json.has("data") || json.isNull("data")) throw new JoueurException(newCtx,"Impossible de lire les données du joueur");

            JSONObject data = json.getJSONObject("data");

            String rank = data.optString("currenttierpatched", "Inconnu");

            ArrayList<CompteValoDiscord> comptes = DataManager.loadValoDis();
            if(!(pseudoValoExist(inputPseudo))){
                comptes.add(new CompteValoDiscord(event.getUser().getId(),event.getUser().getName(),inputPseudo));
            }
            DataManager.saveValoDis(comptes);

            Color rankColor = getColorRankByRankTxt(rank);


            Role rankRole = ServeurDs.getOrCreateRole(event.getGuild(), rank, rankColor);
            event.getGuild().addRoleToMember(event.getMember(), rankRole).queue();

            Role valoRole = ServeurDs.getOrCreateRole(event.getGuild(),"Valorant",Color.RED);
            event.getGuild().addRoleToMember(event.getMember(),valoRole).queue();

            event.reply("Parfait ! Tu as été classé sur le serveur maintenant.").setEphemeral(true).queue();
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

            List<String> suggestions = getPseudosAutocomplete(input);

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
        Guild guild = event.getGuild();
        Member member = event.getMember();

        Role roles = ServeurDs.getOrCreateRole(event.getGuild(),"membre",Color.GRAY);//donne le role membre des l'arrive sur le serve
        event.getGuild().addRoleToMember(event.getMember(), roles).queue();

        TextChannel welcomeChannel = getOrCreateWelcomeChannel(guild);//recup ou crée le channel d'accueil

        if (welcomeChannel != null) {
            welcomeChannel.upsertPermissionOverride(member)
                    .setAllowed(Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY)
                    .queue();

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Bienvenue sur "+event.getGuild().getName()+" !")
                    .setDescription("Pour accéder au reste du serveur, clique sur un bouton ci-dessous pour configurer ton profil.")
                    .setThumbnail(event.getUser().getEffectiveAvatarUrl())
                    .setColor(Color.CYAN);

            welcomeChannel.sendMessage(event.getMember().getAsMention()) // Mentionne le nouveau
                    .setEmbeds(eb.build())
                    .addActionRow(
                            net.dv8tion.jda.api.interactions.components.buttons.Button.success("start_onboarding", "Commencer la configuration"),//revoie au listener de bouton
                            net.dv8tion.jda.api.interactions.components.buttons.Button.success("dont_start_onboarding", "Non merci")//revoie au listener de bouton -> a faire
                    )
                    .queue();
        } else {
            writeLogFile("logs.txt", "Salon d'accueil introuvable sur " + event.getGuild().getName());
        }
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

            // 2. Parcours automatique des jeux dispo sur le serve
            for (String gameName : values) {
                // Récupère la couleur configurée pour CE serveur et CE jeu
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

