package pl.tkaczyk.walletapp;

import java.util.Date;

public class Wydatki {
    String value, category, userMail,date;

    public Wydatki() {
    }

    public Wydatki(String value, String category, String userMail, String date) {
        this.value = value;
        this.category = category;
        this.userMail = userMail;
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
