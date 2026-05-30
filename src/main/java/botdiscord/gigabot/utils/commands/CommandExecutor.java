package botdiscord.gigabot.utils.commands;

import java.sql.SQLException;

/**
 * Interface fonctionnelle définissant le contrat d'exécution d'une commande du bot.
 * Toute commande ou sous-système devant traiter une action déclenchée par un utilisateur
 * doit implémenter cette interface.
 */
public interface CommandExecutor {
    /**
     * Déclenche l'exécution de la logique métier associée à une commande spécifique.
     *
     * @param ctx     Le contexte complet de la commande ({@link CommandContext}), contenant
     * les informations sur l'événement JDA, le salon, le serveur et l'auteur.
     * @param command L'instance de la commande ({@link Command}) qui a été interceptée et validée.
     * @param args    Un tableau de chaînes de caractères ({@code String[]}) représentant les
     * arguments ou options fournis par l'utilisateur lors de la saisie.
     */
    void run(CommandContext ctx, Command command,String[] args);
}
