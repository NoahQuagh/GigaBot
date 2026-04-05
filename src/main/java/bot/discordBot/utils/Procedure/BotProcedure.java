package bot.discordBot.utils.Procedure;

import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.StrucNew;

import java.util.ArrayList;

import static bot.discordBot.Main.version;

public class BotProcedure {
    public static String DerniereVersionBot(){
        return version;
    }

    public static boolean cetteVersionDuBotExiste(String version){
        ArrayList<StrucNew> listeNouveauteParVersion= DataManager.loadNew();
        for(StrucNew NouveauteDeCetteVersion : listeNouveauteParVersion){
            if(NouveauteDeCetteVersion.getVersion().equals(version)){
                return true;
            }
        }
        return false;
    }

    public static StrucNew rechercherInfoVersionBot(String version){
        ArrayList<StrucNew> listeNouveauteParVersion= DataManager.loadNew();
        for(StrucNew NouveauteDeCetteVersion : listeNouveauteParVersion){
            if(NouveauteDeCetteVersion.getVersion().equals(version)){
                return NouveauteDeCetteVersion;
            }
        }
        return null;
    }
}
