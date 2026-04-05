package bot.discordBot.utils.Exception;

import bot.discordBot.utils.commands.CommandContext;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class DefaultException {
    public static void ExceptionDefault(CommandContext ctx, String message){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ "+message+" !")
                .setColor(Color.red);
        ctx.replyDeferred(embed);
    }
}
