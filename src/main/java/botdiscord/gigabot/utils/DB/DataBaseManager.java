package botdiscord.gigabot.utils.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class pour établir une connexion à la base de données du serveur
 */
public abstract class DataBaseManager {
    private static final String URL ="jdbc:mariadb://localhost:3306/portfolio_db";
    private static final String USER ="root";
    private static final String PASSWORD ="2007,MAri";
    private Connection db;
    private log_DB logs;

    public DataBaseManager(){
        try{
            this.db = DriverManager.getConnection(URL, USER, PASSWORD);
            this.logs = new log_DB();
        }catch (SQLException e){
            System.err.println(e);
        }

    }

    /**
     * Obtenir la connexion à la base de données
     * @return {@link Connection} connexion pour effectuer des requêtes
     */
    public Connection getDb() {
        return db;
    }

    public log_DB getLogs() {
        return logs;
    }
}
