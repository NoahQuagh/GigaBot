package bot.discordBot.utils.Exception;

import bot.discordBot.utils.commands.CommandContext;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌   Aucune donnée trouvée !")
                .setColor(Color.red);
        ctx.replyDeferred(embed);
    }
    public NoDataFoundException(CommandContext ctx,String message) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌   Aucune donnée trouvée !")
                .setDescription(message+".")
                .setColor(Color.red);
        ctx.replyDeferred(embed);
    }
}
