package bot.discordBot.utils.commands;

public class Command {

    private String id;
    private String[] aliases;
    private CommandExecutor executor;

    public Command(String id,CommandExecutor executor, String... aliases) {
        this.id = id;
        this.aliases = aliases;
        this.executor = executor;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return executor.getDescription();
    }

    public String[] getAliases() {
        return aliases;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }
}
