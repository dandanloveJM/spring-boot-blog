package hello.entity;

import java.math.BigDecimal;

public class TeamRank {
    private String teamRank;
    private String teamName;
    private BigDecimal productSum;

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
}
