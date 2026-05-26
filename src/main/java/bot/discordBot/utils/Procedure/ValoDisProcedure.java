package bot.discordBot.utils.Procedure;

import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.CompteValoDiscord;
import bot.discordBot.utils.commands.datamanager.DataStructure.TrackedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe utilitaire gérant les procédures liées à l'association des comptes Valorant
 * avec les profils Discord, ainsi que la gestion de l'auto-complétion des pseudos dans le bot.
 */
public class ValoDisProcedure {
    /**
     * Vérifie si un pseudo Valorant spécifique existe déjà dans la base de données locale du bot.
     *
     * @param pseudo Le pseudo Valorant à rechercher.
     * @return {@code true} si le pseudo est déjà enregistré, sinon {@code false}.
     */
    public static boolean pseudoValoExist(String pseudo){
        ArrayList<CompteValoDiscord> listeCompteValoDis=DataManager.loadValoDis();
        if(listeCompteValoDis==null){return false;}
        for(CompteValoDiscord compte : listeCompteValoDis){
            if(compte.getPseudoValo().equals(pseudo)){
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si un nom d'utilisateur Discord est déjà associé à un compte enregistré.
     *
     * @param nom Le nom d'utilisateur Discord à vérifier.
     * @return {@code true} si le nom Discord existe dans les données, sinon {@code false}.
     */
    public static boolean nomDiscordExist(String nom){
        ArrayList<CompteValoDiscord> listeCompteValoDis=DataManager.loadValoDis();
        if(listeCompteValoDis==null){return false;}
        for(CompteValoDiscord compte : listeCompteValoDis){
            if(compte.getNomDiscord().equals(nom)){
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si un identifiant numérique unique Discord (ID) possède déjà un compte lié.
     *
     * @param id L'identifiant unique (Snowflake ID) de l'utilisateur Discord.
     * @return {@code true} si l'ID Discord est déjà enregistré, sinon {@code false}.
     */
    public static boolean idDiscordExist(String id){
        ArrayList<CompteValoDiscord> listeCompteValoDis=DataManager.loadValoDis();
        if(listeCompteValoDis==null){return false;}
        for(CompteValoDiscord compte : listeCompteValoDis){
            if(compte.getIdDiscord().equals(id)){
                return true;
            }
        }
        return false;
    }

    /**
     * Calcule et retourne le nombre total de comptes Valorant actuellement enregistrés dans le bot.
     *
     * @return Le nombre total de comptes liés, ou {@code 0} si la liste est vide ou inaccessible.
     */
    public static int nombreCompteEnregistrer(){
        ArrayList<CompteValoDiscord> listeCompteValoDis=DataManager.loadValoDis();
        if(listeCompteValoDis==null){return 0;}
        return listeCompteValoDis.size();
    }

    /**
     * Propose une valeur textuelle brute pour l'auto-complétion en fonction d'un index donné.
     * Récupère le pseudo correspondant à la position spécifiée dans la liste des comptes.
     *
     * @param n L'index du compte ciblé dans la liste.
     * @return Le pseudo Valorant trouvé à cet index, ou un message d'erreur si aucun compte n'est enregistré.
     */
    public static String proposerAutoCompleteValue(int n){
        ArrayList<CompteValoDiscord> listeCompteValoDis=DataManager.loadValoDis();
        if(listeCompteValoDis==null){return "Aucun compte à proposer";}
        return listeCompteValoDis.get(n).getPseudoValo();
    }

    /**
     * Génère une liste filtrée de suggestions de pseudos Valorant pour l'auto-complétion des commandes Discord.
     * Filtre les pseudos enregistrés en mémoire en ne gardant que ceux qui commencent par la saisie actuelle de l'utilisateur (insensible à la casse).
     * Limite automatiquement les résultats à 25 propositions maximum (contrainte imposée par l'API Discord).
     *
     * @param currentInput Le texte actuellement tapé par l'utilisateur dans le champ de la commande Discord.
     * @return Une liste de chaînes de caractères contenant les pseudos correspondants, limitée à 25 éléments.
     */
    public static List<String> getPseudosAutocomplete(String currentInput) {
        // Charge ton fichier ValoDis.json (utilise ta méthode habituelle pour lire le JSON)
        ArrayList<CompteValoDiscord> comptes = DataManager.loadValoDis();

        return comptes.stream()
                .map(CompteValoDiscord::getPseudoValo)
                .filter(pseudo->pseudo.toLowerCase().contains(currentInput.toLowerCase()))
                .limit(25)
                .collect(Collectors.toList());
    }
}
