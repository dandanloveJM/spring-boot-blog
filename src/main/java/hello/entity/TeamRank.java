package hello.entity;

import java.math.BigDecimal;

public class TeamRank {
    private int rankId;
    private String teamRank;
    private String teamName;
    private BigDecimal productSum;
    private BigDecimal sumForR4;
    private BigDecimal sumForR5;

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public String getTeamRank() {
        return teamRank;
    }

    public void setTeamRank(String teamRank) {
        this.teamRank = teamRank;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public BigDecimal getProductSum() {
        return productSum;
    }

    public void setProductSum(BigDecimal productSum) {
        this.productSum = productSum;
    }

    public BigDecimal getSumForR4() {
        return sumForR4;
    }

    public void setSumForR4(BigDecimal sumForR4) {
        this.sumForR4 = sumForR4;
    }

    public BigDecimal getSumForR5() {
        return sumForR5;
    }

    public void setSumForR5(BigDecimal sumForR5) {
        this.sumForR5 = sumForR5;
    }
}
