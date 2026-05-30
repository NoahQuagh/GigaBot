package botdiscord.gigabot.utils.DB;

import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.exception.TrackingException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class Valo_Dis_DB extends DataBaseManager{


    public Valo_Dis_DB(){
        super();
    }

    public int setValoDis(String discord_id, String discord_pseudo, String valo_pseudo){
        if(ValoDisExist(valo_pseudo)) return 0;
        String requete = "INSERT INTO valo_dis(discord_id,discord_pseudo,valo_pseudo) VALUES (?, ?, ?)";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setString(1, discord_id);
            preState.setString(2, discord_pseudo);
            preState.setString(3, valo_pseudo);
            return preState.executeUpdate();
        } catch (SQLException | TrackingException e) {
            getLogs().writeLog(LevelLog.ERR,Valo_Dis_DB.class.getName(),"Erreur critique lors de la création de la correspondance : " + e.getMessage());
            return 0;
        }
    }

    public int setValoDis(String valo_pseudo){
        if(ValoDisExist(valo_pseudo)) return 0;
        String requete = "INSERT INTO valo_dis(valo_pseudo) VALUES (?)";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setString(3, valo_pseudo);
            return preState.executeUpdate();
        } catch (SQLException | TrackingException e) {
            getLogs().writeLog(LevelLog.ERR,Valo_Dis_DB.class.getName(),"Erreur critique lors de la création de la correspondance : " + e.getMessage());
            return 0;
        }
    }

    public boolean ValoDisExist(String valo_pseudo){
        String requete = "SELECT valo_pseudo FROM valo_dis";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                if(rs.getString("valo_pseudo").equals(valo_pseudo)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,Valo_Dis_DB.class.getName(),"Verification joueur déjà dans la correspondance échoué : "+e);
            return true;
        }
    }

    public ArrayList<String> getValoPseudo(){
        String requete = "SELECT valo_pseudo FROM valo_dis";
        ArrayList<String> listePlayer = new ArrayList<>();
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                listePlayer.add(rs.getString("valo_pseudo"));
            }
            return listePlayer;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,Valo_Dis_DB.class.getName(),"get Team id par nom d'équipe à échoué : "+e);
            return listePlayer;
        }
    }

    /**
     * Génère une liste filtrée de suggestions de pseudos Valorant pour l'auto-complétion des commandes Discord.
     * Filtre les pseudos enregistrés en mémoire en ne gardant que ceux qui commencent par la saisie actuelle de l'utilisateur (insensible à la casse).
     * Limite automatiquement les résultats à 25 propositions maximum (contrainte imposée par l'API Discord).
     *
     * @param currentInput Le texte actuellement tapé par l'utilisateur dans le champ de la commande Discord.
     * @return Une liste de chaînes de caractères contenant les pseudos correspondants, limitée à 25 éléments.
     */
    public List<String> getPseudosAutocomplete(String currentInput) {
        ArrayList<String> comptes = getValoPseudo();

        if (comptes == null) {
            return Collections.emptyList();
        }

        return comptes.stream()
                .filter(Objects::nonNull)
                .filter(pseudo -> pseudo.toLowerCase().startsWith(currentInput.toLowerCase()))
                .limit(25)
                .collect(Collectors.toList());
    }

    public ArrayList<String> getValoDisByDiscordId(String discord_id){
        ArrayList<String> liste = new ArrayList<>();
        String requete = "SELECT * FROM valo_dis WHERE discord_id=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setString(1, discord_id);
            java.sql.ResultSet rs = preState.executeQuery();
            while (rs.next()) {
                liste.add(rs.getString("discord_id"));
                liste.add(rs.getString("discord_pseudo"));
                liste.add(rs.getString("valo_pseudo"));
            }
            return liste;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,Valo_Dis_DB.class.getName(),"Obtention ValoDis pour "+discord_id+" échoué : "+e);
            return liste;
        }
    }

    public ArrayList<String> getValoDisByValoPseudo(String valo_pseudo){
        ArrayList<String> liste = new ArrayList<>();
        String requete = "SELECT * FROM valo_dis WHERE valo_pseudo=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setString(1, valo_pseudo);
            java.sql.ResultSet rs = preState.executeQuery();
            while (rs.next()) {
                liste.add(rs.getString("discord_id"));
                liste.add(rs.getString("discord_pseudo"));
                liste.add(rs.getString("valo_pseudo"));
            }
            return liste;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,Valo_Dis_DB.class.getName(),"Obtention ValoDis pour "+valo_pseudo+" échoué : "+e);
            return liste;
        }
    }
}
