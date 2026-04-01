package bot.discordBot.utils.commands.datamanager.DataStructure;

import java.time.LocalDateTime;

public class Rappel {
    private String userId;
    private LocalDateTime date;

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getDate() {
        return date;
    }


    public Rappel(String userId, LocalDateTime date) {
        this.userId = userId;
        this.date = date;
    }
}
