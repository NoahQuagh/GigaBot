package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.Exception.CapitaineException;
import bot.discordBot.utils.Exception.EquipeException;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Equipe;

import java.util.ArrayList;

import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.Procedure.EquipeProcedure.estDejaCapitaineDuneEquipe;
import static bot.discordBot.utils.Procedure.EquipeProcedure.nomEquipteUtiliser;
import static bot.discordBot.utils.Success.success.EventSuccess;
import static bot.discordBot.utils.commands.Code.SYNTAXE_INCORRECTE;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierAddTeam extends CommandPremier {
    @Override
    public void run(CommandContext ctx, Command command, String[] args){
        if (ctx.isSlash()) ctx.defer();
        try {
            String nomTeam = ctx.isSlash()
                    ? ctx.getOptionString("nom").orElseThrow(() -> new SyntaxeException(ctx, "/premier addteam <nomTeam>"))
                    : (args.length >= 2 ? args[1] : null);

            if (nomTeam == null) throw new SyntaxeException(ctx, "/premier addteam <nomTeam>");

            String idCapitaine = ctx.getAuthorId();

            if (estDejaCapitaineDuneEquipe(idCapitaine))throw new CapitaineException(ctx, "Vous possédez déjà une team Premier");


            execute(ctx,idCapitaine,nomTeam);

        }catch (SyntaxeException e){
            writeLogFile("logs.txt", ctx.getAuthorName()+" | Code : "+ SYNTAXE_INCORRECTE);
        }catch (EquipeException | CapitaineException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE+" : "+e);
        } catch (Exception e){
            ExceptionDefault(ctx,"Impossible de crée une Team Premier");
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
        }
    }
    public void execute(CommandContext ctx,String idCapitaine,String nomTeam) throws EquipeException{
        if (nomEquipteUtiliser(nomTeam)) throw new EquipeException(ctx, "Ce nom de team existe déja");

        ArrayList<Equipe> equipe = new ArrayList<>();
        ArrayList<String> joueur = new ArrayList<>();

        joueur.add(idCapitaine);
        equipe.add(new Equipe(nomTeam, idCapitaine, joueur));

        DataManager.saveEquipes(equipe);

        writeLogFile("logs.txt", "new team created :" + nomTeam);

        EventSuccess(ctx, "Création réussie", "Nouvelle team crée : **" + nomTeam.toUpperCase() + "**");
    }
}
