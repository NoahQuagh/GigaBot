package botdiscord.gigabot.commandsBot.cmd;

import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import botdiscord.gigabot.utils.commands.CommandExecutor;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class CommandReport implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (!ctx.isSlash()) return;

        TextInput subject = TextInput.create("subject", "Sujet du bug / Joueur", TextInputStyle.SHORT)
                .setPlaceholder("Ex: Bug d'affichage ou Pseudo#Tag")
                .setRequired(true)
                .build();

        TextInput body = TextInput.create("body", "Description détaillée", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Expliquez ici le problème...")
                .setMinLength(10)
                .setRequired(true)
                .build();

        Modal modal = Modal.create("report_modal", "Signalement / Support")
                .addComponents(ActionRow.of(subject), ActionRow.of(body))
                .build();

        if (ctx.getEvent() instanceof net.dv8tion.jda.api.interactions.callbacks.IModalCallback callback) {
            callback.replyModal(modal).queue();
        }
    }
}
