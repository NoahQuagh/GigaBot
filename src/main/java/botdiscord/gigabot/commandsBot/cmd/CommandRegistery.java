package botdiscord.gigabot.commandsBot.cmd;

import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import botdiscord.gigabot.utils.commands.CommandExecutor;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class CommandRegistery implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        TextInput pseudo = TextInput.create("valo_pseudo", "Pseudo Valorant (avec #tag)", TextInputStyle.SHORT)
                .setPlaceholder("Pseudo#1234")
                .setRequired(true)
                .build();

        Modal modal = Modal.create("onboarding_valo", "Ton Pseudo Valorant")
                .addComponents(ActionRow.of(pseudo))
                .build();

        ctx.getSlashEvent().replyModal(modal).queue();
    }
}
