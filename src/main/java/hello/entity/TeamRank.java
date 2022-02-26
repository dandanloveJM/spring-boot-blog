package hello.entity;

import java.math.BigDecimal;

public class TeamRank {
    private int rankId;
    private String team;
    private String teamRank;
    private String teamName;
    private BigDecimal allBonus;
    private BigDecimal R4Bonus;
    private BigDecimal R5Bonus;


    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public BigDecimal getAllBonus() {
        return allBonus;
    }

    public void setAllBonus(BigDecimal allBonus) {
        this.allBonus = allBonus;
    }

    public BigDecimal getR4Bonus() {
        return R4Bonus;
    }

    public void setR4Bonus(BigDecimal r4Bonus) {
        R4Bonus = r4Bonus;
    }

    public BigDecimal getR5Bonus() {
        return R5Bonus;
    }

    public void setR5Bonus(BigDecimal r5Bonus) {
        R5Bonus = r5Bonus;
    }

    public String getTeamRank() {
        return teamRank;
    }

    public void setTeamRank(String teamRank) {
        this.teamRank = teamRank;
    }
}
