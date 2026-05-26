package bot.discordBot.utils.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Conteneur unifié servant de contexte d'exécution pour toutes les commandes du bot.
 * Cette classe encapsule et harmonise les différents types d'événements JDA
 * (commandes Slash ou formulaires Modals) afin d'offrir une interface d'interaction unique pour récupérer
 * les auteurs, les salons, les mentions et envoyer des réponses.
 */
public class CommandContext {

    private final SlashCommandInteractionEvent slashEvent;
    private final ModalInteractionEvent modalEvent;
    private boolean deferred = false;

    /**
     * Construit un contexte à partir d'un événement d'interaction de commande Slash.
     *
     * @param event L'événement JDA d'interaction de commande Slash.
     */
    public CommandContext(SlashCommandInteractionEvent event) {
        this.slashEvent = event;
        this.modalEvent =null;
    }

    /**
     * Construit un contexte à partir d'un événement de soumission de formulaire (Modal).
     *
     * @param modalEvent L'événement JDA d'interaction avec un Modal.
     */
    public CommandContext(ModalInteractionEvent modalEvent) {
        this.modalEvent = modalEvent;
        this.slashEvent = null;
    }

    /**
     * Récupère l'événement de type commande Slash sous-jacent.
     *
     * @return L'objet {@link SlashCommandInteractionEvent}, ou {@code null} si le contexte n'est pas une commande Slash.
     */
    public IReplyCallback getEvent() {
        if (this.slashEvent != null) return slashEvent;
        if (this.modalEvent != null) return modalEvent;
        return null;
    }

    /**
     * Détermine si le contexte actuel provient d'une commande Slash.
     *
     * @return {@code true} s'il s'agit d'une commande Slash, sinon {@code false}.
     */
    public boolean isSlash() {
        return slashEvent != null;
    }

    /**
     * Récupère l'utilisateur Discord (User) à l'origine de l'interaction ou du message.
     *
     * @return L'objet {@link User} représentant l'auteur.
     */
    public User getUser() {
        return slashEvent.getUser();
    }

    /**
     * Récupère l'identifiant unique Discord (Snowflake ID) de l'auteur de la commande.
     *
     * @return L'ID de l'auteur sous forme de chaîne de caractères.
     */
    public String getAuthorId() {
        return getUser().getId();
    }

    /**
     * Récupère le pseudonyme (nom d'utilisateur global ou effectif) de l'auteur de l'interaction.
     *
     * @return Le nom d'utilisateur sous forme de chaîne de caractères.
     */
    public String getAuthorName() {
        return getUser().getName();
    }

    /**
     * Vérifie si l'utilisateur à l'origine de la commande est le propriétaire configuré du bot.
     *
     * @return {@code true} si l'auteur possède l'ID du propriétaire, sinon {@code false}.
     */
    public boolean isBotOwner() {
        // Tu peux adapter l'ID ici ou le tirer de ta config
        return getUser().getId().equals("TON_ID_PROPRIETAIRE");
    }


    /**
     * Envoie une réponse textuelle à l'utilisateur.
     * Gère automatiquement le type d'origine (commande Slash immédiate ou différée).
     *
     * @param message Le message textuel à envoyer.
     * @return Un {@link CompletableFuture} représentant l'action d'envoi asynchrone.
     */
    public CompletableFuture<?> reply(String message) {
        if (deferred) return slashEvent.getHook().sendMessage(message).submit();
        return slashEvent.reply(message).submit();
    }

    /**
     * Envoie un message graphique structuré (Embed) à l'utilisateur.
     * Gère automatiquement le type d'origine (commande Slash immédiate ou différée).
     *
     * @param embed Le constructeur d'embed {@link EmbedBuilder} contenant les données à envoyer.
     * @return Un {@link CompletableFuture} représentant l'action d'envoi asynchrone.
     */
    public CompletableFuture<?> reply(EmbedBuilder embed) {
        if (deferred) return slashEvent.getHook().sendMessageEmbeds(embed.build()).submit();
        return slashEvent.replyEmbeds(embed.build()).submit();
    }

    /**
     * Diffère la réponse pour une commande Slash (Affiche "Le bot réfléchit...").
     * Indispensable pour les traitements ou requêtes API qui durent plus de 3 secondes.
     */
    public void defer() {
        if (isSlash()) {
            slashEvent.deferReply().queue();
            this.deferred = true;
        }
    }

    /**
     * Récupère la valeur d'une option de commande Slash sous forme de chaîne de caractères (String),
     * encapsulée dans un {@link Optional}.
     *
     * @param optionName Le nom de l'option à extraire.
     * @return Un {@link Optional} contenant la valeur textuelle si elle est présente, ou {@link Optional#empty()}.
     */
    public Optional<String> getOptionString(String optionName) {
        if (isSlash()) {
            return Optional.ofNullable(slashEvent.getOption(optionName))
                    .map(OptionMapping::getAsString);
        }
        return Optional.empty();
    }


    /**
     * Récupère directement la valeur textuelle d'une option (alias direct de {@code getOptionString}).
     *
     * @param optionName Le nom de l'option à extraire.
     * @return Un {@link Optional} contenant la valeur textuelle si elle est présente.
     */
    public Optional<String> getOptionStringDirect(String optionName) {
        return getOptionString(optionName);
    }


    /**
     * Extrait l'ensemble des options fournies à la commande Slash sous forme d'une liste de chaînes de caractères.
     *
     * @return Une {@link List} contenant toutes les valeurs des options converties en chaînes, ou une liste vide.
     */
    public List<String> getOptionsAsStringList() {
        if (isSlash()) {
            return slashEvent.getOptions().stream()
                    .map(OptionMapping::getAsString)
                    .collect(Collectors.toList());
        }
        return List.of();
    }


    /**
     * Récupère l'événement de commande Slash d'origine.
     *
     * @return L'objet {@link SlashCommandInteractionEvent}, ou {@code null} si le contexte provient d'un autre événement.
     */
    public SlashCommandInteractionEvent getSlashEvent() {
        return slashEvent;
    }


    /**
     * Récupère l'instance JDA (Java Discord API) active liée à cet événement.
     *
     * @return L'instance {@link JDA} du bot.
     */
    public JDA getJda() {
        return slashEvent.getJDA();
    }

    /**
     * Récupère le salon textuel d'Union dans lequel la commande ou l'interaction a été déclenchée.
     * S'adapte dynamiquement selon que l'événement soit un Modal ou une commande Slash.
     *
     * @return L'instance {@link MessageChannelUnion} représentant le salon.
     */
    public MessageChannelUnion getChannel() {
        if (this.modalEvent != null) { // Si c'est une Modal
            return this.modalEvent.getChannel();
        }else{
            return this.slashEvent.getChannel();
        }
    }

    /**
     * Récupère le salon textuel sous forme d'un conteneur {@link Optional}.
     *
     * @return Un {@link Optional} contenant le {@link MessageChannel} s'il est disponible.
     */
    public java.util.Optional<MessageChannel> getChannelOptional() {
        return java.util.Optional.ofNullable(getChannel());
    }

    /**
     * Récupère l'identifiant unique (ID de flocon / Snowflake ID) du salon actuel.
     *
     * @return La chaîne de caractères représentant l'ID du salon.
     */
    public String getChannelId() {
        return getChannel().getId();
    }

    /**
     * Extrait la liste des utilisateurs mentionnés dans les arguments de l'événement.
     * Gère les options de type "User" dans les commandes Slash.
     *
     * @return Une liste contenant les {@link User} mentionnés.
     */
    public List<User> getMentionedUsers() {
        return slashEvent.getOptionsByType(net.dv8tion.jda.api.interactions.commands.OptionType.USER)
                .stream()
                .map(net.dv8tion.jda.api.interactions.commands.OptionMapping::getAsUser)
                .collect(java.util.stream.Collectors.toList());
    }
}