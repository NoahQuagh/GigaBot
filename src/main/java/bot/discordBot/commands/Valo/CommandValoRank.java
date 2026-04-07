package bot.discordBot.commands.Valo;

import bot.discordBot.Main;
import bot.discordBot.commands.CommandValo;
import bot.discordBot.utils.Exception.ApiException;
import bot.discordBot.utils.Exception.JoueurException;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.CompteValoDiscord;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;

import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.Procedure.ApiProcedure.ApiRiotRequete;
import static bot.discordBot.utils.Procedure.ValoDisProcedure.pseudoValoExist;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandValoRank extends CommandValo {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try {
            String pseudoRaw = ctx.isSlash()
                    ? ctx.getOptionString("pseudotag").orElseThrow(() -> new SyntaxeException(ctx, "/valo rank <pseudo#tag>"))
                    : (args.length >= 2 ? args[1] : null);


            if (pseudoRaw == null) throw new SyntaxeException(ctx, "/valo rank <pseudo#tag>");


            String[] tmp= pseudoRaw.split("#");
            String pseudo=tmp[0];
            String tag = tmp[1];

            String url = "https://api.henrikdev.xyz/valorant/v1/mmr/eu/" + pseudo + "/" + tag;
            HttpResponse<String> response = ApiRiotRequete(ctx, url);


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

            ctx.replyDeferred(embed);

            if(!(pseudoValoExist(pseudoRaw))){
                ArrayList<CompteValoDiscord> compte = DataManager.loadValoDis();
                if(compte.isEmpty()){
                    compte = new ArrayList<>();
                }
                compte.add(new CompteValoDiscord("","",pseudoRaw));
                DataManager.saveValoDis(compte);
            }

        }catch (SyntaxeException e) {
            writeLogFile("logs.txt", ctx.getAuthorName() + " | Code : " + Code.ERREUR_API);
        }catch(ApiException | JoueurException e){
            writeLogFile("logs.txt", ctx.getAuthorName() + " | Code : " + Code.AUCUNE_DONNEE_TROUVER);
        } catch (Exception e) {
            writeLogFile("logs.txt","Code : "+ Code.ERREUR_API+" : "+e);
            ExceptionDefault(ctx,"Impossible de joindre le serveur de Riot");
        }
    }
}