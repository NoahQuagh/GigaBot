package botdiscord.gigabot.commandsBot.sousCmdValorant;

import botdiscord.gigabot.commandsBot.cmd.CommandValo;
import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.log_DB;
import botdiscord.gigabot.utils.DB.tracker_joueur_DB;
import botdiscord.gigabot.utils.exception.NoDataFoundException;
import botdiscord.gigabot.utils.exception.SyntaxeException;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;

import java.sql.SQLException;

import static botdiscord.gigabot.utils.exception.DefaultException.ExceptionDefault;
import static botdiscord.gigabot.utils.success.success.EventSuccess;

public class CommandValoDeleteTrack extends CommandValo {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) throws SQLException {
        if (ctx.isSlash()) ctx.defer();
        tracker_joueur_DB tracker=new tracker_joueur_DB();
        log_DB logs=new log_DB();
        try {
            String pseudoRaw = ctx.isSlash()
                    ? ctx.getOptionString("pseudotag").orElseThrow(() -> new SyntaxeException(ctx, "/valo rank <pseudo#tag>"))
                    : (args.length >= 2 ? args[1] : null);

            if (tracker.playerIsTracked(pseudoRaw)) {
                if (tracker.deleteTrackingByPlayerName(ctx,pseudoRaw)>0) {
                    EventSuccess(ctx, "Tracking annuler", "Le tracking Valorant du joueur **" + pseudoRaw + "** a été supprimé");
                } else throw new NoDataFoundException(ctx, "Impossible de supprimer le tracking du joueur **" + pseudoRaw+"**");
            }else throw new NoDataFoundException(ctx, "Impossible de supprimer le tracking du joueur **" + pseudoRaw+"**");

        }catch (NoDataFoundException e){
            logs.writeLog(LevelLog.ERR, CommandValoDeleteTrack.class.getName(),"Erreur critique aucune données trouver : " + e.getMessage());
        }catch (Exception e){
            logs.writeLog(LevelLog.ERR,CommandValoTrack.class.getName(),"Erreur critique lors de la suppression du tracking : " + e.getMessage());
            ExceptionDefault(ctx,"Impossible de joindre le serveur de Riot");
        }
    }
}
