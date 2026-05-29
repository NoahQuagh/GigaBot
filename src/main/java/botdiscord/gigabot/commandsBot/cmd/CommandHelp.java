package botdiscord.gigabot.commandsBot.cmd;

import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import botdiscord.gigabot.utils.commands.CommandExecutor;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class CommandHelp implements CommandExecutor {

    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        sendDefaultHelp(ctx);
    }

    private void sendDefaultHelp(CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("**AIDE** :")
                .addField("", "- Utiliser ```/doc``` pour lire la documentation.",false)
                .setColor(Color.GREEN);
        ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
