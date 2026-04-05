package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.Exception.CapitaineException;
import bot.discordBot.utils.Exception.EquipeException;
import bot.discordBot.utils.Exception.JoueurException;
import bot.discordBot.utils.Exception.SyntaxeException;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Equipe;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static bot.discordBot.Main.api;
import static bot.discordBot.utils.Exception.DefaultException.ExceptionDefault;
import static bot.discordBot.utils.Procedure.EquipeProcedure.*;
import static bot.discordBot.utils.commands.Code.*;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierTeamInvite extends CommandPremier {
    @Override
    public void run(CommandContext ctx, Command command, String[] args) {
        if (ctx.isSlash()) ctx.defer();
        try {
            List<User> joueurs = ctx.getMentionedUsers();
            String team = getTeamNameByIdCapitaine(ctx.getAuthorId());
            if (team==null) throw new EquipeException(ctx,"Il existe aucune team Premier dont vous êtes le capitaine");
            if(NbJoueurMaxAtteint(ctx.getAuthorId())) throw new JoueurException(ctx, "Nombre de joueurs dans une team atteint");

            execute(ctx,joueurs,team);

        }catch (CapitaineException e){
            writeLogFile("logs.txt", "Code : " + ACCEE_REFUSE);
        }catch (SyntaxeException e){
            writeLogFile("logs.txt", ctx.getAuthorName()+" | Code : "+ SYNTAXE_INCORRECTE);
        }catch (JoueurException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE+" : "+e);
        }catch (IndexOutOfBoundsException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE+" : "+e);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible d'ajouter le joueur'.","@nomJoueur doit être une mention du joueur souhaité")
                    .setColor(Color.red);
            ctx.replyDeferred(embed);
        }catch (Exception e){
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
            ExceptionDefault(ctx, "Impossible d'ajouter le(s) joueur(s)");
        }
    }

    private void sendDemande(User user, String pseudo, String team,String idJoueur,CommandContext ctx){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Invitation dans la team Premier")
                .setDescription("Vous avez été invité dans une team de Premier **"+team.toUpperCase()+"**")
                .setColor(Color.CYAN);
        new org.javacord.api.entity.message.MessageBuilder()
                .setEmbed(embed)
                .addComponents(
                        org.javacord.api.entity.message.component.ActionRow.of(
                                org.javacord.api.entity.message.component.Button.create("event_yes"+ user.getId(), ButtonStyle.SUCCESS, "Accepter"),
                                Button.create("event_no"+ user.getId(), ButtonStyle.DANGER, "Refuser")
                        )
                )
                .send(user).thenAccept(message -> {
                    message.addButtonClickListener(clickEvent -> {
                        if (!clickEvent.getButtonInteraction().getUser().equals(user)) return;

                        String customId = clickEvent.getButtonInteraction().getCustomId();
                        var updater = message.createUpdater();

                        if (customId.startsWith("event_yes")) {
                            writeLogFile("logs.txt",pseudo+" | accepted the invitation to join the team Premier");
                            updater.setContent("✅ Invitation accepté est enregistrée ! Bienvenu dans la team Premier **"+team.toUpperCase()+"**")
                                    .removeAllEmbeds()
                                    .removeAllComponents()
                                    .applyChanges();


                            validerInvitation(team,idJoueur,pseudo,ctx);
                        } else {
                            writeLogFile("logs.txt",pseudo+" | refused the invitation to join the team Premier");
                            updater.setContent("❌ Invitation refusé dans la team Premier **"+team.toUpperCase()+"**")
                                    .removeAllEmbeds()
                                    .removeAllComponents()
                                    .applyChanges();

                            api.getUserById(ctx.getAuthorId()).thenAccept(chef -> {
                                chef.sendMessage("❌ **"+pseudo+"** à refusé l'invitation dans votre team **"+team.toUpperCase()+"**");
                            }).exceptionally(e -> {

                                writeLogFile("logs.txt","Code : "+ Code.AUCUNE_DONNEE_TROUVER+" : "+e);
                                ExceptionDefault(ctx,"Impossible d'envoyé une réponse au chef de la team");
                                return null;
                            });
                        }
                    }).removeAfter(1, TimeUnit.DAYS);
                }).exceptionally(e -> {
                    writeLogFile("logs.txt","Code : "+ ECHEC+" : "+e);
                    ExceptionDefault(ctx, "Impossible d'ajouter le(s) joueur(s)");
                    return null;
                });
    }

    private void validerInvitation(String team,String idJoueur,String pseudo,CommandContext ctx){

        api.getUserById(ctx.getAuthorId()).thenAccept(chef -> {
            chef.sendMessage("✅ **"+pseudo+"** à accepté l'invitation dans votre team **"+team.toUpperCase()+"**");
        }).exceptionally(e -> {

            writeLogFile("logs.txt","Code : "+ Code.AUCUNE_DONNEE_TROUVER+" : "+e);
            ExceptionDefault(ctx,"Impossible d'envoyé une réponse au chef de la team");
            return null;
        });
        try{
            ArrayList<Equipe> curseur = DataManager.loadEquipes();

            for (Equipe equipe : curseur) {
                if (equipe.getEquipeId().equalsIgnoreCase(team)) {
                    equipe.getJoueurIds().add(idJoueur);
                }
            }

            writeLogFile("logs.txt",pseudo+" is registered in the team : "+team);
            DataManager.saveEquipes(curseur);

        }catch (Exception e){
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
            ExceptionDefault(ctx, "Impossible d'ajouter le(s) joueur(s)");
        }
    }

    public void execute(CommandContext ctx,List<User> joueurs,String team){
        StringBuilder confirmation = new StringBuilder();

        for (User user : joueurs) {
            String idJoueur = user.getIdAsString();

            if (joueurDejaDansUneEquipe(idJoueur)) {
                confirmation.append("⚠️ **").append(user.getName()).append("** fait déjà partie d'une team.\n");
                continue;
            }

            String pseudo = user.getName();

            confirmation.append("✅ Invitation envoyée à **").append(pseudo).append("**\n");
            writeLogFile("logs.txt", pseudo + " | Invitation sent to join team " + team);
            sendDemande(user, pseudo, team, idJoueur, ctx);
        }

        ctx.replyDeferred(confirmation.toString());
    }

}
