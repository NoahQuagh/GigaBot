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
import static bot.discordBot.utils.Success.success.EventSuccess;
import static bot.discordBot.utils.commands.Code.SYNTAXE_INCORRECTE;
import static bot.discordBot.utils.commands.datamanager.DataStructure.Equipe.getTeamNameByIdCapitaine;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierSuprimerTeam extends CommandPremier {
    @Override
    public void run(CommandContext ctx, Command command, String[] args){
        if (ctx.isSlash()) ctx.defer();
        try {
            String team = getTeamNameByIdCapitaine(ctx.getAuthorId());
            if (team==null) throw new EquipeException(ctx,"Il existe aucune team Premier dont vous êtes le capitaine");

            execute(ctx,team);

        }catch (SyntaxeException e){
            writeLogFile("logs.txt",ctx.getAuthorName()+" | Code : "+ SYNTAXE_INCORRECTE);
        }catch (EquipeException | CapitaineException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE+" : "+e);
        }catch (Exception e){
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
            ExceptionDefault(ctx,"Impossible de supprimé la team Premier "+getTeamNameByIdCapitaine(ctx.getAuthorId()));
        }
    }

    public void execute(CommandContext ctx,String team)throws EquipeException{
        ArrayList<Equipe> equipes = DataManager.loadEquipes();
        boolean aEteSupprime = equipes.removeIf(e -> e.getEquipeId().equalsIgnoreCase(team));

        if (aEteSupprime) {
            DataManager.saveEquipes(equipes);
            EventSuccess(ctx,"Suppression réussie","L'équipe **" + team.toUpperCase() + "** a été supprimée");
            writeLogFile("logs.txt", "The team " + team + " has been deleted");
        } else throw new EquipeException(ctx, "Impossible de supprimer cette team");
    }
}
