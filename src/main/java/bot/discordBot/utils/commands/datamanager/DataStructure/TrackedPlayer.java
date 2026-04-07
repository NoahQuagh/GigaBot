package bot.discordBot.utils.commands.datamanager.DataStructure;

import bot.discordBot.utils.commands.datamanager.DataManager;

import java.util.ArrayList;

public class TrackedPlayer {
    private String pseudoRaw;
    private String channelId;
    private int peakTier;

    public TrackedPlayer(String pseudoRaw, String channelId, int peakTier) {
        this.pseudoRaw = pseudoRaw;
        this.channelId = channelId;
        this.peakTier = peakTier;
    }

    public String getPseudoRaw() {
        return pseudoRaw;
    }

    public String getChannelId() {
        return channelId;
    }

    public int getPeakTier() {
        return peakTier;
    }

    public void setPseudoRaw(String pseudoRaw) {
        this.pseudoRaw = pseudoRaw;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setPeakTier(int peakTier) {
        this.peakTier = peakTier;
    }

    public static boolean ceSuiviExiste(String pseudoRaw){
        ArrayList<TrackedPlayer> players = DataManager.loadTrackedPlayer();
        for(TrackedPlayer player : players){
            if(player.getPseudoRaw().equals(pseudoRaw)){
                return true;
            }
        }
        return false;
    }

    public static boolean RemoveTrackerByPseudoRaw(String pseudoRaw){
        ArrayList<TrackedPlayer> players = DataManager.loadTrackedPlayer();
        boolean rep = players.removeIf(id -> id.getPseudoRaw().equals(pseudoRaw));
        DataManager.saveTrackedPlayer(players);
        return rep;
    }
}
