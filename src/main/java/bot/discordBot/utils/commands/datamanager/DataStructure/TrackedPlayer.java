package bot.discordBot.utils.commands.datamanager.DataStructure;

import bot.discordBot.utils.commands.datamanager.DataManager;

import java.util.ArrayList;

/**
 * Représente un joueur suivi par le système de tracker Valorant du bot.
 * Cette classe stocke l'identifiant de jeu du joueur, le salon Discord cible pour l'envoi des alertes,
 * ainsi que son rang compétitif historique le plus élevé (Peak Tier).
 */
public class TrackedPlayer {
    private String pseudoRaw;
    private String channelId;
    private int peakTier;

    /**
     * Instancie une nouvelle structure de joueur à suivre.
     *
     * @param pseudoRaw Le pseudonyme brut complet du joueur (ex: "Pseudo#TAG").
     * @param channelId L'identifiant unique (ID de salon) du canal Discord où publier les notifications.
     * @param peakTier  L'identifiant numérique représentant le grade historique maximum atteint par le joueur.
     */
    public TrackedPlayer(String pseudoRaw, String channelId, int peakTier) {
        this.pseudoRaw = pseudoRaw;
        this.channelId = channelId;
        this.peakTier = peakTier;
    }

    /**
     * Récupère le pseudonyme complet et brut (Pseudo#TAG) du joueur suivi.
     *
     * @return Le pseudo brut sous forme de chaîne de caractères.
     */
    public String getPseudoRaw() {
        return pseudoRaw;
    }

    /**
     * Récupère l'identifiant du salon Discord associé aux alertes de ce joueur.
     *
     * @return L'identifiant unique du salon.
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Récupère la valeur numérique représentant le rang le plus élevé (Peak Tier) enregistré.
     *
     * @return L'entier identifiant le niveau de grade maximum.
     */
    public int getPeakTier() {
        return peakTier;
    }

    /**
     * Met à jour le pseudonyme brut complet du joueur.
     *
     * @param pseudoRaw Le nouveau pseudo au format "Nom#TAG".
     */
    public void setPseudoRaw(String pseudoRaw) {
        this.pseudoRaw = pseudoRaw;
    }

    /**
     * Modifie le salon de destination Discord pour les notifications de suivi.
     *
     * @param channelId Le nouvel identifiant de salon.
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    /**
     * Met à jour la valeur numérique du grade le plus élevé atteint.
     *
     * @param peakTier Le nouvel entier représentant le niveau de grade maximum.
     */
    public void setPeakTier(int peakTier) {
        this.peakTier = peakTier;
    }

    /**
     * Vérifie de manière globale si un joueur est déjà enregistré et suivi dans le système.
     * Cette méthode charge la liste persistée via {@link DataManager#loadTrackedPlayer()} et effectue
     * une comparaison exacte sur le pseudonyme fourni.
     *
     * @param pseudoRaw Le pseudonyme brut complet recherché (ex: "Pseudo#TAG").
     * @return {@code true} si un suivi actif correspond au pseudonyme, sinon {@code false}.
     */
    public static boolean ceSuiviExiste(String pseudoRaw){
        ArrayList<TrackedPlayer> players = DataManager.loadTrackedPlayer();
        for(TrackedPlayer player : players){
            if(player.getPseudoRaw().equals(pseudoRaw)){
                return true;
            }
        }
        return false;
    }

    /**
     * Supprime un joueur du système de suivi (tracker) à partir de son pseudonyme brut.
     * Cette méthode charge la liste actuelle des joueurs, applique un filtre de suppression basé sur le pseudo,
     * sauvegarde la liste mise à jour via le {@link DataManager}, puis indique si l'opération a réussi.
     *
     * @param pseudoRaw Le pseudonyme brut exact (Nom#TAG) du joueur à retirer du suivi.
     * @return {@code true} si le joueur figurait dans la liste et a bien été supprimé, {@code false} s'il n'existait pas.
     */
    public static boolean RemoveTrackerByPseudoRaw(String pseudoRaw){
        ArrayList<TrackedPlayer> players = DataManager.loadTrackedPlayer();
        boolean rep = players.removeIf(id -> id.getPseudoRaw().equals(pseudoRaw));
        DataManager.saveTrackedPlayer(players);
        return rep;
    }
}
