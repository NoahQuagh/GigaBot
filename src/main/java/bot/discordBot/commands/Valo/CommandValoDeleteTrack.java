package bot.discordBot.commands.Valo;

import bot.discordBot.commands.CommandValo;
import bot.discordBot.utils.Exception.NoDataFoundException;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;

import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.Success.success.EventSuccess;
import static bot.discordBot.utils.commands.datamanager.DataStructure.TrackedPlayer.RemoveTrackerByPseudoRaw;
import static bot.discordBot.utils.commands.datamanager.DataStructure.TrackedPlayer.ceSuiviExiste;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandValoDeleteTrack extends CommandValo {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try {
            String pseudoRaw = ctx.isSlash()
                    ? ctx.getOptionString("pseudotag").orElseThrow(() -> new SyntaxeException(ctx, "/valo rank <pseudo#tag>"))
                    : (args.length >= 2 ? args[1] : null);

            if (ceSuiviExiste(pseudoRaw)) {
                if (RemoveTrackerByPseudoRaw(pseudoRaw)) {
                    EventSuccess(ctx, "Tracking annuler", "Le tracking Valorant du joueur **" + pseudoRaw + "** a été supprimé");
                } else throw new NoDataFoundException(ctx, "Impossible de supprimer le tracking du joueur **" + pseudoRaw+"**");
            }else throw new NoDataFoundException(ctx, "Impossible de supprimer le tracking du joueur **" + pseudoRaw+"**");

        }catch (NoDataFoundException e){
            writeLogFile("logs.txt","Code : "+ Code.AUCUNE_DONNEE_TROUVER+" : "+e);
        }catch (Exception e){
            writeLogFile("logs.txt","Code : "+ Code.ERREUR_API+" : "+e);
            ExceptionDefault(ctx,"Impossible de joindre le serveur de Riot");
        }
    }
}
