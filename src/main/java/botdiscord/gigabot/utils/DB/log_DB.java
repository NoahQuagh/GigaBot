package botdiscord.gigabot.utils.DB;

import botdiscord.gigabot.utils.DB.enumDB.LevelLog;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Class pour gérer les requêtes après de la table logs_bot
 */
public class log_DB extends DataBaseManager {


    public log_DB() throws SQLException {
        super();
    }

    /**
     * écriture des logs dans la BDD
     * @param level {@link LevelLog} état de l'événement
     * @param path {@link String} localisation de l'évènement qui écrit le log
     * @param message {@link String} message de précision de l'événement
     * @return {@link int} nombre de lignes écrit dans la BDD, 0 si échec
     */
    public int writeLog(LevelLog level, String path, String message) {
        String requete = "INSERT INTO logs_bot(niveau, localisation, message) VALUES (?, ?, ?)";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            preState.setString(1, level.toString());
            preState.setString(2, path);
            preState.setString(3, message);
            return preState.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur critique lors de l'insertion des logs : " + e.getMessage());
            return 0;
        }
    }

    /**
     * Obtenir les logs du bot sous forme de liste de lignes textuelles.
     * * @return Une {@link ArrayList} contenant chaque log formaté, ou une liste vide en cas d'erreur.
     */
    public ArrayList<String> getLog() {
        ArrayList<String> listeLogs = new ArrayList<>();
        String requete = "SELECT * FROM logs_bot";
        try (PreparedStatement preState = getDb().prepareStatement(requete);
            java.sql.ResultSet rs = preState.executeQuery()) {
            while (rs.next()) {
                java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
                String niveau = rs.getString("niveau");
                String localisation = rs.getString("localisation");
                String message = rs.getString("message");
                String logFormatte = "[" + createdAt + "] " + niveau + " | " + localisation + " | " + message;
                listeLogs.add(logFormatte);
            }
        } catch (SQLException e) {
            writeLog(LevelLog.ERR,log_DB.class.getName(),"Obtention des logs échoué : "+e);
            return listeLogs;
        }
        writeLog(LevelLog.OK,log_DB.class.getName(),"Obtention des logs réussie");
        return listeLogs;
    }

    /**
     * Supprime les logs dans la BDD
     * @return {@link int} nombre de ligne modifié
     */
    public int clearLog(){
        String requete = "DELETE FROM logs_bot WHERE id>=0";
        try (PreparedStatement preState = getDb().prepareStatement(requete)) {
            writeLog(LevelLog.OK,log_DB.class.getName(),"Suppression des logs réussie");
            return preState.executeUpdate();
        } catch (SQLException e) {
            writeLog(LevelLog.ERR,log_DB.class.getName(),"Suppression des logs échoué");
            return 0;
        }
    }
}
