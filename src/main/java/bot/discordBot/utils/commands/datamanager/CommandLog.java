package bot.discordBot.utils.commands.datamanager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandLog {

    private static final String LOG_FILE = "logs.txt";

    public CommandLog(String command, String auteur) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = "[" + timestamp + "] USER: " + auteur + " | CMD: " + command;
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(logEntry);
            bw.newLine();

        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du log : " + e.getMessage());
        }
    }
}
