package botdiscord.gigabot.utils.exception;

import botdiscord.gigabot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;


import java.awt.*;

/**
 * Exception personnalisée permettant d'intercepter et de gérer les situations où
 * les données recherchées (profil, statistiques, historique) sont introuvables.
 * Lorsqu'elle est levée, elle génère et renvoie un retour visuel d'erreur standardisé
 * (Embed rouge) sur Discord pour notifier l'utilisateur.
 */
public class NoDataFoundException extends RuntimeException {
    /**
     * Instancie une exception générique de données introuvables.
     * Envoie un Embed d'erreur rouge standardisé indiquant simplement qu'aucune donnée n'a été trouvée.
     *
     * @param ctx Le contexte de la commande Discord (CommandContext) dans lequel l'erreur s'est produite.
     */
    public NoDataFoundException(CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌   Aucune donnée trouvée !")
                .setColor(Color.red);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * Instancie une exception de données introuvables avec un message explicatif personnalisé.
     * Construit et publie un Embed rouge incluant un texte de description pour apporter
     * plus de contexte sur la nature de la recherche infructueuse.
     *
     * @param ctx     Le contexte de la commande Discord (CommandContext).
     * @param message Le message d'erreur ou de précision décrivant ce qui n'a pas pu être trouvé.
     */
    public NoDataFoundException(CommandContext ctx,String message) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌   Aucune donnée trouvée !")
                .setDescription(message+".")
                .setColor(Color.red);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
