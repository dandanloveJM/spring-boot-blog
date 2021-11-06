package hello.entity;

import java.time.Instant;

public class User {
    Integer id;
    String username;
    String avatar;
    String password;
    Instant createdAt;
    Instant updatedAt;

    public User(Integer id, String username, String avatar, String password, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
