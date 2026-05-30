package botdiscord.gigabot.commandsBot.sousCmdPremierValorant;

import botdiscord.gigabot.commandsBot.cmd.CommandPremier;
import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.equipe_DB;
import botdiscord.gigabot.utils.DB.log_DB;
import botdiscord.gigabot.utils.exception.CapitaineException;
import botdiscord.gigabot.utils.exception.EquipeException;
import botdiscord.gigabot.utils.exception.SyntaxeException;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;

import java.util.ArrayList;

import static botdiscord.gigabot.utils.exception.DefaultException.ExceptionDefault;
import static botdiscord.gigabot.utils.success.success.EventSuccess;

public class CommandPremierAddTeam extends CommandPremier {

    private log_DB logs;
    private equipe_DB equipes;

    public CommandPremierAddTeam(){
        this.logs=new log_DB();
        this.equipes=new equipe_DB();
    }

    @Override
    public void run(CommandContext ctx, Command command, String[] args){
        if (ctx.isSlash()) ctx.defer();
        try {
            String nomTeam = ctx.isSlash()
                    ? ctx.getOptionString("nom").orElseThrow(() -> new SyntaxeException(ctx, "/premier addteam <Compétitif/Entraînement>"))
                    : (args.length >= 2 ? args[1] : null);

            if (nomTeam == null) throw new SyntaxeException(ctx, "/premier addteam <Compétitif/Entraînement>");

            String idCapitaine = ctx.getAuthorId();

            if (equipes.estCapitaine(idCapitaine))throw new CapitaineException(ctx, "Vous possédez déjà une équipe Premier");
            if (equipes.estDansUneEquipe(idCapitaine)) throw new CapitaineException(ctx,"Vous faite déjà partie d'une équipe Premier");

            execute(ctx,idCapitaine,nomTeam);

        }catch (SyntaxeException | EquipeException | CapitaineException e){
            logs.writeLog(LevelLog.ERR,getClass().getName(),ctx.getAuthorName()+" syntaxe incorrecte : " + e.getMessage());
        } catch (Exception e){
            ExceptionDefault(ctx, "Impossible de créer une équipe Premier");
            logs.writeLog(LevelLog.ERR,getClass().getName(),ctx.getAuthorName()+" échec de la commande : " + e.getMessage());
        }
    }
    public void execute(CommandContext ctx,String idCapitaine,String nomTeam) throws EquipeException{
        equipes.createTeam(ctx,nomTeam,idCapitaine);
        logs.writeLog(LevelLog.ERR,getClass().getName(),"Nouvelle équipe créer : "+nomTeam);
        EventSuccess(ctx, "Création réussie", "Nouvelle team crée : **" + nomTeam.toUpperCase() + "**");
    }
}
