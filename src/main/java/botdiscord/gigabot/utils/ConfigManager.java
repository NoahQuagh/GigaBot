package botdiscord.gigabot.utils;

import com.moandjiezana.toml.Toml;

import java.io.File;

/**
 * Gestionnaire de configuration pour le bot Discord.
 * Cette classe permet de charger, de lire et de mettre à jour les paramètres du bot
 * stockés dans un fichier au format TOML (comme les tokens, préfixes, ou identifiants).
 */
public class ConfigManager {

    private Toml toml;

    /**
     * Constructeur de la configuration du bot.
     * Initialise et lit le fichier TOML spécifié pour charger les paramètres en mémoire.
     * @param file Le fichier de configuration au format .toml à charger.
     */
    public ConfigManager(File file) {
        this.toml = new Toml().read(file);
    }

    /**
     * Récupère l'objet de configuration TOML actuel.
     * Permet d'extraire facilement des valeurs ou des blocs de configuration spécifiques du fichier.
     * @return L'objet Toml contenant l'ensemble des données de configuration.
     */
    public Toml getToml() {
        return toml;
    }

    /**
     * Redéfinit ou met à jour la configuration TOML actuelle en mémoire.
     * @param toml Le nouvel objet Toml contenant la configuration mise à jour.
     */
    public void setToml(Toml toml) {
        this.toml = toml;
    }
}
