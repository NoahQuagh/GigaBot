package bot.discordBot.utils.commands.datamanager.DataStructure;

import bot.discordBot.utils.commands.datamanager.DataManager;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Rappel {
    private String userId;
    private LocalDateTime date;

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getDate() {
        return date;
    }


    public Rappel(String userId, LocalDateTime date) {
        this.userId = userId;
        this.date = date;
    }

    public static boolean idJoueurEstDansRappel(String idJoueur){
        ArrayList<Rappel> rappels = DataManager.loadRappels();
        if(rappels.isEmpty()) return false;
        for(Rappel rappel : rappels){
            if(rappel.getUserId().equals(idJoueur)){
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Rappel> getListeRappelByIdjoueur(String idJoueur){
        ArrayList<Rappel> rappels = DataManager.loadRappels();
        ArrayList<Rappel> rappelPrevu = new ArrayList<>();
        if(rappels.isEmpty()) return null;
        for(Rappel rappel : rappels){
            if(rappel.getUserId().equals(idJoueur)){
                rappelPrevu.add(rappel);
            }
        }
        return rappelPrevu;
    }
}
