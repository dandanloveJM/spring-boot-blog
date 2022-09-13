package hello.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class User {
    Integer id;
    String username;
    String avatar;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @JsonProperty("createdAt")
    ZonedDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @JsonProperty("updatedAt")
    ZonedDateTime updatedAt;
//  @JsonIgnore
    String password;
    int roleId;
    String displayName;
    BigDecimal userReallocateProduct;
    String department;
    String teamName;


    public User(Integer id, String username, String avatar, ZonedDateTime createdAt, ZonedDateTime updatedAt, String password) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.password = password;
    }

    public User(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BigDecimal getUserReallocateProduct() {
        return userReallocateProduct;
    }

    public void setUserReallocateProduct(BigDecimal userReallocateProduct) {
        this.userReallocateProduct = userReallocateProduct;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
