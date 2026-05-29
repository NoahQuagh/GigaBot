package botdiscord.gigabot.utils.exception;

import botdiscord.gigabot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

/**
 * Exception personnalisée permettant d'intercepter et de gérer les erreurs liées aux API externes.
 * Lorsqu'elle est levée, cette exception génère et envoie automatiquement un message d'erreur
 * visuel (Embed rouge) dans le salon Discord de l'utilisateur concerné.
 */
public class ApiException extends RuntimeException {
    /**
     * Instancie une nouvelle exception d'API et avertit immédiatement l'utilisateur sur Discord.
     * Crée un Embed rouge normalisé contenant les détails du problème technique rencontré.
     *
     * @param ctx     Le contexte de la commande Discord (CommandContext) dans lequel l'erreur est survenue.
     * @param message Le message d'erreur explicatif qui sera affiché à l'utilisateur dans l'Embed.
     */
    public ApiException(CommandContext ctx, String message) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌   Erreur API :")
                .setDescription(message+".")
                .setColor(Color.red);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
