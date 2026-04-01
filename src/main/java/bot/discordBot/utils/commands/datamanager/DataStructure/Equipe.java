package bot.discordBot.utils.commands.datamanager.DataStructure;

import java.util.ArrayList;

public class Equipe {
    private String equipeId;
    private String chefId;
    private ArrayList<String> joueurIds;

    public Equipe(String equipeId, String chefId, ArrayList<String> joueurIds) {
        this.equipeId = equipeId;
        this.chefId = chefId;
        this.joueurIds = joueurIds;
    }

    public String getEquipeId() {
        return equipeId;
    }

    public String getChefId() {
        return chefId;
    }

    public ArrayList<String> getJoueurIds() {
        return joueurIds;
    }
}
