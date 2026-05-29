package botdiscord.gigabot.utils.DB;

import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.structure.TrackedPlayer;
import botdiscord.gigabot.utils.exception.TrackingException;
import botdiscord.gigabot.utils.commands.CommandContext;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


public class tracker_joueur_DB extends DataBaseManager{


    public tracker_joueur_DB() throws SQLException {
        super();
    }

    public ArrayList<TrackedPlayer> getTrackedPlayer(){
        ArrayList<TrackedPlayer> listePlayer = new ArrayList<>();
        String requete = "SELECT * FROM tracker_joueur_bot";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                String valo_pseudo = rs.getString("valo_pseudo");
                String channel_id = rs.getString("channel_id");
                int rank_peak = rs.getInt("rank_peak");
                listePlayer.add(new TrackedPlayer(valo_pseudo,channel_id,rank_peak));
            }
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,tracker_joueur_DB.class.getName(),"Obtention des joueur suivit échoué : "+e);
            return listePlayer;
        }
        getLogs().writeLog(LevelLog.OK,tracker_joueur_DB.class.getName(),"Obtention des joueur suivit réussie");
        return listePlayer;
    }

    public int updatePeakRank(String valo_pseudo,int newPeak){
        String requete = "UPDATE tracker_joueur_bot set rank_peak = ? WHERE valo_pseudo=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setInt(1, newPeak);
            preState.setString(2, valo_pseudo);
            getLogs().writeLog(LevelLog.OK,tracker_joueur_DB.class.getName(),"Peak rank de "+valo_pseudo+" mis à jour");
            return preState.executeUpdate();
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,tracker_joueur_DB.class.getName(),"Mise à jour du peak rank de "+valo_pseudo+" échoué");
            return 0;
        }
    }

    public int setTracking(CommandContext ctx, String valo_pseudo, String channel_id, int rank_peak){
        if(playerIsTracked(valo_pseudo)) throw new TrackingException(ctx,"Ce joueur est déjà traqué par le bot");
        String requete = "INSERT INTO tracker_joueur_bot(valo_pseudo, channel_id, rank_peak) VALUES (?, ?, ?)";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setString(1, valo_pseudo);
            preState.setString(2, channel_id);
            preState.setInt(3, rank_peak);
            return preState.executeUpdate();
        } catch (SQLException | TrackingException e) {
            getLogs().writeLog(LevelLog.ERR,tracker_joueur_DB.class.getName(),"Erreur critique lors de la création du tracking : " + e.getMessage());
            return 0;
        }
    }

    public boolean playerIsTracked(String player){
        String requete = "SELECT valo_pseudo FROM tracker_joueur_bot";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                if(rs.getString("valo_pseudo").equals(player)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,tracker_joueur_DB.class.getName(),"Verification joueur déjà traqué échoué : "+e);
            return true;
        }
    }

    public int deleteTrackingByPlayerName(CommandContext ctx,String valo_pseudo){
        if(!(playerIsTracked(valo_pseudo))) throw new TrackingException(ctx,"Ce joueur n'est pas traqué par le bot");
        String requete = "DELETE FROM tracker_joueur_bot WHERE valo_pseudo=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setString(1, valo_pseudo);
            return preState.executeUpdate();
        } catch (SQLException | TrackingException e) {
            getLogs().writeLog(LevelLog.ERR,tracker_joueur_DB.class.getName(),"Erreur critique lors de la suppression du tracking : " + e.getMessage());
            return 0;
        }
    }
}
