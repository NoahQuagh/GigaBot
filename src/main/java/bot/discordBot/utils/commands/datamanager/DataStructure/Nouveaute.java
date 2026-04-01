package bot.discordBot.utils.commands.datamanager.DataStructure;

public class Nouveaute {
    private String nomNouveaute;
    private String description;

    public Nouveaute(String nomNouveaute, String description) {
        this.nomNouveaute = nomNouveaute;
        this.description = description;
    }

    public String getNomNouveaute() {
        return nomNouveaute;
    }

    public String getDescription() {
        return description;
    }
}
