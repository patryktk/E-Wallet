package pl.tkaczyk.walletapp.model;

public class Expenses {
    int id;
    Double value;
    String category, userMail, date, description, month, year;


    public Expenses() {
    }

    public Expenses(int id, Double value, String category, String userMail, String date, String description, String month, String year) {
        this.id = id;
        this.value = value;
        this.category = category;
        this.userMail = userMail;
        this.date = date;
        this.description = description;
        this.month = month;
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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
