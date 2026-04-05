package bot.discordBot.utils.Exception;

import bot.discordBot.utils.commands.CommandContext;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class EquipeException extends RuntimeException {
    public EquipeException(CommandContext ctx, String message) {
        super(message);
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ "+super.getMessage()+" !")
                .setColor(Color.red);
        ctx.replyDeferred(embed);
    }
}
