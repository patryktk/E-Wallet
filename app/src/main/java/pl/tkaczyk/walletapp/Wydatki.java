package pl.tkaczyk.walletapp;

import java.util.Date;

public class Wydatki {
    Date date;
    String name, user;
    Number value;


    public Wydatki() {
    }

    public Wydatki(Date date, String name, String user, Number value) {
        this.date = date;
        this.name = name;
        this.user = user;
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }
}
