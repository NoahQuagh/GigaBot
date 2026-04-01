package bot.discordBot.utils.commands.datamanager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static bot.discordBot.utils.commands.datamanager.Fichier.*;

public class logManager {

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

    public static void readLogFile(String fileName){
        readFile(fileName);
    }

    public static void clearLogFile(String fileName){
        clearFile(fileName);
    }
}
