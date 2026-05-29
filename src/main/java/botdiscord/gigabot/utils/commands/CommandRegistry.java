package botdiscord.gigabot.utils.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Registre de centralisation et de gestion des commandes du bot.
 * Cette classe permet de stocker, d'ajouter, de supprimer et de rechercher
 * les commandes disponibles du bot via leur alias.
 */
public class CommandRegistry {
    private ArrayList<Command> commands;

    /**
     * Initialise un nouveau registre de commandes vierge.
     */
    public CommandRegistry() {
        this.commands = new ArrayList<>();
    }

    /**
     * Récupère la liste complète des commandes actuellement enregistrées dans le bot.
     *
     * @return Une {@link ArrayList} contenant toutes les instances de {@link Command}.
     */
    public ArrayList<Command> getCommands() {
        return this.commands;
    }

    /**
     * Ajoute une nouvelle commande au registre du bot.
     *
     * @param cmd L'instance de la commande à inscrire.
     */
    public void addCommand(Command cmd){
        commands.add(cmd);
    }

    /**
     * Supprime une commande du registre en fonction de son identifiant unique.
     * La comparaison est insensible à la casse.
     *
     * @param id L'identifiant unique (ID) de la commande à retirer.
     */
    public void removeCommand(String id){
        commands.removeIf((cmd)-> cmd.getId().equalsIgnoreCase(id));
    }

    /**
     * Recherche une commande enregistrée à partir d'un alias saisi par l'utilisateur.
     * Parcourt les alias de chaque commande pour trouver une correspondance.
     * * @note Si le nombre de commandes dépasse 100,migrer
     * cette structure vers une {@code HashMap<String, Command>} pour optimiser les performances de recherche.
     *
     * @param alias L'alias ou le nom de la commande recherchée.
     * @return Un {@link Optional} contenant la {@link Command} si elle existe,
     * ou {@link Optional#empty()} si aucune commande ne correspond à cet alias.
     */
    public Optional<Command> getByAlias(String alias){
        for(Command command : commands){
            if(Arrays.asList(command.getAliases()).contains(alias)){
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }
}
