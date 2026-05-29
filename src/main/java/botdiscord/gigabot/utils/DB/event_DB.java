package botdiscord.gigabot.utils.DB;

import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.structure.Rappel;
import botdiscord.gigabot.utils.exception.TrackingException;
import botdiscord.gigabot.utils.commands.CommandContext;

import java.sql.*;
import java.util.ArrayList;

public class event_DB extends DataBaseManager{

    public event_DB() throws SQLException {
        super();
    }

    public int setEvent(CommandContext ctx, int team_id, Timestamp date_match, String joueur){
        if(playerEventExist(joueur,date_match)) throw new TrackingException(ctx,"Ce joueur a déjà rappel de prévu par le bot");
        String requete = "INSERT INTO evenements_bot(equipe_id, date_match, cree_par) VALUES (?, ?, ?)";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setInt(1, team_id);
            preState.setTimestamp(2, date_match);
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

    public boolean playerEventExist(String joueur,Timestamp date_match){
        String requete = "SELECT cree_par,date_match FROM evenements_bot";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                if(rs.getString("cree_par").equals(joueur) && rs.getTimestamp("date_match").equals(date_match)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,event_DB.class.getName(),"Verification joueur déjà un rappel échoué : "+e);
            return true;
        }
    }
}
