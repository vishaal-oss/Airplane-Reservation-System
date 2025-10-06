// ReservationManager.java
import java.util.ArrayList;
import java.util.List;

public class ReservationManager {

    private final List<Flight> flights;
    private final List<Seat> seats;

    public ReservationManager() {
        this.flights = createFlights();
        this.seats = generateSeats();
    }

    private List<Flight> createFlights() {
        List<Flight> flightList = new ArrayList<>();
        flightList.add(new Flight("1", "Sky Airlines", "SA123", "New York (JFK)", "Los Angeles (LAX)", "08:00 AM", "11:30 AM"));
        flightList.add(new Flight("2", "Cloud Express", "CE456", "Chicago (ORD)", "Miami (MIA)", "02:15 PM", "05:45 PM"));
        flightList.add(new Flight("3", "JetStream", "JS789", "San Francisco (SFO)", "Seattle (SEA)", "06:30 AM", "08:15 AM"));
        return flightList;
    }

    private List<Seat> generateSeats() {
        List<Seat> seatList = new ArrayList<>();
        int rows = 10;
        String[] letters = {"A", "B", "C", "D", "E", "F"};

        for (int row = 1; row <= rows; row++) {
            for (String letter : letters) {
                // Mock unavailability based on original logic
                boolean isUnavailable = (row == 3 && letter.equals("B")) ||
                                        (row == 5 && letter.equals("C")) ||
                                        (row == 7 && letter.equals("A"));

                double price;
                if (row <= 3) price = 200;
                else if (row <= 7) price = 150;
                else price = 100;

                seatList.add(new Seat(
                    row + letter,
                    row,
                    letter,
                    !isUnavailable,
                    price
                ));
            }
        }
        return seatList;
    }

    // --- State & Handlers ---
    private Flight selectedFlight = null;
    private Seat selectedSeat = null;
    private boolean isConfirmed = false;

    public List<Flight> getFlights() { return flights; }
    public List<Seat> getSeats() { return seats; }
    public Flight getSelectedFlight() { return selectedFlight; }
    public Seat getSelectedSeat() { return selectedSeat; }
    public boolean isConfirmed() { return isConfirmed; }

    public void selectFlight(Flight flight) {
        this.selectedFlight = flight;
        this.selectedSeat = null; // Reset seat
        this.isConfirmed = false;
    }

    public boolean selectSeat(Seat seat) {
        if (seat != null && seat.isAvailable()) {
            this.selectedSeat = seat;
            return true;
        }
        return false;
    }

    public boolean confirmReservation() {
        if (selectedFlight != null && selectedSeat != null) {
            // In a real app, you'd save to a DB here
            // Mark the seat as unavailable
            selectedSeat.setAvailable(false);
            this.isConfirmed = true;
            return true;
        }
        return false;
    }

    public void resetReservation() {
        this.selectedFlight = null;
        this.selectedSeat = null;
        this.isConfirmed = false;
    }
}