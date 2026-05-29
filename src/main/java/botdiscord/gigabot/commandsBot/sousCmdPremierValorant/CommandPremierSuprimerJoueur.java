package botdiscord.gigabot.commandsBot.sousCmdPremierValorant;

import botdiscord.gigabot.commandsBot.cmd.CommandPremier;
import botdiscord.gigabot.utils.exception.CapitaineException;
import botdiscord.gigabot.utils.exception.JoueurException;
import bot.discordBot.utils.commands.Code;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Equipe;
import net.dv8tion.jda.api.entities.User;


import java.util.List;

import static botdiscord.gigabot.utils.exception.DefaultException.ExceptionDefault;
import static botdiscord.gigabot.utils.success.success.EventSuccess;
import static bot.discordBot.utils.commands.datamanager.DataStructure.Equipe.*;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierSuprimerJoueur extends CommandPremier {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try {
            List<User> joueursMentioned = ctx.getMentionedUsers();
            String idJoueur = joueursMentioned.getFirst().getId();
            String idCapitaine = ctx.getAuthorId();
            String joueur = joueursMentioned.getFirst().getName();

            if (CapitaineNaPasDEquipe(idCapitaine))
                throw new CapitaineException(ctx, "Vous ne posséder pas de team Premier");
            String team = getTeamNameByIdCapitaine(idCapitaine);
            if (!(estDansCetteEquipe(team, idJoueur)))
                throw new JoueurException(ctx, "Ce joueur ne fait pas parti de votre team Premier");
            if (!(removeJoueurByIdCapitaineIdJoueur(idCapitaine, idJoueur)))
                throw new JoueurException(ctx, "Impossible de supprimer ce joueur de votre team Premier");

            EventSuccess(ctx, "Suppression réussie", "**" + joueur + "** a été supprimé de la team Premier **" + team.toUpperCase() + "**");
            writeLogFile("logs.txt","Joueur " + joueur + " supprimé de la team Premier " + team);

        }catch (JoueurException | CapitaineException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE+" : "+e);
        }catch (Exception e){
            ExceptionDefault(ctx,"Impossible de supprimer ce joueur de votre Team Premier");
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
        }
    }
}
