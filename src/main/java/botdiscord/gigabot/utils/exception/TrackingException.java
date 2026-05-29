package botdiscord.gigabot.utils.exception;

import botdiscord.gigabot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class TrackingException extends RuntimeException {
    public TrackingException(CommandContext ctx,String message) {
        super(message);
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ "+super.getMessage()+" !")
                .setColor(Color.red);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }

    public TrackingException(String message) {
        super(message);
    }
}
