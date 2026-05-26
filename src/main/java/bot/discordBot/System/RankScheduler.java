package bot.discordBot.System;

import bot.discordBot.utils.Exception.ApiException;
import bot.discordBot.utils.Exception.JoueurException;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.CompteValoDiscord;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static bot.discordBot.System.ServeurDs.getOrCreateRole;
import static bot.discordBot.utils.Procedure.ApiProcedure.*;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class RankScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int currentIndex = 0;

    public void startUpdating(JDA jda) {
        scheduler.scheduleAtFixedRate(() -> {
            ArrayList<CompteValoDiscord> comptes = DataManager.loadValoDis();
            if (comptes == null || comptes.isEmpty()) return;


            if (currentIndex >= comptes.size()) currentIndex = 0;

            CompteValoDiscord compte = comptes.get(currentIndex);
            updatePlayerRank(jda, compte);

            currentIndex++;
        }, 0, 20, TimeUnit.SECONDS);
    }

    private void updatePlayerRank(JDA jda, CompteValoDiscord compte) {
        String[] parts = compte.getPseudoValo().split("#");
        if (parts.length < 2) return;

        // 2. Appel API (Réutilise ta méthode ApiRiotRequete)
        // Note: Il faudra adapter ApiRiotRequete pour qu'elle puisse marcher sans "event"
        String url = "https://api.henrikdev.xyz/valorant/v1/mmr/eu/" + parts[0] + "/" + parts[1];
        HttpResponse<String> response =  ApiRiotRequete(url);

        if (response == null || response.statusCode() != 200) {
            writeLogFile("logs.txt", "Échec API ou joueur introuvable pour : " + compte.getPseudoValo());
            return;
        }

        JSONObject json = new JSONObject(response.body());

        if (!json.has("data") || json.isNull("data")){writeLogFile("logs.txt","impossible de lire les données de "+compte);return;}

        JSONObject data = json.getJSONObject("data");

        int rankNb = data.optInt("currenttier", 0);
        String rank = getRankTxtByInt(rankNb);
        java.awt.Color rankColor = getColorRankByRankTxt(rank);

        for (Guild guild : jda.getGuilds()) {

            guild.retrieveMemberById(compte.getIdDiscord()).queue(member -> {

                // 4. On récupère ou crée le rôle du nouveau rang sur ce serveur
                Role newRankRole = getOrCreateRole(guild, rank, rankColor);

                if (newRankRole != null) {
                    if (!member.getRoles().contains(newRankRole)) {
                        replaceRankRole(member, newRankRole);
                        writeLogFile("logs.txt", "Update Rank: " + compte.getPseudoValo() + " est maintenant " + rank);
                    }
                }

            }, throwable -> {
                writeLogFile("logs.txt", compte+" n'est plus ce serveur");
            });
        }
    }

    private void replaceRankRole(Member member, Role newRankRole) {
        Guild guild = member.getGuild();

        // Liste de tous les noms de rangs possibles (Fer, Bronze, Argent, Or, etc.)
        List<String> allPossibleRanks = List.of("Iron", "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Ascendant", "Immortal", "Radiant");

        // On retire tous les anciens rôles de rank avant de mettre le nouveau
        for (Role role : member.getRoles()) {
            for (String rankName : allPossibleRanks) {
                if (role.getName().contains(rankName) && !role.equals(newRankRole)) {
                    guild.removeRoleFromMember(member, role).complete();
                }
            }
        }

        // On ajoute le nouveau
        guild.addRoleToMember(member, newRankRole).queue();
    }
}
