package hello.entity;

public class TeamMembers {
    private int id;
    private String teamName;
    private double members;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public double getMembers() {
        return members;
    }

    public void setMembers(double members) {
        this.members = members;
    }
}
