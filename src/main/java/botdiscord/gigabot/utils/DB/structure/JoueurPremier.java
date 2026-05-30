package botdiscord.gigabot.utils.DB.structure;

import botdiscord.gigabot.utils.DB.enumDB.RoleTeamPremier;

public class JoueurPremier {
    private String equipeName;
    private String discordId;
    private String pseudo;
    private RoleTeamPremier role;

    public JoueurPremier(String equipeName, String discordId, String pseudo, RoleTeamPremier role) {
        this.equipeName = equipeName;
        this.discordId = discordId;
        this.pseudo = pseudo;
        this.role = role;
    }

    public String getEquipeName() {
        return equipeName;
    }

    public void setEquipeName(String equipeName) {
        this.equipeName = equipeName;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public RoleTeamPremier getRole() {
        return role;
    }

    public void setRole(RoleTeamPremier role) {
        this.role = role;
    }
}
