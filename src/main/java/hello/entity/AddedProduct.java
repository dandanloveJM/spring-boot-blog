package hello.entity;

import java.math.BigDecimal;

public class AddedProduct {
    private Integer id;
    private Integer userId;
    private String displayName;
    private BigDecimal product;
    private Integer rankId;

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

    public BigDecimal getProduct() {
        return product;
    }

    public void setProduct(BigDecimal product) {
        this.product = product;
    }

    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
    }
}
