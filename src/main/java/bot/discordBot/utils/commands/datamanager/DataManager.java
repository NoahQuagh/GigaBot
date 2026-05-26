package bot.discordBot.utils.commands.datamanager;

import bot.discordBot.utils.commands.datamanager.DataStructure.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.google.gson.*;
import java.time.format.DateTimeFormatter;
import java.lang.reflect.Type;

/**
 * Gestionnaire central de persistance et de chargement des données (Data Manager).
 * Cette classe utilise la bibliothèque Google Gson pour sérialiser et désérialiser au format JSON
 * les différentes structures de données du bot (rappels, équipes, nouveautés, comptes liés, etc.).
 * Elle intègre également des adaptateurs de types personnalisés pour gérer proprement {@link LocalDateTime} et {@link Color}.
 */
public class DataManager {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");

    /**
     * Instance partagée et configurée de {@link Gson}.
     * Enregistre des adaptateurs spécifiques pour formater les dates au format "JJ:MM:AAAA HH:mm",
     * convertir les objets {@link Color} en valeurs entières RGB (et inversement),
     * et activer l'affichage indenté (Pretty Printing).
     */
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.format(formatter));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString(), formatter);
                }
            })
            .registerTypeAdapter(Color.class, (JsonSerializer<Color>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.getRGB()))
            .registerTypeAdapter(Color.class, (JsonDeserializer<Color>) (json, typeOfT, context) ->
                    new Color(json.getAsInt()))
            .setPrettyPrinting()
            .create();

    //RAPPELS
    private static final File FILE_RAPPELS = new File("src/main/data/rappels.json");

    /**
     * Sauvegarde la liste des rappels actifs dans le fichier JSON correspondant.
     *
     * @param rappels La liste {@link ArrayList} de {@link Rappel} à enregistrer.
     */
    public static void saveRappels(ArrayList<Rappel> rappels) {
        if (rappels == null) return;
        try (Writer writer = new FileWriter(FILE_RAPPELS)) {
            gson.toJson(rappels, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge la liste des rappels depuis le fichier de stockage JSON.
     * Sécurisé contre les fichiers manquants, vides ou corrompus.
     *
     * @return Une {@link ArrayList} contenant les {@link Rappel} sauvegardés, ou une liste vide en cas d'erreur ou d'absence de données.
     */
    public static ArrayList<Rappel> loadRappels() {
        try {
            if (!FILE_RAPPELS.exists() || FILE_RAPPELS.length() == 0) {
                return new ArrayList<>(); // Retourne une liste vide, pas null !
            }
            Reader reader = new FileReader(FILE_RAPPELS);
            ArrayList<Rappel> list = gson.fromJson(reader, new TypeToken<ArrayList<Rappel>>(){}.getType());
            reader.close();

            // Sécurité supplémentaire : si le JSON était bizarre et a renvoyé null
            return (list != null) ? list : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Erreur de chargement, retour d'une liste vide.");
            return new ArrayList<>(); // Retourne une liste vide en cas de crash
        }
    }


    //EQUIPE
    private static final File FILE_EQUIPES = new File("src/main/data/equipes.json");

    /**
     * Sauvegarde la liste des équipes enregistrées dans le fichier JSON dédié.
     *
     * @param list La liste {@link ArrayList} de {@link Equipe} à enregistrer.
     */
    public static void saveEquipes(ArrayList<Equipe> list) {
        try (Writer w = new FileWriter(FILE_EQUIPES)) { gson.toJson(list, w); } catch (IOException e) {}
    }

    /**
     * Charge la liste des équipes depuis le fichier JSON.
     *
     * @return Une {@link ArrayList} contenant les {@link Equipe}, ou une liste vide si le fichier n'existe pas ou est illisible.
     */
    public static ArrayList<Equipe> loadEquipes() {
        if (!FILE_EQUIPES.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(FILE_EQUIPES)) {
            return gson.fromJson(r, new TypeToken<ArrayList<Equipe>>(){}.getType());
        } catch (Exception e) { return new ArrayList<>(); }
    }


    //NOUVEAUTE
    private static final File FILE_NOUVEAUTE = new File("src/main/data/new.json");

    /**
     * Sauvegarde la liste des nouveautés / annonces du bot dans le fichier JSON dédié.
     *
     * @param list La liste {@link ArrayList} de {@link StrucNew} à enregistrer.
     */
    public static void saveNew(ArrayList<StrucNew> list) {
        try (Writer w = new FileWriter(FILE_NOUVEAUTE)) { gson.toJson(list, w); } catch (IOException e) {}
    }

    /**
     * Charge la liste des nouveautés enregistrées depuis le fichier JSON.
     *
     * @return Une {@link ArrayList} contenant les structures de nouveautés {@link StrucNew}, ou une liste vide par défaut.
     */
    public static ArrayList<StrucNew> loadNew() {
        if (!FILE_NOUVEAUTE.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(FILE_NOUVEAUTE)) {
            return gson.fromJson(r, new TypeToken<ArrayList<StrucNew>>(){}.getType());
        } catch (Exception e) { return new ArrayList<>(); }
    }


    //VALODIS
    private static final File FILE_VALODIS = new File("src/main/data/ValoDis.json");

    /**
     * Sauvegarde les associations entre comptes Valorant et comptes Discord dans le fichier JSON dédié.
     *
     * @param list La liste {@link ArrayList} de {@link CompteValoDiscord} à enregistrer.
     */
    public static void saveValoDis(ArrayList<CompteValoDiscord> list) {
        try (Writer w = new FileWriter(FILE_VALODIS)) { gson.toJson(list, w); } catch (IOException e) {}
    }

    /**
     * Charge la liste des liaisons de comptes Valorant-Discord depuis le fichier de stockage.
     *
     * @return Une {@link ArrayList} de {@link CompteValoDiscord}, ou une liste vide si aucune liaison n'existe.
     */
    public static ArrayList<CompteValoDiscord> loadValoDis() {
        if (!FILE_VALODIS.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(FILE_VALODIS)) {
            return gson.fromJson(r, new TypeToken<ArrayList<CompteValoDiscord>>(){}.getType());
        } catch (Exception e) { return new ArrayList<>(); }
    }


    //TRACKER
    private static final File FILE_TRACKER = new File("src/main/data/trackerValo.json");

    /**
     * Sauvegarde la liste des joueurs suivis par le système de tracker Valorant.
     *
     * @param list La liste {@link ArrayList} de {@link TrackedPlayer} à persister.
     */
    public static void saveTrackedPlayer(ArrayList<TrackedPlayer> list) {
        try (Writer w = new FileWriter(FILE_TRACKER)) { gson.toJson(list, w); } catch (IOException e) {}
    }

    /**
     * Charge la liste des joueurs suivis par le tracker depuis le fichier JSON.
     *
     * @return Une {@link ArrayList} contenant les joueurs suivis ({@link TrackedPlayer}), ou une liste vide.
     */
    public static ArrayList<TrackedPlayer> loadTrackedPlayer() {
        if (!FILE_TRACKER.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(FILE_TRACKER)) {
            return gson.fromJson(r, new TypeToken<ArrayList<TrackedPlayer>>(){}.getType());
        } catch (Exception e) { return new ArrayList<>(); }
    }


    //FILE_GAME
    private static final File FILE_GAMES = new File("src/main/data/games.json");

    /**
     * Sauvegarde la liste de configuration des jeux supportés sur le serveur.
     *
     * @param list La liste {@link ArrayList} d'objets {@link Games} à enregistrer.
     */
    public static void saveGamesList(ArrayList<Games> list) {
        try (Writer w = new FileWriter(FILE_GAMES)) { gson.toJson(list, w); } catch (IOException e) {}
    }

    /**
     * Charge la liste complète des jeux configurés depuis le fichier de stockage JSON.
     *
     * @return Une {@link ArrayList} contenant les configurations {@link Games}, ou une liste vide par défaut.
     */
    public static ArrayList<Games> loadGamesList() {
        if (!FILE_GAMES.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(FILE_GAMES)) {
            return gson.fromJson(r, new TypeToken<ArrayList<Games>>(){}.getType());
        } catch (Exception e) { return new ArrayList<>(); }
    }


    /**
     * Récupère l'instance globale configurée de Gson pour des opérations de sérialisation externes.
     *
     * @return L'instance configurée de {@link Gson}.
     */
    public static Gson getGson() {
        return gson;
    }
}
