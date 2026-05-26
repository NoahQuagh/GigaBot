package bot.discordBot.utils.Procedure;

import bot.discordBot.Main;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.CommandContext;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

/**
 * Classe utilitaire gérant les procédures d'appel aux API externes (Riot/HenrikDev)
 * ainsi que la traduction des données de jeu de Valorant (Rangs, Couleurs, etc.).
 */
public class ApiProcedure {
    /**
     * Exécute une requête HTTP GET asynchrone vers l'API Valorant spécifiée.
     * Récupère automatiquement la clé d'API depuis la configuration TOML et l'ajoute dans les en-têtes.
     * En cas d'échec (timeout, coupure réseau), l'erreur est interceptée et consignée dans le fichier de logs.
     *
     * @param ctx Le contexte de la commande Discord actuelle (utilisé pour identifier l'auteur dans les logs).
     * @param url L'URL complète de l'endpoint d'API à interroger.
     * @return L'objet {@link HttpResponse} contenant la réponse sous forme de chaîne de caractères,
     * ou {@code null} si la requête a échoué.
     */
    public static HttpResponse<String> ApiRiotRequete(CommandContext ctx,String url){
        try{
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            String apiKey = Main.getConfigManager().getToml().getString("api.valorant_key");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", apiKey)
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }catch (Exception e){
            writeLogFile("logs.txt", ctx.getAuthorName()+" | Code : "+ Code.ERREUR_API);
        }
        return null;
    }

    /**
     * Exécute une requête HTTP GET asynchrone vers l'API Valorant spécifiée.
     * Récupère automatiquement la clé d'API depuis la configuration TOML et l'ajoute dans les en-têtes.
     * En cas d'échec (timeout, coupure réseau), l'erreur est interceptée et consignée dans le fichier de logs.
     *
     * @param url L'URL complète de l'endpoint d'API à interroger.
     * @return L'objet {@link HttpResponse} contenant la réponse sous forme de chaîne de caractères,
     * ou {@code null} si la requête a échoué.
     */
    public static HttpResponse<String> ApiRiotRequete(String url){
        try{
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            String apiKey = Main.getConfigManager().getToml().getString("api.valorant_key");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", apiKey)
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }catch (Exception e){
            writeLogFile("logs.txt", "TrackerValo | Code : "+ Code.ERREUR_API);
        }
        return null;
    }

    /**
     * Convertit l'identifiant numérique d'un rang compétitif Valorant (Tier ID) en son équivalent textuel en anglais.
     * Les valeurs correspondent aux ID officiels retournés par les APIs Valorant (0 pour Sans rang, jusqu'à 27 pour Radiant).
     *
     * @param nb L'identifiant numérique du rang (Tier ID).
     * @return Le libellé du rang sous forme de chaîne de caractères (ex: "Diamond 3", "Radiant").
     */
    public static String getRankTxtByInt(int nb){
        return switch (nb) {
            case 0 -> "Unranked";
            case 3 -> "Iron 1";
            case 4 -> "Iron 2";
            case 5 -> "Iron 3";
            case 6 -> "Bronze 1";
            case 7 -> "Bronze 2";
            case 8 -> "Bronze 3";
            case 9 -> "Silver 1";
            case 10 -> "Silver 2";
            case 11 -> "Silver 3";
            case 12 -> "Gold 1";
            case 13 -> "Gold 2";
            case 14 -> "Gold 3";
            case 15 -> "Platinum 1";
            case 16 -> "Platinum 2";
            case 17 -> "Platinum 3";
            case 18 -> "Diamond 1";
            case 19 -> "Diamond 2";
            case 20 -> "Diamond 3";
            case 21 -> "Ascendant 1";
            case 22 -> "Ascendant 2";
            case 23 -> "Ascendant 3";
            case 24 -> "Immortal 1";
            case 25 -> "Immortal 2";
            case 26 -> "Immortal 3";
            case 27 -> "Radiant";
            default -> "Inconnu (" + nb + ")";
        };
    }

    /**
     * Associe une couleur graphique (AWT {@link Color}) à une chaîne de caractères représentant un rang Valorant.
     * Cette méthode permet d'harmoniser visuellement la couleur des bordures des Embeds Discord en fonction de la charte du jeu.
     *
     * @param rank Le nom textuel du rang (ex: "Gold 1", "Ascendant 3", etc.). supporte l'anglais et le français.
     * @return L'objet {@link Color} correspondant au thème du rang, ou {@link Color#GRAY} si le rang est inconnu ou nul.
     */
    public static Color getColorRankByRankTxt(String rank){
        if (rank == null) return Color.GRAY;

        String lowerRank = rank.toLowerCase();

        if (lowerRank.contains("iron") || lowerRank.contains("fer")) {
            return new Color(82, 82, 82);
        } else if (lowerRank.contains("bronze")) {
            return new Color(141, 107, 72);
        } else if (lowerRank.contains("silver") || lowerRank.contains("argent")) {
            return new Color(180, 195, 197);
        } else if (lowerRank.contains("gold") || lowerRank.contains("or")) {
            return new Color(229, 203, 91);
        } else if (lowerRank.contains("platinum") || lowerRank.contains("platine")) {
            return new Color(46, 171, 185);
        } else if (lowerRank.contains("diamond") || lowerRank.contains("diamant")) {
            return new Color(185, 128, 237);
        } else if (lowerRank.contains("ascendant")) {
            return new Color(69, 145, 107);
        } else if (lowerRank.contains("immortal") || lowerRank.contains("immortel")) {
            return new Color(185, 49, 79);
        } else if (lowerRank.contains("radiant")) {
            return new Color(255, 244, 186);
        }

        return Color.WHITE;
    }
}
