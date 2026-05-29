package botdiscord.gigabot.utils.DB;

import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.enumDB.TypeChangelog;
import botdiscord.gigabot.utils.exception.BotException;
import botdiscord.gigabot.utils.exception.TrackingException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class version_bot_DB extends DataBaseManager{

    public version_bot_DB() throws SQLException {
        super();
    }

    public String getLastVersionBot(){
        String requete = "SELECT numero FROM versions_bot WHERE actuelle=true";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                getLogs().writeLog(LevelLog.OK,version_bot_DB.class.getName(),"Obtention de le dernière versions du bot réussie");
                return rs.getString("numero");
            }
            return "0.0.0";
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,version_bot_DB.class.getName(),"Obtention de le dernière versions du bot échoué : "+e);
            return "0.0.0";
        }
    }

    public boolean VersionBotExiste(String version){
        String requete = "SELECT numero FROM versions_bot";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                if(rs.getString("numero").equals(version)) return true;
            }
            return false;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,tracker_joueur_DB.class.getName(),"Verification version bot existe échoué : "+e);
            return false;
        }
    }

    public ArrayList<String> getInfoVersionBot(String version, TypeChangelog type){
        ArrayList<String> liste = new ArrayList<>();
        String requete = "SELECT texte FROM changelog_entrees_bot WHERE type=? and version_id=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            if(VersionBotExiste(version)) throw new BotException("Cette version du bot");
            int versionId = getIdVersionByVersionName(version);
            preState.setString(1, type.toString());
            preState.setInt(2, versionId);
            java.sql.ResultSet rs = preState.executeQuery();
            while (rs.next()) {
                liste.add(rs.getString("texte"));
            }
            getLogs().writeLog(LevelLog.OK,version_bot_DB.class.getName(),"Obtention des "+type+" de cette versions du bot réussie");
            return liste;
        } catch (SQLException | BotException e) {
            getLogs().writeLog(LevelLog.ERR,version_bot_DB.class.getName(),"Obtention des "+type.toString()+" de cette versions du bot échoué : "+e);
            return liste;
        }
    }

    public ArrayList<String> getVersionsBot(){
        ArrayList<String> listeVersion = new ArrayList<>();
        String requete = "SELECT numero FROM versions_bot";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
             java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                listeVersion.add(rs.getString("numero"));
            }
            getLogs().writeLog(LevelLog.OK,version_bot_DB.class.getName(),"Obtention de la liste des versions du bot réussie");
            return listeVersion;
        } catch (SQLException e) {
            getLogs().writeLog(LevelLog.ERR,version_bot_DB.class.getName(),"Obtention de la liste des versions du bot échoué : "+e);
            return listeVersion;
        }
    }

    private int getIdVersionByVersionName(String version){
        String requete = "SELECT id FROM versions_bot WHERE numero=?";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setString(1, version);
            java.sql.ResultSet rs = preState.executeQuery();
            while (rs.next()) {
                return rs.getInt("id");
            }
            return 0;
        } catch (SQLException | TrackingException e) {
            getLogs().writeLog(LevelLog.ERR,version_bot_DB.class.getName(),"Erreur lors de l'obtention de l'id de la version "+version+" : " + e.getMessage());
            return 0;
        }
    }
}
