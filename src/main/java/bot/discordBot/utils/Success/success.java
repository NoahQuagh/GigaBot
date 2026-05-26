package bot.discordBot.utils.Success;

import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataStructure.TrackedPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.time.OffsetDateTime;

import static bot.discordBot.Main.jda;

/**
 * Gestionnaire des annonces et des messages de succès pour le bot Discord.
 * Cette classe permet de formaliser et d'envoyer des retours visuels (Embeds)
 * aux utilisateurs lors de la réussite d'une commande ou d'un événement de jeu.
 */
public class success {

    /**
     * Envoie un message d'embed standardisé pour confirmer la réussite d'une action ou commande.
     * Le message utilise une mise en forme verte avec une icône de coche.
     *
     * @param ctx     Le contexte de la commande Discord (CommandContext) ayant déclenché l'événement.
     * @param titre   Le titre principal à afficher dans l'embed de succès.
     * @param message Le corps du texte détaillant l'action réussie.
     */
    public static void EventSuccess(CommandContext ctx, String titre, String message){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("✅ "+titre+" :")
                .setDescription(message+".")
                .setColor(Color.GREEN);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * Envoie une annonce publique et stylisée dans un salon textuel spécifique
     * lorsqu'un joueur suivi (tracked) atteint un nouveau record de rang (Peak Rank).
     *
     * @param api        L'instance active de l'API JDA du bot.
     * @param player     L'objet contenant les informations du joueur suivi (ID salon, pseudo, etc.).
     * @param newRankTxt Le libellé textuel du nouveau rang atteint (ex: "Immortal 1").
     * @param rankImg    L'URL de l'image ou de l'icône représentative du nouveau rang.
     */
    public static void sendRankupMessage(JDA api, TrackedPlayer player, String newRankTxt, String rankImg){
        TextChannel channel = jda.getTextChannelById(player.getChannelId());
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🚀  NOUVEAU PEAK RANK !")
                .setDescription("Félicitations à **" + player.getPseudoRaw() + "** qui vient de dépasser son record ! **W dans le chat !!!**")
                .addField("Nouveau Rang", "**" + newRankTxt + "**", false)
                .setThumbnail(rankImg)
                .setColor(Color.GREEN)
                .setTimestamp(OffsetDateTime.now());
        channel.sendMessageEmbeds(embed.build()).queue();
    }
}
