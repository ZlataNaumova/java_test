package models;

public class User {
    private final String username;
    private final String password;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString(){
        return username + "|" + password;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }
}
