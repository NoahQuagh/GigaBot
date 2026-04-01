package bot.discordBot.commands.Premier;

import bot.discordBot.commands.CommandPremier;
import bot.discordBot.utils.commands.Code;
import bot.discordBot.utils.commands.Command;
import bot.discordBot.utils.commands.datamanager.DataManager;
import bot.discordBot.utils.commands.datamanager.DataStructure.Equipe;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static bot.discordBot.Main.api;
import static bot.discordBot.utils.commands.Code.*;
import static bot.discordBot.utils.commands.datamanager.logManager.writeLogFile;

public class CommandPremierAddPlayerTeam extends CommandPremier {
    @Override
    public void run(MessageCreateEvent event, Command command, String[] args){
        if (args.length != 2) {
            String name=event.getMessageAuthor().getDisplayName();
            writeLogFile("logs.txt",name+" | Code : "+ SYNTAXE_INCORRECTE);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("⚠️   Attention :")
                    .setDescription("Syntaxe incorrecte !")
                    .addField("Exemple syntaxe:","```!premier -addplayerteam @nomJoueur```")
                    .setColor(Color.orange);
            event.getChannel().sendMessage(embed);
            return;
        }
        try{
            String idJoueur = event.getMessage().getMentionedUsers().getFirst().getIdAsString();


            ArrayList<Equipe> equipes = DataManager.loadEquipes();

            for(Equipe equipe : equipes){
                if(equipe.getChefId().equals(event.getMessageAuthor().getIdAsString())){
                    api.getUserById(idJoueur).thenAccept(user -> {

                        String team = equipe.getEquipeId();
                        String pseudo = user.getName();

                        event.getChannel().sendMessage("✅ Invitation envoyé à **"+pseudo+"**");
                        writeLogFile("logs.txt",pseudo+" | Invitation sent to join a team Premier");
                        sendDemande(user,pseudo,team,idJoueur,event);

                    }).exceptionally(e ->{

                        writeLogFile("logs.txt","Code : "+ Code.AUCUNE_DONNEE_TROUVER+" : "+e);

                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle("❌   Erreur :")
                                .addField("Impossible d'ajouter le joueur'.","```Code : "+AUCUNE_DONNEE_TROUVER+"```")
                                .setColor(Color.red);
                        event.getChannel().sendMessage(embed);

                        return null;
                    });
                }else{
                    writeLogFile("logs.txt","Code : "+ ACCEE_REFUSE);

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("❌   Erreur :")
                            .addField("Impossible d'ajouter le joueur car vous n'êtes pas le capitaine de cette team.","```Code : "+ACCEE_REFUSE+"```")
                            .setColor(Color.red);
                    event.getChannel().sendMessage(embed);
                    return;
                }
            }

        }catch (IndexOutOfBoundsException e){
            writeLogFile("logs.txt","Code : "+ Code.SYNTAXE_INCORRECTE+" : "+e);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible d'ajouter le joueur'.","@nomJoueur doit être une mention du joueur souhaité")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
        }catch (Exception e){
            writeLogFile("logs.txt","Code : "+ Code.ECHEC+" : "+e);
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible de crée la team premier.","```Code : "+ECHEC+"```")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
        }
    }

    private void sendDemande(User user, String pseudo, String team,String idJoueur,MessageCreateEvent event){
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


                            validerInvitation(team,idJoueur,pseudo,event);
                        } else {
                            writeLogFile("logs.txt",pseudo+" | refused the invitation to join the team Premier");
                            updater.setContent("❌ Invitation refusé dans la team Premier **"+team.toUpperCase()+"**")
                                    .removeAllEmbeds()
                                    .removeAllComponents()
                                    .applyChanges();

                            api.getUserById(event.getMessageAuthor().getIdAsString()).thenAccept(chef -> {
                                chef.sendMessage("❌ **"+pseudo+"** à refusé l'invitation dans votre team **"+team.toUpperCase()+"**");
                            }).exceptionally(e -> {

                                writeLogFile("logs.txt","Code : "+ Code.AUCUNE_DONNEE_TROUVER+" : "+e);

                                EmbedBuilder embed2 = new EmbedBuilder()
                                        .setTitle("❌   Erreur :")
                                        .addField("Impossible d'envoyé une reponse au chef de la team'.","```Code : "+AUCUNE_DONNEE_TROUVER+"```")
                                        .setColor(Color.red);
                                event.getChannel().sendMessage(embed2);

                                return null;
                            });
                        }
                    }).removeAfter(1, TimeUnit.DAYS);
                }).exceptionally(e -> {
                    writeLogFile("logs.txt","Code : "+ ECHEC+" : "+e);
                    return null;
                });
    }

    private void validerInvitation(String team,String idJoueur,String pseudo,MessageCreateEvent event){

        api.getUserById(event.getMessageAuthor().getIdAsString()).thenAccept(chef -> {
            chef.sendMessage("✅ **"+pseudo+"** à accepté l'invitation dans votre team **"+team.toUpperCase()+"**");
        }).exceptionally(e -> {

            writeLogFile("logs.txt","Code : "+ Code.AUCUNE_DONNEE_TROUVER+" : "+e);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible d'envoyé une reponse au chef de la team'.","```Code : "+AUCUNE_DONNEE_TROUVER+"```")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);

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
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌   Erreur :")
                    .addField("Impossible d'ajouter le joueur.","```Code : "+ECHEC+"```")
                    .setColor(Color.red);
            event.getChannel().sendMessage(embed);
        }
    }
}
