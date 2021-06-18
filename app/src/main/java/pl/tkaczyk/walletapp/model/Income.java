package pl.tkaczyk.walletapp.model;

public class Income {
    int id;
    String data, description, month, userMail;
    Double value;

    public Income() {
    }

    public Income(int id, String data, String description, String month, Double value,String userMail) {
        this.id = id;
        this.data = data;
        this.description = description;
        this.month = month;
        this.value = value;
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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
