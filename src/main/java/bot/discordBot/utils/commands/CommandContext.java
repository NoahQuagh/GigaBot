// bot/discordBot/utils/commands/CommandContext.java
package bot.discordBot.utils.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommandContext {

    private final MessageCreateEvent messageEvent;
    private final SlashCommandCreateEvent slashEvent;

    // Constructeur pour les anciens messages
    public CommandContext(MessageCreateEvent event) {
        this.messageEvent = event;
        this.slashEvent = null;
    }

    // Constructeur pour les slash commands
    public CommandContext(SlashCommandCreateEvent event) {
        this.slashEvent = event;
        this.messageEvent = null;
    }

    public boolean isSlash() {
        return slashEvent != null;
    }

    // --- Méthodes unifiées que tes commandes vont utiliser ---

    public DiscordApi getApi() {
        if (isSlash()) return slashEvent.getApi();
        return messageEvent.getApi();
    }

    public Optional<TextChannel> getChannel() {
        if (isSlash()) return slashEvent.getSlashCommandInteraction().getChannel();
        return Optional.of(messageEvent.getChannel());
    }

    public User getUser() {
        if (isSlash()) return slashEvent.getSlashCommandInteraction().getUser();
        return messageEvent.getMessageAuthor().asUser().orElseThrow();
    }

    public String getAuthorName() {
        if (isSlash()) return getUser().getName();
        return messageEvent.getMessageAuthor().getDisplayName();
    }

    public String getAuthorId() {
        if (isSlash()) return getUser().getIdAsString();
        return messageEvent.getMessageAuthor().getIdAsString();
    }

    // Répondre : ephemeral = visible uniquement par l'auteur (utile pour les erreurs)
    public CompletableFuture<?> reply(EmbedBuilder embed) {
        if (isSlash()) {
            SlashCommandInteraction interaction = slashEvent.getSlashCommandInteraction();
            return interaction.createImmediateResponder()
                    .addEmbed(embed)
                    .respond();
        }
        return getChannel()
                .map(c -> c.sendMessage(embed))
                .orElse(CompletableFuture.completedFuture(null));
    }

    public CompletableFuture<?> reply(String message) {
        if (isSlash()) {
            SlashCommandInteraction interaction = slashEvent.getSlashCommandInteraction();
            return interaction.createImmediateResponder()
                    .setContent(message)
                    .respond();
        }
        return getChannel()
                .map(c -> c.sendMessage(message))
                .orElse(CompletableFuture.completedFuture(null));
    }

    public CompletableFuture<?> replyEphemeral(EmbedBuilder embed) {
        if (isSlash()) {
            return slashEvent.getSlashCommandInteraction()
                    .createImmediateResponder()
                    .addEmbed(embed)
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
        return reply(embed);
    }

    public Optional<User> getMentionedUser(String optionName) {
        if (isSlash()) {
            return slashEvent.getSlashCommandInteraction()
                    .getOptions()       // niveau 1 : le subcommand (ex: "teamInvite")
                    .stream()
                    .findFirst()
                    .flatMap(sub -> sub.getOptionByName(optionName)) // niveau 2 : l'option
                    .flatMap(SlashCommandInteractionOption::getUserValue);
        }
        return messageEvent.getMessage()
                .getMentionedUsers()
                .stream()
                .findFirst();
    }
    public Optional<String> getOptionString(String optionName) {
        if (isSlash()) {
            return slashEvent.getSlashCommandInteraction()
                    .getOptions() // liste des subcommands
                    .stream()
                    .findFirst() // le subcommand actif (ex: "createTeam")
                    .flatMap(sub -> sub.getOptionByName(optionName)) // ← descend dans le subcommand
                    .flatMap(SlashCommandInteractionOption::getStringValue);
        }
        return Optional.empty();
    }
    public List<User> getMentionedUsers() {
        if (isSlash()) {
            return slashEvent.getSlashCommandInteraction()
                    .getOptions()
                    .stream()
                    .map(SlashCommandInteractionOption::getUserValue)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        return messageEvent.getMessage().getMentionedUsers();
    }
    public boolean isOwner() {
        if (isSlash()) {
            // Récupère l'application info pour comparer avec l'owner
            return slashEvent.getApi()
                    .getOwnerId()
                    .map(ownerId -> ownerId == getUser().getId())
                    .orElse(false);
        }
        return messageEvent.getMessageAuthor().isBotOwner();
    }
    private boolean deferred = false;

    public void defer() {
        if (isSlash()) {
            slashEvent.getSlashCommandInteraction()
                    .respondLater() // envoie l'acknowledge
                    .join();
            deferred = true;
        }
    }

    public CompletableFuture<?> replyDeferred(EmbedBuilder embed) {
        if (isSlash() && deferred) {
            return slashEvent.getSlashCommandInteraction()
                    .createFollowupMessageBuilder()
                    .addEmbed(embed)
                    .send();
        }
        return reply(embed); // fallback normal
    }

    public CompletableFuture<?> replyDeferred(String message) {
        if (isSlash() && deferred) {
            return slashEvent.getSlashCommandInteraction()
                    .createFollowupMessageBuilder()
                    .setContent(message)
                    .send();
        }
        return reply(message);
    }
    public SlashCommandCreateEvent getSlashEvent() {
        return slashEvent;
    }
    public Optional<String> getOptionStringDirect(String optionName) {
        if (isSlash()) {
            return slashEvent.getSlashCommandInteraction()
                    .getOptionByName(optionName)  // ← niveau racine, pas de subcommand
                    .flatMap(SlashCommandInteractionOption::getStringValue);
        }
        return Optional.empty();
    }
}
