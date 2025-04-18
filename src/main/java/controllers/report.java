package controllers;

public class report {
    private String id;
    private String name;
    private String date;
    private String amount;

    public report(String id, String name, String date, String amount) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.amount = amount;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getAmount() { return amount; }
}

