package botdiscord.gigabot.commandsBot.sousCmdPremierValorant;

import botdiscord.gigabot.commandsBot.cmd.CommandPremier;
import botdiscord.gigabot.utils.DB.enumDB.LevelLog;
import botdiscord.gigabot.utils.DB.equipe_DB;
import botdiscord.gigabot.utils.DB.event_DB;
import botdiscord.gigabot.utils.DB.log_DB;
import botdiscord.gigabot.utils.DB.structure.JoueurPremier;
import botdiscord.gigabot.utils.DB.structure.Rappel;
import botdiscord.gigabot.utils.commands.Command;
import botdiscord.gigabot.utils.commands.CommandContext;
import botdiscord.gigabot.utils.exception.EquipeException;
import botdiscord.gigabot.utils.exception.RappelException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static botdiscord.gigabot.Main.jda;
import static botdiscord.gigabot.utils.exception.DefaultException.ExceptionDefault;
import static botdiscord.gigabot.utils.success.success.EventSuccess;

public class CommandPremierCancelEvent extends CommandPremier {
    log_DB logs;
    event_DB rappels;
    equipe_DB equipes;

    public CommandPremierCancelEvent(){
        this.logs=new log_DB();
        this.equipes=new equipe_DB();
        this.rappels=new event_DB();
    }

    @Override
    public void run(CommandContext ctx, Command command, String[] args){
        rappels.netoyageRappel();
        if (ctx.isSlash()) ctx.defer();
        try {
            if (equipes.estCapitaine(ctx.getAuthorId()))
                throw new EquipeException(ctx, "Il existe aucune team Premier dont vous êtes le capitaine");

            String team = equipes.getTeamNameByCapitaineId(ctx.getAuthorId());
            ArrayList<JoueurPremier> listeJoueur = equipes.getJoueurPremierListByTeamName(team);

            ArrayList<Rappel> listeRappels = rappels.getEvent();
            if (listeRappels == null || listeRappels.isEmpty())
                throw new RappelException(ctx, "Aucun rappel n'est programmé.");

            execute(ctx, args[1] + ":" + args[2] + ":" + args[3], args[4] + ":" + args[5], equipes.getTeamNameByCapitaineId(ctx.getAuthorId()),0,"");
        }catch (EquipeException e){
            logs.writeLog(LevelLog.ERR,getClass().getName(),ctx.getAuthorName()+" aucune données trouver : " + e.getMessage());
        }catch (Exception e){
            ExceptionDefault(ctx, "Impossible de supprimer le rappel ( vérifiez la date ) ");
            logs.writeLog(LevelLog.ERR,getClass().getName()," Erreur lors de la suppression du rappel : " + e.getMessage());
        }
    }

    public void execute(CommandContext ctx, String date, String heure, String team,int n,String salonid){;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");
        LocalDateTime dateTimeEvent = LocalDateTime.parse(date+ " " + heure, formatter);

        rappels.deleteRappelForTeam(equipes.getTeamIdByTeamName(team),dateTimeEvent);
        ArrayList<JoueurPremier> listeJoueurs = equipes.getJoueurPremierListByTeamName(team);

        for (JoueurPremier Joueur : listeJoueurs) {
            String idJoueur = Joueur.getDiscordId();
            if (idJoueur == null) continue;

            String cle = idJoueur.trim() + ":" + dateTimeEvent;

            java.util.concurrent.ScheduledFuture<?> taskToCancel = CommandPremierEvent.tachesActives.get(cle);


            if (taskToCancel != null) {
                taskToCancel.cancel(false);
                CommandPremierEvent.tachesActives.remove(cle);

            }
        }
        if(ctx == null){
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("✅ Évènement annulé")
                    .setDescription("L'évènement du "+date.replace(":","/")+" à "+heure+" de la team "+team+" est annulé par manque de joueur.")
                    .setColor(Color.GREEN);
            GuildMessageChannel channel = jda.getChannelById(GuildMessageChannel.class, salonid);
            channel.sendMessageEmbeds(embed.build()).queue();
            return;
        }
        if(n==1){
            EventSuccess(ctx,"Évènement annulé","L'évènement du "+heure.replace(":","/")+" à "+date+" est annulé par manque de joueur");
        }else {
            EventSuccess(ctx,"Évènement annulé","L'évènement du "+heure.replace(":","/")+" à "+date+" est annulé");
        }
    }
}
