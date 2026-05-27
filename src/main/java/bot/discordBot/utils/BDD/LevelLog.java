package bot.discordBot.utils.BDD;

public enum LevelLog {
    OK,
    WARN,
    ERR;

    @Override
    public String toString() {
        return name();
    }
}
