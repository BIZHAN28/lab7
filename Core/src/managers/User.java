package managers;

public class User {
    private byte[] password;
    private String username;

    public User(){

    }
    public User(String username,byte[] password){
        this.username = username;
        this.password = password;
    }
    public void setPassword(byte[] password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPassword() {
        return password;
    }
}
