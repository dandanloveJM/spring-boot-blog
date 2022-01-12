package hello.entity;

public class Role {
    private int id;
    private String role;
    private String description;
    private String permissionIds;

    public Role(){

    }

    public Role(int id, String role, String description, String permissionIds) {
        this.id = id;
        this.role = role;
        this.description = description;
        this.permissionIds = permissionIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(String permissionIds) {
        this.permissionIds = permissionIds;
    }
}
