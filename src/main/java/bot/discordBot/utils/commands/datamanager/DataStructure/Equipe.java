package bot.discordBot.utils.commands.datamanager.DataStructure;

import bot.discordBot.utils.commands.datamanager.DataManager;

import java.util.ArrayList;

/**
 * Représente une équipe (Team) gérée par le bot pour les compétitions ou événements.
 * Cette classe encapsule le nom de l'équipe, l'identifiant du capitaine (chef), de son adjoint,
 * ainsi que la liste des identifiants Discord des joueurs membres.
 * Elle fournit un ensemble complet de méthodes statiques pour manipuler, persister et interroger
 * les équipes via le {@link DataManager}.
 */
public class Equipe {
    private String equipeId;
    private String chefId;
    private String chefAdjId;
    private ArrayList<String> joueurIds;

    /**
     * Instancie une nouvelle équipe avec un nom, un chef et une liste initiale de joueurs.
     * L'adjoint est initialisé par défaut à vide.
     *
     * @param equipeId  L'identifiant unique ou le nom de l'équipe.
     * @param chefId    L'identifiant Discord (Snowflake) du capitaine de l'équipe.
     * @param joueurIds La liste {@link ArrayList} des identifiants Discord des membres initiaux.
     */
    public Equipe(String equipeId, String chefId, ArrayList<String> joueurIds) {
        this.equipeId = equipeId;
        this.chefId = chefId;
        this.chefAdjId = "";
        this.joueurIds = joueurIds;
    }

    /**
     * Récupère l'identifiant ou le nom unique de l'équipe.
     *
     * @return Le nom de l'équipe sous forme de chaîne de caractères.
     */
    public String getEquipeId() {
        return equipeId;
    }

    /**
     * Récupère l'identifiant Discord du capitaine (chef) de l'équipe.
     *
     * @return L'identifiant Discord du chef.
     */
    public String getChefId() {
        return chefId;
    }

    /**
     * Récupère la liste des identifiants Discord de tous les joueurs de l'équipe.
     *
     * @return Une {@link ArrayList} contenant les identifiants Discord des membres.
     */
    public ArrayList<String> getJoueurIds() {
        return joueurIds;
    }

    /**
     * Récupère l'identifiant Discord du chef adjoint de l'équipe.
     *
     * @return L'identifiant Discord de l'adjoint, ou une chaîne vide s'il n'y en a pas.
     */
    public String getChefAdjId() {
        return chefAdjId;
    }

    /**
     * Modifie l'identifiant de l'équipe dans le système.
     *
     * @param equipeId {@link String} nouvelle id de l'équipe.
     */
    public void setEquipeId(String equipeId) {
        this.equipeId = equipeId;//TODO====================================================
    }

    /**
     * Modifie l'identifiant Discord du capitaine (chef) de l'équipe.
     *
     * @param chefId Le nouvel identifiant Discord du chef.
     */
    public void setChefId(String chefId) {
        this.chefId = chefId;
    }

    /**
     * Modifie l'identifiant Discord du chef adjoint de l'équipe.
     *
     * @param chefAdjId Le nouvel identifiant Discord de l'adjoint.
     */
    public void setChefAdjId(String chefAdjId) {
        this.chefAdjId = chefAdjId;
    }

    /**
     *Modifie la liste des joueurs dans la team
     *
     * @param joueurIds La liste {@link ArrayList} des identifiants Discord des membres.
     */
    public void setJoueurIds(ArrayList<String> joueurIds) {
        this.joueurIds = joueurIds;
    }

    /**
     * Vérifie de manière globale si un nom d'équipe est déjà utilisé dans les données enregistrées.
     *
     * @param nomteam Le nom de l'équipe à rechercher.
     * @return {@code true} si l'équipe existe déjà, sinon {@code false}.
     */
    public static boolean nomEquipteUtiliser(String nomteam){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return false;
        for (Equipe equipe : equipes) {
            if (equipe.getEquipeId().equals(nomteam)) return true;
        }
        return false;
    }

    /**
     * Détermine si un joueur spécifique est déja capitaine d'une équipe.
     *
     * @param idCapitaine L'identifiant Discord du joueur à tester.
     * @return {@code true} s'il s'agit du chef de l'équipe, sinon {@code false}.
     */
    public static boolean estDejaCapitaineDuneEquipe(String idCapitaine){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return false;
        for (Equipe equipe : equipes) {
            if (equipe.getChefId().equals(idCapitaine)) return true;
        }
        return false;
    }

    /**
     * Vérifie si le joueur donné comme capitaine a une équipe.
     *
     * @param idCapitaine L'identifiant Discord du joueur à tester.
     * @return {@code true} s'il n'a pas d'équipe, sinon {@code false}.
     */
    public static boolean CapitaineNaPasDEquipe(String idCapitaine){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return true;
        for (Equipe equipe : equipes) {
            if (equipe.getChefId().equals(idCapitaine)) return false;
        }
        return true;
    }

    /**
     * Vérifie si le joueur donné comme capitaine adjoint a une équipe.
     *
     * @param idAdjoint L'identifiant Discord du joueur adjoint à tester.
     * @return {@code true} s'il n'a pas d'équipe, sinon {@code false}.
     */
    public static boolean AdjointNaPasDEquipe(String idAdjoint){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return true;
        for (Equipe equipe : equipes) {
            if (equipe.getChefAdjId().equals(idAdjoint)) return false;
        }
        return true;
    }

    /**
     * Obtenir le nom de l'équipe pour un nom de capitaine donné.
     *
     * @param idCapitaine L'identifiant Discord du joueur à tester.
     * @return le nom de l'équipe {@link String}.
     */
    public static String getTeamNameByIdCapitaine(String idCapitaine){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return null;
        for (Equipe equipe : equipes) {
            if (equipe.getChefId().equals(idCapitaine)) return equipe.getEquipeId();
        }
        return null;
    }

    /**
     * Obtenir le nom de l'équipe pour un nom de capitaine adjoint donné.
     *
     * @param idAdjoin L'identifiant Discord du joueur à tester.
     * @return le nom de l'équipe {@link String}.
     */
    public static String getTeamNameByIdAdjoint(String idAdjoin){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return null;
        for (Equipe equipe : equipes) {
            if (equipe.getChefAdjId().equals(idAdjoin)) return equipe.getEquipeId();
        }
        return null;
    }

    /**
     * Vérifie si le nombre maximum de joueurs (limité à 7) est atteint pour l'équipe d'un capitaine donné.
     *
     * @param idCapitaine L'identifiant Discord du capitaine de l'équipe.
     * @return {@code true} si l'équipe compte exactement 7 joueurs, sinon {@code false}.
     */
    public static boolean NbJoueurMaxAtteint(String idCapitaine){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return false;
        for(Equipe equipe : equipes){
            if(equipe.getJoueurIds().size()==7 & equipe.getChefId().equals(idCapitaine)){
                return true;
            }
        }
        return false;
    }

    /**
     * Supprime un joueur d'une équipe en ciblant cette dernière par l'identifiant de son capitaine, puis enregistre les données.
     *
     * @param idCapitaine L'identifiant Discord du capitaine de l'équipe.
     * @param idJoueur    L'identifiant Discord du joueur à retirer de l'équipe.
     * @return {@code true} si l'équipe a été trouvée et la tentative de suppression effectuée, sinon {@code false}.
     */
    public static boolean removeJoueurByIdCapitaineIdJoueur(String idCapitaine,String idJoueur){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return false;
        for(Equipe equipe : equipes){
            if(equipe.getChefId().equals(idCapitaine)){
                equipe.getJoueurIds().removeIf(id -> id.equals(idJoueur));
                DataManager.saveEquipes(equipes);
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie de manière globale si un joueur est déjà membre d'au moins une équipe enregistrée.
     *
     * @param idJoueur L'identifiant Discord du joueur à rechercher.
     * @return {@code true} si le joueur est présent dans la liste des membres d'une équipe, sinon {@code false}.
     */
    public static boolean joueurDejaDansUneEquipe(String idJoueur){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        for(Equipe equipe : equipes){
            for(String joueur : equipe.getJoueurIds()){
                if(joueur.equals(idJoueur)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si un joueur spécifique appartient à une équipe précise (recherche par nom d'équipe).
     *
     * @param team   Le nom (ID) de l'équipe cible.
     * @param player L'identifiant Discord du joueur recherché.
     * @return {@code true} si le joueur fait bien partie de cette équipe, sinon {@code false}.
     */
    public static boolean estDansCetteEquipe(String team,String player){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        for(Equipe equipe : equipes){
            if(equipe.getEquipeId().equals(team)){
                for(String joueur : equipe.getJoueurIds()){
                    if(joueur.equals(player)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Récupère une instance complète d'équipe à partir de son nom.
     *
     * @param name Le nom de l'équipe recherchée.
     * @return L'objet {@link Equipe} correspondant, ou {@code null} s'il n'existe pas.
     */
    public static Equipe getEquipeByEquipeName(String name){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return null;
        for(Equipe equipe : equipes){
            if(equipe.getEquipeId().equals(name)){
                return equipe;
            }
        }
        return null;
    }

    /**
     * Transfère la fonction de capitaine (succession) à un nouveau membre de l'équipe.
     * Si le nouveau capitaine était l'adjoint, son poste d'adjoint est automatiquement libéré.
     * Les modifications sont ensuite sauvegardées de manière persistante.
     *
     * @param team     Le nom de l'équipe concernée.
     * @param idJoueur L'identifiant Discord du membre reprenant le rôle de capitaine.
     * @return {@code true} si la succession s'est déroulée avec succès, {@code false} si l'équipe n'a pas pu être traitée.
     */
    public static boolean successionCapitaine(String team,String idJoueur){
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        if (equipes == null) return false;
        for(Equipe equipe : equipes){
            if(equipe.getEquipeId().equals(team)){
                equipe.setChefId(idJoueur);
                if(equipe.getChefAdjId().equals(idJoueur)){
                    equipe.setChefAdjId("");
                }
                DataManager.saveEquipes(equipes);
                return true;
            }
        }
        return false;
    }

    /**
     * Ajoute un joueur à la liste des membres de l'équipe.
     *
     * @param id L'identifiant Discord du joueur à ajouter.
     */
    public void addJoueur(String id){
        this.joueurIds.add(id);
    }

    /**
     * Retire un joueur de la liste des membres de l'équipe.
     *
     * @param id L'identifiant Discord du joueur à supprimer.
     */
    public void removeJoueur(String id){
        this.joueurIds.remove(id);
    }
}
