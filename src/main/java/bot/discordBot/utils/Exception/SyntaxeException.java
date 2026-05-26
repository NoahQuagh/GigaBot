package bot.discordBot.utils.Exception;

import bot.discordBot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

/**
 * Exception personnalisée permettant d'intercepter et de gérer les erreurs de syntaxe
 * dans les arguments ou les options des commandes saisies par les utilisateurs.
 * Lorsqu'elle est levée, elle génère et renvoie un avertissement visuel (Embed orange)
 * sur Discord indiquant la bonne syntaxe à utiliser pour guider l'utilisateur.
 */
public class SyntaxeException extends RuntimeException {
    /**
     * Instancie une nouvelle exception de type SyntaxeException et envoie un retour visuel d'aide à l'utilisateur.
     * Construit et publie un Embed Discord orange contenant un bloc de code mettant en évidence
     * l'exemple de syntaxe correct ou la solution attendue.
     *
     * @param ctx      Le contexte de la commande Discord (CommandContext) dans lequel l'erreur est survenue.
     * @param solution L'exemple de commande ou la syntaxe correcte (ex: "/commande [option]") à afficher.
     */
    public SyntaxeException(CommandContext ctx, String solution) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("⚠️   Attention :")
                .setDescription("Syntaxe incorrecte !")
                .addField("Syntaxe:", "```" + solution + "```",false)
                .setColor(Color.orange);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
