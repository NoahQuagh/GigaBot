package bot.discordBot.commands.Valo;

import bot.discordBot.commands.CommandValo;
import bot.discordBot.utils.Exception.ApiException;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.CompteValoDiscord;
import bot.discordBot.utils.commands.datamanager.DataStructure.TrackedPlayer;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.ArrayList;

import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.Procedure.ApiProcedure.ApiRiotRequete;
import static bot.discordBot.utils.Procedure.ValoDisProcedure.pseudoValoExist;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandValoTrack extends CommandValo {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try {
            String pseudoRaw = ctx.isSlash()
                    ? ctx.getOptionString("pseudotag").orElseThrow(() -> new SyntaxeException(ctx, "/valo setTracker <pseudo>#<tag>"))
                    : (args.length >= 2 ? args[1] : null);


            if (pseudoRaw == null) throw new SyntaxeException(ctx, "/valo rank <pseudo> <tag>");

            String[] tmp = pseudoRaw.split("#");
            String pseudo = tmp[0];
            String tag = tmp[1];


            HttpResponse<String> response = ApiRiotRequete("https://api.henrikdev.xyz/valorant/v2/mmr/eu/" + pseudo + "/" + tag);

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                int currentTier = json.getJSONObject("data").getJSONObject("highest_rank").getInt("tier");


                String channelId = ctx.getChannelId();

                TrackedPlayer newPlayer = new TrackedPlayer(pseudoRaw, channelId, currentTier);


                ArrayList<TrackedPlayer> players = DataManager.loadTrackedPlayer();
                players.add(newPlayer);
                DataManager.saveTrackedPlayer(players);

                ctx.getEvent().getHook().sendMessage("✅ Le joueur **" + pseudo + "** est maintenant suivi dans ce salon !").queue();
                if(!(pseudoValoExist(pseudoRaw))){
                    ArrayList<CompteValoDiscord> compte = DataManager.loadValoDis();
                    if(compte.isEmpty()){
                        compte = new ArrayList<>();
                    }
                    compte.add(new CompteValoDiscord("","",pseudoRaw));
                    DataManager.saveValoDis(compte);
                }
            } else throw new ApiException(ctx, "Joueur introuvable : " + response.statusCode());
        }catch (ApiException e){
            writeLogFile("logs.txt", ctx.getAuthorName() + " | Code : " + Code.AUCUNE_DONNEE_TROUVER);
        }catch (Exception e){
            writeLogFile("logs.txt","Code : "+ Code.ERREUR_API+" : "+e);
            ExceptionDefault(ctx,"Impossible de joindre le serveur de Riot");
        }

    }
}
