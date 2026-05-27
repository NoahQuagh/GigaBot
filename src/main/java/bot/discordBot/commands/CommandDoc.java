package bot.discordBot.commands;

import bot.discordBot.utils.Exception.DefaultException;
import bot.discordBot.utils.commands.*;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandDoc implements CommandExecutor {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try {
            String htmlContent = Files.readString(Path.of("doc.html"));
            String jsonContent = Files.readString(Path.of("src/main/data/new.json"));


            // 2. Injecter le JSON dans le HTML
            // On remplace le commentaire balise par le vrai contenu JSON
            String finalHtml = htmlContent.replace("[ /* JSON_HERE */ ]", jsonContent);

            // 3. Envoyer le résultat comme un fichier unique
            FileUpload file = FileUpload.fromData(finalHtml.getBytes(StandardCharsets.UTF_8), "documentation.html");

            ctx.getEvent().getHook().sendMessage("☝️🤓 Voici la documentation à jour :")
                    .addFiles(file)
                    .queue();

            writeLogFile("logs.txt",ctx.getAuthorName()+" : a demandé la documentation");

        } catch (Exception e) {
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
            ExceptionDefault(ctx,"Une erreur est survenue lors de l'envoi du fichier");
        }

    }
}
