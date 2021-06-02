package pl.tkaczyk.walletapp.model;

public class Income {
    int id;
    String data, description, month;
    Double value;

    public Income() {
    }

    public Income(int id, Double value, String data, String description, String month) {
        this.id = id;
        this.value = value;
        this.data = data;
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
