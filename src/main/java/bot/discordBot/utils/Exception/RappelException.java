package bot.discordBot.utils.Exception;

import bot.discordBot.utils.commands.CommandContext;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public class RappelException extends RuntimeException{
    public RappelException(CommandContext ctx,String message) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ "+ message +"!")
                .setColor(Color.red);
        ctx.replyDeferred(embed);
    }
}
