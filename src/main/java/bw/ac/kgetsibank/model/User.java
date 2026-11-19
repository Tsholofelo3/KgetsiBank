package bw.ac.kgetsibank.model;

public class User {

    private long id;

    private String username;
    private String passwordHash;
    private String role;

    private Long customerId;

    private boolean active;

    public User() {
        this.active = true;
    }

    public User(
            String username,
            String passwordHash,
            String role,
            Long customerId
    ) {

        setUsername(username);
        setPasswordHash(passwordHash);
        setRole(role);

        this.customerId = customerId;
        this.active = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(
            String username
    ) {

        if (username == null ||
                username.isBlank()) {

            throw new IllegalArgumentException(
                    "Username is required."
            );
        }

        this.username = username.trim();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(
            String passwordHash
    ) {

        if (passwordHash == null ||
                passwordHash.isBlank()) {

            throw new IllegalArgumentException(
                    "Password is required."
            );
        }

        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(
            String role
    ) {

        if (role == null ||
                role.isBlank()) {

            throw new IllegalArgumentException(
                    "User role is required."
            );
        }

        this.role =
                role.trim().toUpperCase();
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(
            Long customerId
    ) {
        this.customerId = customerId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(
            boolean active
    ) {
        this.active = active;
    }

    @Override
    public String toString() {

        return username
                + " ("
                + role
                + ")";
    }
}
