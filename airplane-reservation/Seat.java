// Seat.java
// Seat.java
public class Seat {
    private final String id;
    private final int row;
    private final String letter;
    private boolean isAvailable; // Made modifiable if you want to 'reserve' it
    private final double price;

    public Seat(String id, int row, String letter, boolean isAvailable, double price) {
        this.id = id;
        this.row = row;
        this.letter = letter;
        this.isAvailable = isAvailable;
        this.price = price;
    }

    // Getters and Setters
    public String getId() { return id; }
    public int getRow() { return row; }
    public String getLetter() { return letter; }
    public boolean isAvailable() { return isAvailable; }
    public double getPrice() { return price; }

    public void setAvailable(boolean available) { isAvailable = available; }
}