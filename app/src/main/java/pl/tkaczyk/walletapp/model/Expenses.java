package pl.tkaczyk.walletapp.model;

public class Expenses {
    int id;
    String category, userMail, date, description, month;
    Double value;

    public Expenses() {
    }

    public Expenses(int id, Double value, String category, String userMail, String date, String description, String month) {
        this.id = id;
        this.value = value;
        this.category = category;
        this.userMail = userMail;
        this.date = date;
        this.description = description;
        this.month = month;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
