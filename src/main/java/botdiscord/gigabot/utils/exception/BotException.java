package botdiscord.gigabot.utils.exception;

import botdiscord.gigabot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class BotException extends RuntimeException {
    public BotException(String message) {
        super(message);
    }

    public BotException(CommandContext ctx, String message) {
        super(message);
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ "+super.getMessage()+" !")
                .setColor(Color.red);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
