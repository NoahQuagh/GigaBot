package bot.discordBot.utils.Exception;

import bot.discordBot.utils.commands.CommandContext;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public class ApiException extends RuntimeException {
    public ApiException(CommandContext ctx, String message) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌   Erreur API :")
                .setDescription(message+".")
                .setColor(Color.red);
        ctx.replyDeferred(embed);
    }
}
