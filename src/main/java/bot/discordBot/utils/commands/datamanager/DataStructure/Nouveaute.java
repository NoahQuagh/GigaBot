package bot.discordBot.utils.commands.datamanager.DataStructure;

/**
 * Représente une nouveauté ou une fonctionnalité ajoutée lors d'une mise à jour du bot.
 * Cette classe stocke le nom de l'ajout ainsi qu'une description détaillée de ce qu'il apporte.
 */
public class Nouveaute {
    private String nomNouveaute;
    private String description;

    /**
     * Instancie une nouvelle structure de nouveauté.
     *
     * @param nomNouveaute Le nom ou le titre explicite de la nouvelle fonctionnalité (ex: "Système de Rappels").
     * @param description  Le texte descriptif détaillant le fonctionnement de cette nouveauté.
     */
    public Nouveaute(String nomNouveaute, String description) {
        this.nomNouveaute = nomNouveaute;
        this.description = description;
    }

    /**
     * Récupère le nom de la fonctionnalité ajoutée.
     *
     * @return Le nom de la nouveauté sous forme de chaîne de caractères.
     */
    public String getNomNouveaute() {
        return nomNouveaute;
    }

    /**
     * Récupère la description explicative associée à cette nouveauté.
     *
     * @return La description sous forme de chaîne de caractères.
     */
    public String getDescription() {
        return description;
    }
}
