package bot.discordBot.utils.commands.datamanager.DataStructure;

import bot.discordBot.utils.commands.datamanager.DataManager;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Représente un rappel temporel planifié pour un utilisateur Discord.
 * Cette classe stocke l'identifiant de l'utilisateur concerné et la date/heure d'échéance.
 * Elle fournit également des méthodes statiques pour interroger, filtrer et manipuler les rappels
 * stockés globalement via le {@link DataManager}.
 */
public class Rappel {
    private String userId;
    private LocalDateTime date;

    /**
     * Instancie une nouvelle structure de rappel.
     *
     * @param userId L'identifiant unique Discord (ID de flocon / Snowflake ID) de l'utilisateur.
     * @param date   L'objet {@link LocalDateTime} représentant l'échéance programmée du rappel.
     */
    public Rappel(String userId, LocalDateTime date) {
        this.userId = userId;
        this.date = date;
    }

    /**
     * Récupère l'identifiant de l'utilisateur lié à ce rappel.
     *
     * @return L'ID de l'utilisateur sous forme de chaîne de caractères.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Récupère la date et l'heure programmées pour ce rappel.
     *
     * @return L'instance de {@link LocalDateTime} correspondante.
     */
    public LocalDateTime getDate() {
        return date;
    }


    /**
     * Vérifie si un joueur possède au moins un rappel actif enregistré dans le système.
     *
     * @param idJoueur L'identifiant de l'utilisateur Discord à tester.
     * @return {@code true} si un rappel correspond à cet utilisateur, sinon {@code false}.
     */
    public static boolean idJoueurEstDansRappel(String idJoueur){
        ArrayList<Rappel> rappels = DataManager.loadRappels();
        if(rappels.isEmpty()) return false;
        for(Rappel rappel : rappels){
            if(rappel.getUserId().equals(idJoueur)){
                return true;
            }
        }
        return false;
    }

    /**
     * Récupère la liste de tous les rappels spécifiques liés à un utilisateur donné.
     *
     * @param idJoueur L'identifiant de l'utilisateur Discord ciblé.
     * @return Une {@link ArrayList} contenant uniquement les objets {@link Rappel} de l'utilisateur.
     */
    public static ArrayList<Rappel> getListeRappelByIdjoueur(String idJoueur){
        ArrayList<Rappel> rappels = DataManager.loadRappels();
        ArrayList<Rappel> rappelPrevu = new ArrayList<>();
        if(rappels.isEmpty()) return null;
        for(Rappel rappel : rappels){
            if(rappel.getUserId().equals(idJoueur)){
                rappelPrevu.add(rappel);
            }
        }
        return rappelPrevu;
    }
}
