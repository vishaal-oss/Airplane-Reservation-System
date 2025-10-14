// Seat.java
public class Seat {
    private String id;
    private int row;
    private String letter;
    private boolean available;
    private double price;

    public Seat(String id, int row, String letter, boolean available, double price) {
        this.id = id;
        this.row = row;
        this.letter = letter;
        this.available = available;
        this.price = price;
    }

    // Getters
    public String getId() { return id; }
    public int getRow() { return row; }
    public String getLetter() { return letter; }
    public boolean isAvailable() { return available; }
    public double getPrice() { return price; }

    // Setter
    public void setAvailable(boolean available) { this.available = available; }
}