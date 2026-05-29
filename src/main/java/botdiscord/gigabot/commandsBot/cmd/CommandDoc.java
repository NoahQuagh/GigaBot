package botdiscord.gigabot.commandsBot.cmd;

import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.log_DB;
import bot.discordBot.utils.commands.*;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import botdiscord.gigabot.utils.commands.CommandExecutor;

import java.sql.SQLException;

public class CommandDoc implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) throws SQLException {
        if (ctx.isSlash()) ctx.defer();
        log_DB logs = new log_DB();
        try {
            ctx.getEvent().getHook().sendMessage("☝️🤓 Voici la documentation : [GigaDoc](https://www.noahquagh.com/pages/gigadoc.html)");
            logs.writeLog(LevelLog.OK, CommandDoc.class.getName(),ctx.getAuthorName()+" a demandé la documentation");
        } catch (Exception e) {
            logs.writeLog(LevelLog.OK, CommandDoc.class.getName(),ctx.getAuthorName()+" a demandé la documentation mais une erreur c'est produite");
        }
    }
}
