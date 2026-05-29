package botdiscord.gigabot.utils.DB.enumDB;

public enum LevelLog {
    OK,
    WARN,
    ERR;

    @Override
    public String toString() {
        return name();
    }
}
