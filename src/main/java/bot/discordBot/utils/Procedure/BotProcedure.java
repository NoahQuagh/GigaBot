package bot.discordBot.utils.Procedure;

import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.StrucNew;

import java.util.ArrayList;

import static bot.discordBot.Main.version;

/**
 * Classe utilitaire gérant les procédures liées au cycle de vie du bot Discord,
 * notamment la vérification et la récupération des informations de mise à jour et de version.
 */
public class BotProcedure {
    /**
     * Récupère la version actuelle globale sous laquelle s'exécute le bot.
     *
     * @return La chaîne de caractères représentant la version courante (ex: "1.0.0").
     */
    public static String DerniereVersionBot(){
        return version;
    }

    /**
     * Vérifie si une version spécifique du bot possède un historique de nouveautés ou de modifications enregistré.
     * Parcourt la liste des nouveautés chargées depuis le gestionnaire de données.
     *
     * @param version Le numéro de version à analyser (ex: "1.2.0").
     * @return {@code true} si la version existe dans les données enregistrées, sinon {@code false}.
     */
    public static boolean cetteVersionDuBotExiste(String version){
        ArrayList<StrucNew> listeNouveauteParVersion= DataManager.loadNew();
        for(StrucNew NouveauteDeCetteVersion : listeNouveauteParVersion){
            if(NouveauteDeCetteVersion.getVersion().equals(version)){
                return true;
            }
        }
        return false;
    }

    /**
     * Recherche et renvoie les détails et notes de mise à jour associés à une version spécifique du bot.
     *
     * @param version Le numéro de la version recherchée.
     * @return L'objet {@link StrucNew} contenant les informations et nouveautés de cette version,
     * ou {@code null} si aucune correspondance n'est trouvée.
     */
    public static StrucNew rechercherInfoVersionBot(String version){
        ArrayList<StrucNew> listeNouveauteParVersion= DataManager.loadNew();
        for(StrucNew NouveauteDeCetteVersion : listeNouveauteParVersion){
            if(NouveauteDeCetteVersion.getVersion().equals(version)){
                return NouveauteDeCetteVersion;
            }
        }
        return null;
    }
}
