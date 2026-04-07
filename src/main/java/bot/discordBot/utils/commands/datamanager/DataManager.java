package bot.discordBot.utils.commands.datamanager;

import bot.discordBot.utils.commands.datamanager.DataStructure.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.google.gson.*;
import java.time.format.DateTimeFormatter;
import java.lang.reflect.Type;

public class DataManager {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");
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
            .setPrettyPrinting()
            .create();


    private static final File FILE_RAPPELS = new File("src/main/data/rappels.json");
    // SAUVEGARDER RAPPELS
    public static void saveRappels(ArrayList<Rappel> rappels) {
        if (rappels == null) return;
        try (Writer writer = new FileWriter(FILE_RAPPELS)) {
            gson.toJson(rappels, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // RAPPELS RAPPELS
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



    private static final File FILE_EQUIPES = new File("src/main/data/equipes.json");
    // SAUVEGARDER // SAUVEGARDER RAPPELS
    public static void saveEquipes(ArrayList<Equipe> list) {
        try (Writer w = new FileWriter(FILE_EQUIPES)) { gson.toJson(list, w); } catch (IOException e) {}
    }
    // RAPPELS // SAUVEGARDER RAPPELS
    public static ArrayList<Equipe> loadEquipes() {
        if (!FILE_EQUIPES.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(FILE_EQUIPES)) {
            return gson.fromJson(r, new TypeToken<ArrayList<Equipe>>(){}.getType());
        } catch (Exception e) { return new ArrayList<>(); }
    }


    private static final File FILE_NOUVEAUTE = new File("src/main/data/new.json");
    // SAUVEGARDER // SAUVEGARDER RAPPELS
    public static void saveNew(ArrayList<StrucNew> list) {
        try (Writer w = new FileWriter(FILE_NOUVEAUTE)) { gson.toJson(list, w); } catch (IOException e) {}
    }
    // RAPPELS // SAUVEGARDER RAPPELS
    public static ArrayList<StrucNew> loadNew() {
        if (!FILE_NOUVEAUTE.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(FILE_NOUVEAUTE)) {
            return gson.fromJson(r, new TypeToken<ArrayList<StrucNew>>(){}.getType());
        } catch (Exception e) { return new ArrayList<>(); }
    }

    private static final File FILE_VALODIS = new File("src/main/data/ValoDis.json");
    // SAUVEGARDER // SAUVEGARDER RAPPELS
    public static void saveValoDis(ArrayList<CompteValoDiscord> list) {
        try (Writer w = new FileWriter(FILE_VALODIS)) { gson.toJson(list, w); } catch (IOException e) {}
    }
    // RAPPELS // SAUVEGARDER RAPPELS
    public static ArrayList<CompteValoDiscord> loadValoDis() {
        if (!FILE_VALODIS.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(FILE_VALODIS)) {
            return gson.fromJson(r, new TypeToken<ArrayList<CompteValoDiscord>>(){}.getType());
        } catch (Exception e) { return new ArrayList<>(); }
    }

    private static final File FILE_TRACKER = new File("src/main/data/trackerValo.json");
    // SAUVEGARDER // SAUVEGARDER RAPPELS
    public static void saveTrackedPlayer(ArrayList<TrackedPlayer> list) {
        try (Writer w = new FileWriter(FILE_TRACKER)) { gson.toJson(list, w); } catch (IOException e) {}
    }
    // RAPPELS // SAUVEGARDER RAPPELS
    public static ArrayList<TrackedPlayer> loadTrackedPlayer() {
        if (!FILE_TRACKER.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(FILE_TRACKER)) {
            return gson.fromJson(r, new TypeToken<ArrayList<TrackedPlayer>>(){}.getType());
        } catch (Exception e) { return new ArrayList<>(); }
    }


    public static Gson getGson() {
        return gson;
    }
}
