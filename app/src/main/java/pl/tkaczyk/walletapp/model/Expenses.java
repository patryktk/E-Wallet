package pl.tkaczyk.walletapp.model;

public class Expenses {
    int id;
    String value,category, userMail, date, description;

    public Expenses() {
    }

    public Expenses(int id, String value, String category, String userMail, String date, String description) {
        this.id = id;
        this.value = value;
        this.category = category;
        this.userMail = userMail;
        this.date = date;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
