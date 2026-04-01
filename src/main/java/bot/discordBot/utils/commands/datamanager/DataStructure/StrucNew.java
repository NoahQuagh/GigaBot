package bot.discordBot.utils.commands.datamanager.DataStructure;

import java.util.ArrayList;

public class StrucNew {
    private String version;
    private ArrayList<Nouveaute> nouveau=new ArrayList<>();
    private ArrayList<Bug> bug=new ArrayList<>();

    public StrucNew(String version, ArrayList<Nouveaute> nouveau, ArrayList<Bug> bug) {
        this.version = version;
        this.nouveau = nouveau;
        this.bug = bug;
    }

    public String getVersion() {
        return version;
    }

    public ArrayList<Nouveaute> getNouveau() {
        return nouveau;
    }

    public ArrayList<Bug> getBug() {
        return bug;
    }
}
