package botdiscord.gigabot.utils.exception;

import botdiscord.gigabot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;


import java.awt.*;

/**
 * Exception personnalisée permettant d'intercepter et de gérer les erreurs liées
 * à la gestion des équipes (création, modification, ou intégration de membres) dans le bot.
 * Lorsqu'elle est levée, elle transmet le message à la classe parente et génère
 * immédiatement un Embed d'erreur rouge standardisé envoyé dans le salon Discord de l'utilisateur.
 */
public class EquipeException extends RuntimeException {
    /**
     * Instancie une nouvelle exception de type EquipeException et envoie un retour visuel à l'utilisateur.
     * Construit et publie un Embed Discord rouge contenant le message d'erreur formaté.
     *
     * @param ctx     Le contexte de la commande Discord (CommandContext) dans lequel l'erreur est survenue.
     * @param message Le message d'erreur explicatif décrivant le problème lié à l'équipe.
     */
    public EquipeException(CommandContext ctx, String message) {
        super(message);
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ "+super.getMessage()+" !")
                .setColor(Color.red);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
