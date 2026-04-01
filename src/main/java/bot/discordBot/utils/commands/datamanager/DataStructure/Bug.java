package bot.discordBot.utils.commands.datamanager.DataStructure;

public class Bug {
    private String nomBug;
    private String description;
    private String resolution;

    public Bug(String nomBug, String description, String resolution) {
        this.nomBug = nomBug;
        this.description = description;
        this.resolution = resolution;
    }

    public String getNomBug() {
        return nomBug;
    }

    public String getDescription() {
        return description;
    }

    public String getResolution() {
        return resolution;
    }
}
