package botdiscord.gigabot.utils.DB.structure;

public class TrackedPlayer {
    private String valo_pseudo;
    private String channel_id;
    private int rank_peak;

    public TrackedPlayer(String valo_pseudo, String channel_id, int rank_peak) {
        this.valo_pseudo = valo_pseudo;
        this.channel_id = channel_id;
        this.rank_peak = rank_peak;
    }

    public String getValo_pseudo() {
        return valo_pseudo;
    }

    public void setValo_pseudo(String dicord_id) {
        this.valo_pseudo = dicord_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public int getRank_peak() {
        return rank_peak;
    }

    public void setRank_peak(int rank_peak) {
        this.rank_peak = rank_peak;
    }
}
