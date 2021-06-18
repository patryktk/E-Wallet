package pl.tkaczyk.walletapp.model;

public class Categories {
    int id;
    String name, userMail;

    public Categories() {
    }

    public Categories(int id, String name, String userMail) {
        this.id = id;
        this.name = name;
        this.userMail = userMail;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
