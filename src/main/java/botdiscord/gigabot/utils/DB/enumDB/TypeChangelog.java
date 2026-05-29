package botdiscord.gigabot.utils.DB.enumDB;

public enum TypeChangelog {
    nouveaute,
    bug;

    @Override
    public String toString() {
        return name();
    }
}
