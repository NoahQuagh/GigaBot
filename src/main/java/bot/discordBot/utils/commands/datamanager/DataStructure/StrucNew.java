package bot.discordBot.utils.commands.datamanager.DataStructure;

import java.util.ArrayList;

/**
 * Représente la structure de données d'une mise à jour (News / Patch Notes) du bot.
 * Cette classe encapsule le numéro de version associé à la mise à jour, ainsi que les listes
 * des nouveautés ajoutées et des corrections de bugs associées.
 */
public class StrucNew {
    private String version;
    private ArrayList<Nouveaute> nouveau=new ArrayList<>();
    private ArrayList<Bug> bug=new ArrayList<>();

    /**
     * Instancie une nouvelle structure de mise à jour avec sa version, ses nouveautés et ses corrections.
     *
     * @param version Le numéro ou nom de la version de la mise à jour (ex: "v1.2.0").
     * @param nouveau La liste {@link ArrayList} contenant les instances de {@link Nouveaute} intégrées.
     * @param bug     La liste {@link ArrayList} contenant les instances de {@link Bug} corrigés.
     */
    public StrucNew(String version, ArrayList<Nouveaute> nouveau, ArrayList<Bug> bug) {
        this.version = version;
        this.nouveau = nouveau;
        this.bug = bug;
    }

    /**
     * Récupère le numéro de version de cette mise à jour.
     *
     * @return La version sous forme de chaîne de caractères.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Récupère la liste de toutes les nouveautés introduites dans cette version.
     *
     * @return Une {@link ArrayList} contenant les objets {@link Nouveaute}.
     */
    public ArrayList<Nouveaute> getNouveau() {
        return nouveau;
    }

    /**
     * Récupère la liste de tous les bugs résolus dans cette version.
     *
     * @return Une {@link ArrayList} contenant les objets {@link Bug}.
     */
    public ArrayList<Bug> getBug() {
        return bug;
    }
}
