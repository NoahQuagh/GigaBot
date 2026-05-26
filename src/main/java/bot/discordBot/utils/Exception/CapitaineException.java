package bot.discordBot.utils.Exception;

import bot.discordBot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

/**
 * Exception personnalisée permettant d'intercepter et de gérer les erreurs de droits ou de rôles
 * spécifiques aux capitaines d'équipes au sein du bot.
 * Lorsqu'elle est levée, elle transmet le message d'erreur à la classe parente et envoie
 * immédiatement un Embed d'alerte rouge standardisé dans le salon Discord de l'utilisateur.
 */
public class CapitaineException extends RuntimeException {
    /**
     * Instancie une nouvelle exception de type CapitaineException et envoie un retour visuel à l'utilisateur.
     * Crée et publie un Embed Discord rouge contenant le message d'erreur formaté.
     *
     * @param ctx     Le contexte de la commande Discord (CommandContext) dans lequel l'exception s'est produite.
     * @param message Le message d'erreur explicatif décrivant le problème de droits ou d'action du capitaine.
     */
    public CapitaineException(CommandContext ctx, String message) {
        super(message);
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ "+super.getMessage()+" !")
                .setColor(Color.red);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
