// ReservationManager.java
import java.util.*;

public class ReservationManager {
    private List<Flight> flights;
    private List<Seat> seats;
    private Flight selectedFlight;
    private Seat selectedSeat;

    public ReservationManager() {
        initializeFlights();
        initializeSeats();
    }

    private void initializeFlights() {
        flights = new ArrayList<>();
        flights.add(new Flight("F1", "American Airlines", "AA123", "New York", "Los Angeles", "08:00 AM", "11:00 AM"));
        flights.add(new Flight("F2", "Delta Airlines", "DL456", "Chicago", "Miami", "10:30 AM", "02:00 PM"));
        flights.add(new Flight("F3", "United Airlines", "UA789", "San Francisco", "Seattle", "01:15 PM", "03:45 PM"));
        flights.add(new Flight("F4", "Southwest", "SW101", "Denver", "Las Vegas", "04:20 PM", "06:00 PM"));
    }

    private void initializeSeats() {
        seats = new ArrayList<>();
        String[] letters = {"A", "B", "C", "D", "E", "F"};
        
        // Create seats for different price categories
        for (int row = 1; row <= 10; row++) {
            for (String letter : letters) {
                String seatId = row + letter;
                double price;
                if (row <= 3) {
                    price = 299.0; // Premium seats
                } else if (row <= 7) {
                    price = 199.0; // Standard seats
                } else {
                    price = 149.0; // Economy seats
                }
                
                // Make some seats unavailable for demonstration
                boolean available = !(row == 2 && letter.equals("C")) && 
                                   !(row == 5 && letter.equals("A")) && 
                                   !(row == 8 && letter.equals("E"));
                
                seats.add(new Seat(seatId, row, letter, available, price));
            }
        }
    }

    // Getters for flights and seats
    public List<Flight> getFlights() {
        return flights;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    // Getters for selected items
    public Flight getSelectedFlight() {
        return selectedFlight;
    }

    public Seat getSelectedSeat() {
        return selectedSeat;
    }

    // Selection methods
    public void selectFlight(Flight flight) {
        this.selectedFlight = flight;
        System.out.println("Selected flight: " + flight.getFlightNumber());
    }

    public boolean selectSeat(Seat seat) {
        if (seat.isAvailable()) {
            this.selectedSeat = seat;
            seat.setAvailable(false); // Mark as taken
            System.out.println("Selected seat: " + seat.getId());
            return true;
        }
        return false;
    }

    // Reservation confirmation
    public boolean confirmReservation() {
        if (selectedFlight != null && selectedSeat != null) {
            System.out.println("Reservation confirmed!");
            System.out.println("Flight: " + selectedFlight.getFlightNumber());
            System.out.println("Seat: " + selectedSeat.getId());
            System.out.println("Total: $" + selectedSeat.getPrice());
            return true;
        }
        return false;
    }

    // Reset reservation
    public void resetReservation() {
        this.selectedFlight = null;
        this.selectedSeat = null;
        System.out.println("Reservation reset");
    }
}