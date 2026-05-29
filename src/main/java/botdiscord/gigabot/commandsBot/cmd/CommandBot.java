package botdiscord.gigabot.commandsBot.cmd;

import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import botdiscord.gigabot.utils.commands.CommandExecutor;

import static botdiscord.gigabot.Main.version;

public class CommandBot implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        messageBot(ctx);
    }

    private void messageBot(CommandContext ctx){
        ctx.getEvent().getHook().sendMessage("```                       .,,uod8B8bou,,.\n" +
                "              ..,uod8BBBBBBBBBBBBBBBBRPFT?l!i:.\n" +
                "         ,=m8BBBBBBBBBBBBBBBRPFT?!||||||||||||||\n" +
                "         !...:!TVBBBRPFT||||||||||!!^^\"\"'   ||||\n" +
                "         !.......:!?|||||!!^^\"\"'            ||||\n" +
                "         !.........||||                     ||||\n" +
                "         !.........|||| GigaBot             ||||\n" +
                "         !.........||||                     ||||\n" +
                "         !.........|||| by noaaaah_493      ||||\n" +
                "         !.........||||                     ||||\n" +
                "         !.........|||| v"+version+"              ||||\n" +
                "         `.........||||                    ,||||\n" +
                "          .;.......||||               _.-!!|||||\n" +
                "   .,uodWBBBBb.....||||       _.-!!|||||||||!:'\n" +
                "!YBBBBBBBBBBBBBBb..!|||:..-!!|||||||!iof68BBBBBb.\n" +
                "!..YBBBBBBBBBBBBBBb!!||||||||!iof68BBBBBBRPFT?!::\n" +
                "!....YBBBBBBBBBBBBBBbaaitf68BBBBBBRPFT?!:::::::::\n" +
                "!......YBBBBBBBBBBBBBBBBBBBRPFT?!::::::;:!^\"`;:::\n" +
                "!........YBBBBBBBBBBRPFT?!::::::::::^''...::::::;\n" +
                "`..........YBRPFT?!::::::::::::::::::::::::;iof68bo.\n" +
                "  `..........:::::::::::::::::::::::;iof688888888888b.\n" +
                "    `........::::::::::::::::;iof688888888888888888888b.\n" +
                "      `......:::::::::;iof688888888888888888888888888888b.\n" +
                "        `....:::;iof688888888888888888888888888888888899fT!\n" +
                "          `..::!8888888888888888888888888888888899fT|!^\"'\n" +
                "            `' !!988888888888888888888888899fT|!^\"'\n" +
                "                `!!8888888888888888899fT|!^\"'\n" +
                "                  `!988888888899fT|!^\"'\n" +
                "                    `!9899fT|!^\"'\n" +
                "                      `!^\"'\n```").queue();
    }
}
