package botdiscord.gigabot.utils.DB;

import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.structure.Rappel;
import botdiscord.gigabot.utils.exception.TrackingException;
import botdiscord.gigabot.utils.commands.CommandContext;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class event_DB extends DataBaseManager{

    public event_DB(){
        super();
    }

    public int setEvent(int team_id, LocalDateTime date_match, String joueur){
        if(playerEventExist(joueur,date_match)) return 0;
        String requete = "INSERT INTO evenements_bot(equipe_id, date_match, cree_par) VALUES (?, ?, ?)";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setInt(1, team_id);
            preState.setObject(2, date_match);
            preState.setString(3, joueur);
            return preState.executeUpdate();
        } catch (SQLException | TrackingException e) {
            getLogs().writeLog(LevelLog.ERR,event_DB.class.getName(),"Erreur critique lors de la création du rappel : " + e.getMessage());
            return 0;
        }
    }

    public ArrayList<Rappel> getEvent(){
        ArrayList<Rappel> listeRappel = new ArrayList<>();
        String requete = "SELECT * FROM evenements_bot";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                int equipe_id = rs.getInt("equipe_id");
                Timestamp date_match = rs.getTimestamp("date_match");
                String auteur = rs.getString("cree_par");
                listeRappel.add(new Rappel(equipe_id,date_match,auteur));
            }
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,event_DB.class.getName(),"Obtention des rappels échoué : "+e);
            return listeRappel;
        }
        getLogs().writeLog(LevelLog.OK,event_DB.class.getName(),"Obtention des rappels réussie");
        return listeRappel;
    }

    public boolean playerEventExist(String joueur,LocalDateTime date_match){
        String requete = "SELECT cree_par,date_match FROM evenements_bot";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                if(rs.getString("cree_par").equals(joueur) && rs.getObject("date_match").equals(date_match)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,event_DB.class.getName(),"Verification joueur déjà un rappel échoué : "+e);
            return true;
        }
    }

    public int netoyageRappel(){
        String requete = "delete from evenements_bot WHERE date_match < NOW() - INTERVAL 10 MINUTE";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            return preState.executeUpdate();
        } catch (SQLException | TrackingException e) {
            getLogs().writeLog(LevelLog.ERR,event_DB.class.getName(),"Erreur critique lors du netoyage des rappels : " + e.getMessage());
            return 0;
        }
    }

    public int deleteRappelByPlayer(int equipeId,LocalDateTime dateMatch,String joueur){
        String requete = "DELETE FROM evenements_bot WHERE equipe_id=? AND date_match=? AND cree_par=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setInt(1, equipeId);
            preState.setObject(2, dateMatch);
            preState.setString(3, joueur);
            return preState.executeUpdate();
        } catch (SQLException | TrackingException e) {
            getLogs().writeLog(LevelLog.ERR,getClass().getName(),"Erreur critique lors de la suppression du rappel : " + e.getMessage());
            return 0;
        }
    }

    public int deleteRappelForTeam(int equipeId,LocalDateTime dateMatch){
        String requete = "DELETE FROM evenements_bot WHERE equipe_id=? AND date_match=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setInt(1, equipeId);
            preState.setObject(2, dateMatch);
            return preState.executeUpdate();
        } catch (SQLException | TrackingException e) {
            getLogs().writeLog(LevelLog.ERR,getClass().getName(),"Erreur critique lors de la suppression du rappel : " + e.getMessage());
            return 0;
        }
    }
}
