package bot.discordBot.utils.commands.datamanager;

import java.io.*;

/**
 * Classe utilitaire permettant de gérer les opérations d'entrée/sortie (I/O) sur les fichiers du système.
 * Elle centralise la création, l'écriture séquentielle, la lecture complète ainsi que la purge
 * de fichiers textes utilisés pour le stockage des données ou des fichiers de journalisation.
 */
public class Fichier {

    /**
     * Crée un nouveau fichier physique sur le disque s'il n'existe pas déjà.
     * Affiche un message de confirmation dans la console standard en cas de succès,
     * d'existence préalable ou d'anomalie.
     *
     * @param nom Le nom ou le chemin d'accès du fichier à créer (ex: "donnees.txt").
     */
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

    /**
     * Écrit une ligne de texte à la fin d'un fichier spécifié (mode Append).
     * Si le fichier contient déjà des données, la nouvelle ligne est ajoutée sans écraser le contenu existant.
     * Un saut de ligne automatique est inséré après le texte.
     *
     * @param nom  Le nom ou le chemin du fichier cible.
     * @param text Le texte à inscrire dans le fichier.
     */
    public static void writeFile(String nom,String text){
        try(BufferedWriter writer =new BufferedWriter(new FileWriter(nom,true))){
            writer.write(text);
            writer.newLine();
        }catch (IOException e){
            System.out.println("File writing or opening failed : "+nom+" >> "+e);
        }
    }

    /**
     * Lit l'intégralité du contenu d'un fichier texte et le retourne sous forme d'une chaîne de caractères.
     * Chaque ligne lue est séparée par un retour à la ligne (`\n`).
     *
     * @param nom Le nom ou le chemin du fichier à lire.
     * @return Le contenu complet du fichier sous forme de {@link String}, ou une chaîne vide {@code ""} en cas d'erreur.
     */
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

    /**
     * Purge et vide l'intégralité du contenu d'un fichier donné sans le supprimer.
     * Ouvre le fichier en mode d'écriture exclusif (sans l'option append), ce qui réinitialise sa taille à 0 octet.
     *
     * @param nom Le nom ou le chemin du fichier à vider.
     */
    public static void clearFile(String nom){
        try(BufferedWriter writer =new BufferedWriter(new FileWriter(nom))){
        }catch (IOException e){
            System.out.println("File clearing or opening failed : "+nom+" >> "+e);
        }
    }
}
