package bot.discordBot.utils.commands.datamanager;

import java.io.*;

public class Fichier {
    public static void createFile(String nom){
        File file = new File(nom);
        try{
            if(file.createNewFile()){
                System.out.println("File created : "+file.getName());
            }else{
                System.out.println("File already exist : "+file.getName());
            }
        }catch (IOException e){
            System.out.println("The application encountered an error while creating the file : "+file.getName()+" >> "+e);
        }
    }

    public static void writeFile(String nom,String text){
        try(BufferedWriter writer =new BufferedWriter(new FileWriter(nom,true))){
            writer.write(text);
            writer.newLine();
        }catch (IOException e){
            System.out.println("File writing or opening failed : "+nom+" >> "+e);
        }
    }

    public static String readFile(String nom){
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(nom))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("File reading or opening failed : " + nom + " >> " + e);
            return "";
        }
        return content.toString();
    }

    public static void clearFile(String nom){
        try(BufferedWriter writer =new BufferedWriter(new FileWriter(nom))){
        }catch (IOException e){
            System.out.println("File clearing or opening failed : "+nom+" >> "+e);
        }
    }
}
