package bot.discordBot.utils.BDD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class pour établir une connexion à la base de données du serveur
 */
public class DataBaseManager {
    private static final String URL ="jdbc:mariadb://localhost:3306/portfolio_db";
    private static final String USER ="root";
    private static final String PASSWORD ="2007,MAri";

    /**
     * Obtenir la connexion à la base de données
     * @return {@link Connection} connexion pour effectuer des requêtes
     * @throws SQLException en cas d'échec de connexion
     */
    public static Connection getConnectionDB() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
