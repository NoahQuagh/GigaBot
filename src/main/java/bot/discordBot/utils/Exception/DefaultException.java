package bot.discordBot.utils.Exception;

import bot.discordBot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;


import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitaire de gestion des exceptions et erreurs par défaut.
 * Contrairement aux autres exceptions du package, elle ne lève pas d'interruption (RuntimeException)
 * mais propose une méthode statique pour envoyer rapidement un retour visuel d'erreur standard
 * (Embed rouge) à un utilisateur sur Discord.
 */
public class DefaultException {
    /**
     * Envoie un message d'erreur générique et standardisé sous forme d'Embed Discord.
     * Utile pour intercepter les cas d'erreurs communs qui ne nécessitent pas de bloquer
     * le thread d'exécution du bot.
     *
     * @param ctx     Le contexte de la commande Discord (CommandContext) à l'origine de la demande.
     * @param message Le message d'erreur ou d'avertissement textuel à afficher dans l'Embed.
     */
    public static void ExceptionDefault(CommandContext ctx, String message){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ "+message+" !")
                .setColor(Color.red);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
