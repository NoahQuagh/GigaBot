package botdiscord.gigabot.utils.exception;

import botdiscord.gigabot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;


import java.awt.*;

/**
 * Exception personnalisée permettant d'intercepter et de gérer les erreurs liées au format
 * ou à la validité des dates saisies par les utilisateurs.
 * Lorsqu'elle est levée, elle génère et renvoie un retour visuel d'erreur (Embed rouge)
 * sur Discord pour guider l'utilisateur.
 */
public class DateException extends RuntimeException {
    /**
     * Instancie une exception générique de date incorrecte.
     * Envoie un Embed d'erreur rouge standardisé indiquant simplement que la date est incorrecte.
     *
     * @param ctx Le contexte de la commande Discord (CommandContext) dans lequel l'erreur s'est produite.
     */
    public DateException(CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ Date incorrecte !")
                .setColor(Color.red);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * Instancie une exception de date détaillée avec des explications spécifiques.
     * Construit et publie un Embed rouge complet incluant un titre personnalisé,
     * une description du problème et un champ additionnel (Field) pour guider l'utilisateur sur le format attendu.
     *
     * @param ctx         Le contexte de la commande Discord (CommandContext).
     * @param titre       Le titre principal décrivant l'erreur de date (ex: "Format invalide").
     * @param description Le corps du message détaillant pourquoi la date a été rejetée.
     * @param field1      Le nom du champ de précision (ex: "Format attendu").
     * @param field2      La valeur ou l'exemple lié au champ de précision (ex: "JJ/MM/AAAA").
     */
    public DateException(CommandContext ctx, String titre,String description,String field1,String field2){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ "+titre+" :")
                .setDescription(description+" !")
                .addField(field1,field2+".",false)
                .setColor(Color.RED);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
