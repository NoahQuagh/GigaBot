package bot.discordBot.utils.commands.datamanager.DataStructure;

/**
 * Représente la structure de données d'un bug système ou applicatif.
 * Permet de stocker le nom du bug, sa description détaillée ainsi que la solution
 * ou résolution qui lui a été apportée.
 */
public class Bug {
    private String nomBug;
    private String description;
    private String resolution;

    /**
     * Initialise une nouvelle instance de signalement de bug avec ses détails associés.
     *
     * @param nomBug      Le nom ou l'intitulé succinct du bug.
     * @param description La description détaillée du problème ou du comportement constaté.
     * @param resolution  La démarche, le correctif ou la solution appliquée pour résoudre le bug.
     */
    public Bug(String nomBug, String description, String resolution) {
        this.nomBug = nomBug;
        this.description = description;
        this.resolution = resolution;
    }

    /**
     * Récupère le nom ou l'intitulé du bug.
     *
     * @return Le nom du bug sous forme de chaîne de caractères.
     */
    public String getNomBug() {
        return nomBug;
    }

    /**
     * Récupère la description détaillée du bug.
     *
     * @return La description du problème.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Récupère les détails concernant la résolution du bug.
     *
     * @return La solution ou l'état de résolution du bug.
     */
    public String getResolution() {
        return resolution;
    }
}
