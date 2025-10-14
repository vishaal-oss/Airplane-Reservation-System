// Flight.java
public class Flight {
    private String id;
    private String airline;
    private String flightNumber;
    private String departure;
    private String arrival;
    private String departureTime;
    private String arrivalTime;

    public Flight(String id, String airline, String flightNumber, String departure, 
                  String arrival, String departureTime, String arrivalTime) {
        this.id = id;
        this.airline = airline;
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.arrival = arrival;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    // Getters
    public String getId() { return id; }
    public String getAirline() { return airline; }
    public String getFlightNumber() { return flightNumber; }
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
}