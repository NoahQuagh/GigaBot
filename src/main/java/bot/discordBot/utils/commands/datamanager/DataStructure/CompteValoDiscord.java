package bot.discordBot.utils.commands.datamanager.DataStructure;

public class CompteValoDiscord {
    private String idDiscord;
    private String nomDiscord;
    private String pseudoValo;

    public CompteValoDiscord(String idDiscord, String nomDiscord, String pseudoValo) {
        this.idDiscord = idDiscord;
        this.nomDiscord = nomDiscord;
        this.pseudoValo = pseudoValo;
    }

    public String getIdDiscord() {
        return idDiscord;
    }

    public String getNomDiscord() {
        return nomDiscord;
    }

    public String getPseudoValo() {
        return pseudoValo;
    }
}
