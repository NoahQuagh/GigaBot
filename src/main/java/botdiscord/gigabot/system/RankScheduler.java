package botdiscord.gigabot.system;

import botdiscord.gigabot.utils.API.ValorantApi;
import botdiscord.gigabot.utils.DB.Valo_Dis_DB;
import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.log_DB;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class RankScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int currentIndex = 0;
    private log_DB logs;
    private Valo_Dis_DB valoDisDb;
    private ValorantApi valorantApi;
    private ServeurDs serveurDs;

    public RankScheduler() throws SQLException {
        this.logs=new log_DB();
        this.valoDisDb= new Valo_Dis_DB();
        this.valorantApi=new ValorantApi();
        this.serveurDs=new ServeurDs();
    }

    public void startUpdating(JDA jda) {
        scheduler.scheduleAtFixedRate(() -> {
            ArrayList<String> pseudos = valoDisDb.getValoPseudo();
            if (pseudos == null || pseudos.isEmpty()) return;


            if (currentIndex >= pseudos.size()) currentIndex = 0;

            String pseudo = pseudos.get(currentIndex);
            updatePlayerRank(jda, pseudo);

            currentIndex++;
        }, 0, 20, TimeUnit.SECONDS);
    }

    private void updatePlayerRank(JDA jda, String pseudo) {
        String[] parts = pseudo.split("#");
        if (parts.length < 2) return;

        String url = "https://api.henrikdev.xyz/valorant/v1/mmr/eu/" + parts[0] + "/" + parts[1];
        HttpResponse<String> response =  valorantApi.request(url);

        if (response == null || response.statusCode() != 200) {
            logs.writeLog(LevelLog.ERR,RankScheduler.class.getName(),"Échec API ou joueur introuvable pour : " + pseudo);
            return;
        }

        JSONObject json = new JSONObject(response.body());

        if (!json.has("data") || json.isNull("data")){logs.writeLog(LevelLog.ERR,RankScheduler.class.getName(),"impossible de lire les données de "+pseudo);return;}

        JSONObject data = json.getJSONObject("data");

        int rankNb = data.optInt("currenttier", 0);
        String rank = valorantApi.getRankTxtByInt(rankNb);
        java.awt.Color rankColor = valorantApi.getColorRankByRankTxt(rank);

        for (Guild guild : jda.getGuilds()) {

            ArrayList<String> liste = valoDisDb.getValoDisByValoPseudo(pseudo);

            guild.retrieveMemberById(liste.getFirst()).queue(member -> {

                // 4. On récupère ou crée le rôle du nouveau rang sur ce serveur
                Role newRankRole = serveurDs.getOrCreateRole(guild, rank, rankColor);

                if (newRankRole != null) {
                    if (!member.getRoles().contains(newRankRole)) {
                        replaceRankRole(member, newRankRole);
                        logs.writeLog(LevelLog.OK,RankScheduler.class.getName(),"Update Rank: " + liste.get(2) + " est maintenant " + rank);
                    }
                }

            }, throwable -> {
                logs.writeLog(LevelLog.ERR,RankScheduler.class.getName(),liste.get(1)+" n'est plus sur ce serveur");
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
