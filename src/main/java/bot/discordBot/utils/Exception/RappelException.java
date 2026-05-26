package bot.discordBot.utils.Exception;

import bot.discordBot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;


import java.awt.*;

/**
 * Exception personnalisée permettant d'intercepter et de gérer les erreurs liées
 * au système de rappels (création de rappels invalides, expiration, ou format incorrect) du bot.
 * Lorsqu'elle est levée, elle génère et renvoie un retour visuel d'erreur standardisé
 * (Embed rouge) sur Discord pour notifier l'utilisateur.
 */
public class RappelException extends RuntimeException{
    /**
     * Instancie une nouvelle exception de type RappelException et envoie un retour visuel à l'utilisateur.
     * Construit et publie un Embed Discord rouge contenant le message d'erreur ou de rappel formaté.
     *
     * @param ctx     Le contexte de la commande Discord (CommandContext) dans lequel l'erreur est survenue.
     * @param message Le message d'erreur ou d'avertissement explicatif décrivant le problème lié au rappel.
     */
    public RappelException(CommandContext ctx,String message) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ "+ message +"!")
                .setColor(Color.red);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
