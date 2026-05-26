package bot.discordBot.System;

import bot.discordBot.utils.commands.datamanager.DataStructure.Games;

import java.awt.Color;
import java.util.*;

import static bot.discordBot.utils.commands.datamanager.DataManager.loadGamesList;
import static bot.discordBot.utils.commands.datamanager.DataManager.saveGamesList;


public class GameConfigRole {
    private static final Map<String, Color> GAME_COLORS = new HashMap<>();


    static {
        GAME_COLORS.put("Valorant", new Color(255, 70, 85));       // Rouge Valorant
        GAME_COLORS.put("League of Legends", new Color(0, 102, 255));        // Bleu League of Legends
        GAME_COLORS.put("Minecraft", new Color(62, 175, 52));  // Vert Minecraft
        GAME_COLORS.put("csgo", new Color(222, 155, 54));      // Orange CS
        GAME_COLORS.put("Fortnite", new Color(190, 30, 235));  // Violet Fortnite
        GAME_COLORS.put("Rocket League", new Color(0, 150, 255));     // Bleu Rocket League
    }

    public static Color getColorForGame(String gameKey) {
        return GAME_COLORS.getOrDefault(gameKey.toLowerCase(), Color.WHITE);
    }

    /**
     * Permet d'obtenir la liste des jeu enregistrer pour un serveur donnée
     * @param servId ID du serveur
     * @return liste de jeu
     */

    public static List<String> getGameNameByServeurId(String servId) {
        ArrayList<Games> allGames = loadGamesList();
        if (allGames == null) return Collections.emptyList();

        return allGames.stream()
                .filter(g -> g.getServeurId().equals(servId))
                .findFirst()
                .map(g -> new ArrayList<>(g.getGameMap().keySet()))
                .orElse(new ArrayList<>());
    }

    /**
     * Permet d'ajouter un jeu avec ca couleur
     * @param serveurId ID du serveur
     * @param name nom du jeu
     * @param r red
     * @param g green
     * @param b blue
     */

    public static void addGameName(String serveurId, String name, int r, int g, int b) {
        ArrayList<Games> allGames = loadGamesList();
        if (allGames == null) allGames = new ArrayList<>();

        Color color = new Color(r, g, b);

        // On cherche si le serveur existe déjà dans la liste
        Games serverConfig = allGames.stream()
                .filter(j -> j.getServeurId().equals(serveurId))
                .findFirst()
                .orElse(null);

        if (serverConfig == null) {
            // Nouveau serveur
            serverConfig = new Games(serveurId);
            serverConfig.addGame(name, color);
            allGames.add(serverConfig);
        } else {
            // Serveur existant, on ajoute juste le jeu
            serverConfig.addGame(name, color);
        }

        saveGamesList(allGames);
    }

    /**
     * Permet d'obtenir la couleur d'un jeu sur un serveur précis
     * @param servId ID du serveur
     * @param gameName nom du jeu
     * @return retourne la couleur du jeu demandé
     */

    public static Color getColorForGame(String servId, String gameName) {
        ArrayList<Games> allGames = loadGamesList();
        if (allGames != null) {
            for (Games g : allGames) {
                if (g.getServeurId().equals(servId) && g.getGameMap().containsKey(gameName)) {
                    int rgb = g.getGameMap().get(gameName);
                    return new Color(rgb);
                }
            }
        }
        return Color.GRAY;
    }
}