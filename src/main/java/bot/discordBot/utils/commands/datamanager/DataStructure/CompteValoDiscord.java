package bot.discordBot.utils.commands.datamanager.DataStructure;

/**
 * Représente l'association entre un compte Discord et un compte Valorant (Riot Games).
 * Cette classe permet de lier l'identifiant Discord d'un utilisateur à son pseudo en jeu (Riot Name)
 * et à son tag (Riot Tagline), facilitant ainsi le suivi et la gestion des joueurs au sein du bot.
 * Elle fournit également des méthodes statiques pour interroger et persister ces comptes via le {@link bot.discordBot.utils.commands.datamanager.DataManager}.
 */
public class CompteValoDiscord {
    private String idDiscord;
    private String nomDiscord;
    private String pseudoValo;

    /**
     * Instancie une nouvelle liaison de compte entre Discord et Valorant.
     *
     * @param idDiscord L'identifiant unique Discord (Snowflake ID) de l'utilisateur.
     * @param nomDiscord  Le pseudo Discord.
     * @param pseudoValo   Le pseudo associé au compte Valorant.
     */
    public CompteValoDiscord(String idDiscord, String nomDiscord, String pseudoValo) {
        this.idDiscord = idDiscord;
        this.nomDiscord = nomDiscord;
        this.pseudoValo = pseudoValo;
    }

    /**
     * Récupère l'identifiant Discord de l'utilisateur.
     *
     * @return L'identifiant Discord sous forme de chaîne de caractères.
     */
    public String getIdDiscord() {
        return idDiscord;
    }

    /**
     * Récupère le pseudo Discord de l'utilisateur.
     *
     * @return Le pseudo Discord sous forme de chaîne de caractères.
     */
    public String getNomDiscord() {
        return nomDiscord;
    }

    /**
     * Récupère le pseudo Valorant (Riot Name) de l'utilisateur.
     *
     * @return Le nom Valorant sous forme de chaîne de caractères.
     */
    public String getPseudoValo() {
        return pseudoValo;
    }

    @Override
    public String toString() {
        return "idDiscord='" + idDiscord + ';' +
                ", nomDiscord='" + nomDiscord + ';' +
                ", pseudoValo='" + pseudoValo;
    }
}
