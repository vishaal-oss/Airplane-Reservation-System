// AirplaneReservationApp.java
import java.util.List;
import java.util.Scanner;

/**
 * Console-based Airplane Reservation application.
 * Replaces the JavaFX GUI so the project compiles and runs without JavaFX on the classpath.
 */
public class AirplaneReservationApp {

    private final ReservationManager manager;
    private final Scanner scanner = new Scanner(System.in);

    public AirplaneReservationApp() {
        manager = new ReservationManager();
    }

    public static void main(String[] args) {
        new AirplaneReservationApp().run();
    }

    private void run() {
        System.out.println("Welcome to the Airplane Reservation System (Console)");

        while (true) {
            System.out.println();
            System.out.println("1) List flights");
            System.out.println("2) Select flight");
            System.out.println("3) Show seat map");
            System.out.println("4) Select seat");
            System.out.println("5) Confirm reservation");
            System.out.println("6) Reset reservation");
            System.out.println("0) Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> listFlights();
                case "2" -> chooseFlight();
                case "3" -> showSeatMap();
                case "4" -> chooseSeat();
                case "5" -> confirmReservation();
                case "6" -> resetReservation();
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Unknown option. Try again.");
            }
        }
    }

    private void listFlights() {
        List<Flight> flights = manager.getFlights();
        System.out.println("Available flights:");
        for (int i = 0; i < flights.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, flights.get(i));
        }
    }

    private void chooseFlight() {
        listFlights();
        System.out.print("Enter flight number to select (or 0 to cancel): ");
        String line = scanner.nextLine().trim();
        try {
            int idx = Integer.parseInt(line);
            if (idx == 0) return;
            Flight f = manager.getFlights().get(idx - 1);
            manager.selectFlight(f);
            System.out.println("Selected: " + f);
        } catch (Exception e) {
            System.out.println("Invalid selection.");
        }
    }

    private void showSeatMap() {
        if (manager.getSelectedFlight() == null) {
            System.out.println("No flight selected. Choose a flight first.");
            return;
        }
        printSeatMap(manager.getSeats());
        Seat s = manager.getSelectedSeat();
        System.out.println("Currently selected seat: " + (s == null ? "None" : s.getId()));
    }

    private void chooseSeat() {
        if (manager.getSelectedFlight() == null) {
            System.out.println("No flight selected. Choose a flight first.");
            return;
        }
        printSeatMap(manager.getSeats());
        System.out.print("Enter seat id to select (e.g. 3B) or 0 to cancel: ");
        String id = scanner.nextLine().trim().toUpperCase();
        if (id.equals("0")) return;
        Seat seat = findSeatById(id);
        if (seat == null) {
            System.out.println("Seat not found.");
            return;
        }
        if (!seat.isAvailable()) {
            System.out.println("Seat " + id + " is not available.");
            return;
        }
        manager.selectSeat(seat);
        System.out.println("Selected seat " + id + " (Price: $" + seat.getPrice() + ")");
    }

    private void confirmReservation() {
        if (manager.getSelectedFlight() == null || manager.getSelectedSeat() == null) {
            System.out.println("You must select a flight and a seat before confirming.");
            return;
        }
        boolean ok = manager.confirmReservation();
        if (ok) {
            System.out.println("Reservation confirmed for seat " + manager.getSelectedSeat().getId());
        } else {
            System.out.println("Failed to confirm reservation.");
        }
    }

    private void resetReservation() {
        manager.resetReservation();
        System.out.println("Reservation state reset.");
    }

    private Seat findSeatById(String id) {
        for (Seat seat : manager.getSeats()) {
            if (seat.getId().equalsIgnoreCase(id)) return seat;
        }
        return null;
    }

    private void printSeatMap(List<Seat> seats) {
        String[] letters = {"A", "B", "C", "D", "E", "F"};
        int rows = seats.stream().mapToInt(Seat::getRow).max().orElse(0);

        System.out.print("    ");
        for (String l : letters) System.out.print(l + " ");
        System.out.println();

        for (int r = 1; r <= rows; r++) {
            final int row = r; // make effectively final for lambda
            System.out.printf("%2d: ", row);
            for (String l : letters) {
                Seat seat = seats.stream().filter(s -> s.getRow() == row && s.getLetter().equals(l)).findFirst().orElse(null);
                if (seat == null) System.out.print("? ");
                else if (!seat.isAvailable()) System.out.print("X ");
                else if (seat.equals(manager.getSelectedSeat())) System.out.print("S ");
                else System.out.print("O ");
            }
            System.out.println();
        }
        System.out.println("Legend: O=available, X=unavailable, S=selected");
    }
}