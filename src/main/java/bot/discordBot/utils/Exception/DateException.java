package bot.discordBot.utils.Exception;

import bot.discordBot.utils.commands.CommandContext;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class DateException extends RuntimeException {
    public DateException(CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ Date incorrecte !")
                .setColor(Color.red);
        ctx.replyDeferred(embed);
    }
    public DateException(CommandContext ctx, String titre,String description,String field1,String field2){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ "+titre+" :")
                .setDescription(description+" !")
                .addField(field1,field2+".")
                .setColor(Color.RED);
        ctx.replyDeferred(embed);
    }
}
