package botdiscord.gigabot.utils.DB.enumDB;

public enum RoleTeamPremier {
    capitaine,
    adjoint,
    joueur;

    @Override
    public String toString() {
        return name();
    }

    /**
     * Convertit une chaîne de caractères en son équivalent RoleTeamPremier.
     * * @param roleStr Le texte représentant le rôle (ex: "adjoint", "Capitaine").
     * @return Le RoleTeamPremier correspondant, ou null si le texte ne correspond à rien.
     */
    public static RoleTeamPremier fromString(String roleStr) {
        if (roleStr == null) {
            return null;
        }
        try {
            return RoleTeamPremier.valueOf(roleStr.trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
