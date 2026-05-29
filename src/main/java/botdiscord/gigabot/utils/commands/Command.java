package botdiscord.gigabot.utils.commands;

/**
 * Représente une commande du bot Discord.
 * Cette classe encapsule l'identifiant unique de la commande, ses alias textuels
 * ainsi que l'exécuteur contenant la logique métier à exécuter lors de son déclenchement.
 */
public class Command {

    private String id;
    private String[] aliases;
    private CommandExecutor executor;

    /**
     * Instancie une nouvelle commande avec un identifiant, un exécuteur et ses alias.
     *
     * @param id       L'identifiant unique de la commande.
     * @param executor L'instance de {@link CommandExecutor} chargée de traiter la commande.
     * @param aliases  Un tableau ou une liste d'arguments variables (Varargs) représentant
     * les alias textuels secondaires de la commande.
     */
    public Command(String id,CommandExecutor executor, String... aliases) {
        this.id = id;
        this.aliases = aliases;
        this.executor = executor;
    }

    /**
     * Récupère l'identifiant unique de la commande.
     *
     * @return L'identifiant (ID) sous forme de chaîne de caractères.
     */
    public String getId() {
        return id;
    }

    /**
     * Récupère l'ensemble des alias associés à cette commande.
     *
     * @return Un tableau de chaînes de caractères ({@code String[]}) contenant les alias.
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Récupère l'exécuteur associé à la commande contenant la logique d'exécution.
     *
     * @return L'instance de {@link CommandExecutor} correspondante.
     */
    public CommandExecutor getExecutor() {
        return executor;
    }
}
