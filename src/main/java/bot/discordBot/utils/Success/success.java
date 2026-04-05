package bot.discordBot.utils.Success;

import bot.discordBot.utils.commands.CommandContext;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class success {
    public static void EventSuccess(CommandContext ctx, String titre, String message){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("✅ "+titre+" :")
                .setDescription(message+".")
                .setColor(Color.GREEN);
        ctx.replyDeferred(embed);
    }
}
