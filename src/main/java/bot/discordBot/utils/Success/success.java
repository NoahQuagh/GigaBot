package bot.discordBot.utils.Success;

import bot.discordBot.utils.commands.CommandContext;
import bot.discordBot.utils.commands.datamanager.DataStructure.TrackedPlayer;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class success {
    public static void EventSuccess(CommandContext ctx, String titre, String message){
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("✅ "+titre+" :")
                .setDescription(message+".")
                .setColor(Color.GREEN);
        ctx.replyDeferred(embed);
    }
    public static void sendRankupMessage(DiscordApi api, TrackedPlayer player, String newRankTxt,String rankImg){
        System.out.println("passmsg");
        api.getTextChannelById(player.getChannelId()).ifPresent(channel -> {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("🚀  NOUVEAU PEAK RANK !")
                    .setDescription("Félicitations à **" + player.getPseudoRaw() + "** qui vient de dépasser son record ! **W dans le chat !!!**")
                    .addField("Nouveau Rang : **"+newRankTxt+"**", "@everyone")
                    .setThumbnail(rankImg)
                    .setColor(Color.GREEN)
                    .setTimestampToNow();

            channel.sendMessage(embed);
        });
    }
}
