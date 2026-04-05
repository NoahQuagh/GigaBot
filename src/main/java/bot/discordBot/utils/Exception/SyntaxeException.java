package bot.discordBot.utils.Exception;

import bot.discordBot.utils.commands.CommandContext;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class SyntaxeException extends RuntimeException {
    public SyntaxeException(CommandContext ctx, String solution) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("⚠️   Attention :")
                .setDescription("Syntaxe incorrecte !")
                .addField("Syntaxe:", "```" + solution + "```")
                .setColor(Color.orange);
        ctx.replyDeferred(embed);
    }
}
