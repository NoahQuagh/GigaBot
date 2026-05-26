package bot.discordBot.utils.commands.datamanager.DataStructure;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Représente la configuration et la cartographie des jeux supportés sur un serveur Discord spécifique.
 * Cette classe permet d'associer des jeux (comme Valorant, League of Legends, etc.) à des couleurs
 * au format RGB afin de gérer dynamiquement les rôles de jeu sur le serveur.
 */
public class Games {
    private Map<String, Integer> gameMap;
    private String serveurId;

    /**
     * Instancie une nouvelle configuration de jeux pour un serveur avec des valeurs par défaut.
     * Initialise automatiquement la carte avec trois jeux prédéfinis et leurs couleurs associées :
     *
     * Valorant : Rouge (#FF4655)
     * League of Legends : Bleu foncé (#0D2F73)
     * Minecraft : Vert (#176709)
     *
     * @param serveurId L'identifiant unique Discord (Snowflake ID) du serveur.
     */
    public Games(String serveurId) {
        this.serveurId = serveurId;
        this.gameMap = new HashMap<>();
        this.gameMap.put("Valorant",new Color(255, 70, 85).getRGB());
        this.gameMap.put("League of Legends",new Color(13, 47, 113).getRGB());
        this.gameMap.put("Minecraft",new Color(23, 103, 9).getRGB());
    }

    /**
     * Instancie une configuration de jeux personnalisée pour un serveur avec une cartographie existante.
     *
     * @param serveurId L'identifiant unique Discord (Snowflake ID) du serveur.
     * @param gameMap   La table d'association {@link Map} contenant les noms des jeux et leurs couleurs RGB (Integer).
     */
    public Games(String serveurId, Map<String, Integer> gameMap) {
        this.serveurId = serveurId;
        this.gameMap = gameMap;
    }

    /**
     * Récupère l'identifiant du serveur Discord associé à cette configuration de jeux.
     *
     * @return L'identifiant du serveur sous forme de chaîne de caractères.
     */
    public String getServeurId() { return serveurId; }

    /**
     * Récupère la table d'association (Map) contenant l'ensemble des jeux configurés et leurs couleurs RGB.
     *
     * @return La {@link Map} associant le nom du jeu à sa couleur au format Integer.
     */
    public Map<String, Integer> getGameMap() { return gameMap; }

    /**
     * Ajoute un nouveau jeu ou met à jour la couleur d'un jeu existant dans la configuration du serveur.
     * La couleur fournie au format {@link Color} est automatiquement convertie en sa valeur entière RGB.
     *
     * @param name   Le nom du jeu à ajouter ou modifier.
     * @param color L'objet {@link Color} représentant la couleur associée au rôle de ce jeu.
     */
    public void addGame(String name, Color color) {
        this.gameMap.put(name, color.getRGB());
    }
}
