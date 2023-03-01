package lan.capstone.demo.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class ChangePassAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String curpass;
    private String newpass;
    private String newpassconf;

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCurpass() {
        return curpass;
    }

    public void setCurpass(String curpass) {
        this.curpass = curpass;
    }

    public String getNewpass() {
        return newpass;
    }

    public void setNewpass(String newpass) {
        this.newpass = newpass;
    }

    public String getNewpassconf() {
        return newpassconf;
    }

    public void setNewpassconf(String newpassconf) {
        this.newpassconf = newpassconf;
    }
}
