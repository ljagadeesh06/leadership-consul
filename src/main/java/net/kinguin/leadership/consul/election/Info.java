package net.kinguin.leadership.consul.election;

public class Info {
    public String status;
    public Vote vote;
    public String error;

    @Override
    public String toString() {
        return "Info{" +
                "status='" + status + '\'' +
                ", vote=" + vote +
                ", error='" + error + '\'' +
                '}';
    }
}
