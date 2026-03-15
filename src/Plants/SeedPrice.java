package Plants;

public class SeedPrice {
    private int id;
    private double price;
    private String name;

    public SeedPrice(int id, double price, String name) {
        this.id = id;
        this.price = price;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}

