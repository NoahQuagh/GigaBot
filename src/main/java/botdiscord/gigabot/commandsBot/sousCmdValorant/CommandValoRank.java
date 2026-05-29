package botdiscord.gigabot.commandsBot.sousCmdValorant;

import botdiscord.gigabot.commandsBot.cmd.CommandValo;
import botdiscord.gigabot.utils.API.ValorantApi;
import botdiscord.gigabot.utils.DB.Valo_Dis_DB;
import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.log_DB;
import botdiscord.gigabot.utils.exception.ApiException;
import botdiscord.gigabot.utils.exception.JoueurException;
import botdiscord.gigabot.utils.exception.SyntaxeException;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;
import java.net.http.HttpResponse;
import java.sql.SQLException;

import static botdiscord.gigabot.utils.exception.DefaultException.ExceptionDefault;

public class CommandValoRank extends CommandValo {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) throws SQLException {
        ctx.defer();
        ValorantApi valorantApi=new ValorantApi();
        log_DB logs=new log_DB();
        Valo_Dis_DB valoDisDb=new Valo_Dis_DB();
        try {
            String pseudoRaw = ctx.isSlash()
                    ? ctx.getOptionString("pseudotag").orElseThrow(() -> new SyntaxeException(ctx, "/valo rank <pseudo#tag>"))
                    : (args.length >= 2 ? args[1] : null);


            if (pseudoRaw == null) throw new SyntaxeException(ctx, "/valo rank <pseudo#tag>");


            String[] tmp= pseudoRaw.split("#");
            String pseudo=tmp[0];
            String tag = tmp[1];

            String url = "https://api.henrikdev.xyz/valorant/v1/mmr/eu/" + pseudo + "/" + tag;
            HttpResponse<String> response = valorantApi.request(ctx,url);


            if (response == null) throw new ApiException(ctx,"L'appel à l'API Riot a échoué");
            if (response.statusCode() != 200) throw new ApiException(ctx,"Joueur introuvable : "+response.statusCode());



            JSONObject json = new JSONObject(response.body());

            if (!json.has("data") || json.isNull("data")) throw new JoueurException(ctx,"Impossible de lire les données du joueur");

            JSONObject data = json.getJSONObject("data");

            String rank = data.optString("currenttierpatched", "Inconnu");
            int mmr = data.optInt("ranking_in_tier", 0);

            String iconUrl = "";
            if (data.has("images")) {
                iconUrl = data.getJSONObject("images").optString("small", "");
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(pseudo + "#" + tag)
                    .setTitle("Rank :")
                    .setDescription("## " + rank + " : " + mmr + "  RR")
                    .setColor(Color.GREEN);

            if (!iconUrl.isEmpty()) embed.setThumbnail(iconUrl);

            ctx.getEvent().getHook().sendMessageEmbeds(embed.build()).queue();

            if(!(valorantApi.valorantPseudoExiste(pseudoRaw))){
                valoDisDb.setValoDis(pseudoRaw);
            }

        }catch (SyntaxeException e) {
            logs.writeLog(LevelLog.ERR, CommandValoRank.class.getName(),ctx.getAuthorName() + " erreur API : "+e);
        }catch(ApiException | JoueurException e){
            logs.writeLog(LevelLog.ERR, CommandValoRank.class.getName(),ctx.getAuthorName() + " aucune données trouver : "+e);
        } catch (Exception e) {
            logs.writeLog(LevelLog.ERR, CommandValoRank.class.getName(),ctx.getAuthorName() + " erreur API : "+e);
            ExceptionDefault(ctx,"Impossible de joindre les serveurs de Riot");
        }
    }
}