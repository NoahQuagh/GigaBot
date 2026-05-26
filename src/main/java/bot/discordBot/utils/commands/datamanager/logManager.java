package bot.discordBot.utils.commands.datamanager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static bot.discordBot.utils.commands.datamanager.Fichier.*;

/**
 * Gestionnaire de journalisation (logs) du bot.
 * Cette classe fournit des méthodes utilitaires permettant d'écrire, de lire et de vider
 * des fichiers de logs en y incluant automatiquement des métadonnées contextuelles précises
 * (horodatage, nom de la classe, méthode appelante et chemin physique).
 */
public class logManager {

    /**
     * Enregistre une entrée de journal (log) formatée dans un fichier spécifique.
     * Cette méthode inspecte la pile d'exécution (StackTrace) pour identifier automatiquement
     * la classe et la méthode qui ont demandé l'écriture du log, capture le moment précis de l'appel,
     * puis construit et écrit la ligne finale via {@link Fichier#writeFile(String, String)}.
     *
     * @param fileName Le nom ou le chemin du fichier cible dans lequel écrire le log (ex: "logs.txt").
     * @param text     Le message d'erreur, d'avertissement ou d'information personnalisé à consigner.
     */
    public static void writeLogFile(String fileName,String text){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
        String className = caller.getClassName();
        String physicalPath = "Inconnu";

        try {
            Class<?> callerClass = Class.forName(className);
            physicalPath = callerClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        } catch (ClassNotFoundException e) {}

        String timestamp = LocalDateTime.now().format(formatter);
        String methodName = caller.getMethodName();


        String rep ="[" + timestamp + "] | "+physicalPath+""+className+"."+methodName+"() >> "+text;
        writeFile(fileName,rep);
    }

    /**
     * Lit et affiche le contenu complet d'un fichier de logs spécifié dans la console.
     * Repose en interne sur la méthode {@link Fichier#readFile(String)}.
     *
     * @param fileName Le nom ou le chemin du fichier de logs à lire.
     */
    public static void readLogFile(String fileName){
        readFile(fileName);
    }

    /**
     * Vide intégralement le contenu d'un fichier de logs spécifié.
     * Utile pour réinitialiser les fichiers de suivi de manière programmatique.
     *
     * @param fileName Le nom ou le chemin du fichier de logs à purger.
     */
    public static void clearLogFile(String fileName){
        clearFile(fileName);
    }
}
