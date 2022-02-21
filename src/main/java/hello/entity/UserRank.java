package hello.entity;

import java.math.BigDecimal;

public class UserRank {
    private Integer id;
    private Integer userId;
    private String displayName;
    private BigDecimal productSum;
    private String team;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BigDecimal getProductSum() {
        return productSum;
    }

    public void setProductSum(BigDecimal productSum) {
        this.productSum = productSum;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
