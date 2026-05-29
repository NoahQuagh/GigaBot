package botdiscord.gigabot.commandsBot.sousCmdValorant;

import botdiscord.gigabot.commandsBot.cmd.CommandValo;
import botdiscord.gigabot.utils.API.ValorantApi;
import botdiscord.gigabot.utils.DB.Valo_Dis_DB;
import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.log_DB;
import botdiscord.gigabot.utils.DB.tracker_joueur_DB;
import botdiscord.gigabot.utils.exception.ApiException;
import botdiscord.gigabot.utils.exception.SyntaxeException;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.sql.SQLException;

import static botdiscord.gigabot.utils.exception.DefaultException.ExceptionDefault;



public class CommandValoTrack extends CommandValo {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) throws SQLException {
        if (ctx.isSlash()) ctx.defer();
        ValorantApi valorantApi=new ValorantApi();
        tracker_joueur_DB tracker=new tracker_joueur_DB();
        Valo_Dis_DB valoDisDb=new Valo_Dis_DB();
        log_DB logs=new log_DB();
        try {
            String pseudoRaw = ctx.isSlash()
                    ? ctx.getOptionString("pseudotag").orElseThrow(() -> new SyntaxeException(ctx, "/valo setTracker <pseudo>#<tag>"))
                    : (args.length >= 2 ? args[1] : null);


            if (pseudoRaw == null) throw new SyntaxeException(ctx, "/valo rank <pseudo> <tag>");

            String[] tmp = pseudoRaw.split("#");
            String pseudo = tmp[0];
            String tag = tmp[1];


            HttpResponse<String> response = valorantApi.request("https://api.henrikdev.xyz/valorant/v2/mmr/eu/" + pseudo + "/" + tag);

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                int currentTier = json.getJSONObject("data").getJSONObject("highest_rank").getInt("tier");


                String channelId = ctx.getChannelId();

                tracker.setTracking(ctx,pseudoRaw,channelId,currentTier);

                ctx.getEvent().getHook().sendMessage("✅ Le joueur **" + pseudo + "** est maintenant suivi dans ce salon !").queue();

                valoDisDb.setValoDis(pseudoRaw);
            } else throw new ApiException(ctx, "Joueur introuvable : " + response.statusCode());
        }catch (ApiException e){
            logs.writeLog(LevelLog.ERR, CommandValoTrack.class.getName(),"Obtention des données du joueur échoué : "+e);
        }catch (Exception e){
            logs.writeLog(LevelLog.ERR, CommandValoTrack.class.getName(),"Impossible de se connecter à l'API Riot : "+e);
            ExceptionDefault(ctx,"Impossible de joindre les serveurs de Riot");
        }

    }
}
