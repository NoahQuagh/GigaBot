package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.Exception.CapitaineException;
import bot.discordBot.utils.Exception.JoueurException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;


import java.util.HashMap;
import java.util.List;

import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.Success.success.EventSuccess;
import static bot.discordBot.utils.commands.datamanager.DataStructure.Equipe.*;
import static bot.discordBot.utils.commands.datamanager.DataStructure.Equipe.successionCapitaine;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierStratAgent extends CommandPremier {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try {
            String idCapitaine = ctx.getAuthorId();

            if (CapitaineNaPasDEquipe(idCapitaine))
                throw new CapitaineException(ctx, "Vous ne posséder pas de team Premier");
            String team = getTeamNameByIdCapitaine(idCapitaine);


            //EventSuccess(ctx, "Succession  réussie", "**" + joueur + "** est désormais capitaine de la team Premier **" + team.toUpperCase() + "**");
            //writeLogFile("logs.txt","Joueur " + joueur + " est capitaine de la team Premier " + team);
        }catch (JoueurException | CapitaineException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE+" : "+e);
        }catch (Exception e){
            ExceptionDefault(ctx,"Impossible de créer une stratégie d'agent pour votre Team Premier");
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
        }
    }
}
