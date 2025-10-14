// SimpleHttpServer.java
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class SimpleHttpServer {
    private static ReservationManager manager = new ReservationManager();
    private static final int[] PORTS = {8080, 8081, 8082, 8083, 8084};
    
    public static void main(String[] args) throws IOException {
        HttpServer server = null;
        int port = -1;
        
        // Try different ports
        for (int p : PORTS) {
            try {
                server = HttpServer.create(new InetSocketAddress(p), 0);
                port = p;
                System.out.println("✅ Successfully bound to port: " + port);
                break;
            } catch (IOException e) {
                System.out.println("❌ Port " + p + " is busy, trying next...");
            }
        }
        
        if (server == null) {
            System.err.println("❌ Could not find an available port!");
            return;
        }
        
        // Set up contexts
        setupContexts(server);
        
        server.start();
        System.out.println("✅ Server is running! Open: http://localhost:" + port);
        System.out.println("🌐 Open your browser and navigate to the URL above");
    }
    
    private static void setupContexts(HttpServer server) {
        // Home page
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Flight Reservation</title>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body { 
                                font-family: Arial, sans-serif; 
                                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                min-height: 100vh; 
                                padding: 20px; 
                                display: flex;
                                justify-content: center;
                                align-items: center;
                            }
                            .container {
                                max-width: 800px;
                                background: white;
                                border-radius: 15px;
                                box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                                overflow: hidden;
                            }
                            .header {
                                background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
                                color: white;
                                padding: 40px;
                                text-align: center;
                            }
                            .header h1 {
                                font-size: 2.5em;
                                margin-bottom: 10px;
                            }
                            .content {
                                padding: 40px;
                                text-align: center;
                            }
                            .btn {
                                background: #3498db;
                                color: white;
                                border: none;
                                padding: 15px 30px;
                                border-radius: 8px;
                                cursor: pointer;
                                font-size: 1.2em;
                                text-decoration: none;
                                display: inline-block;
                                margin: 10px;
                                transition: background 0.3s;
                            }
                            .btn:hover {
                                background: #2980b9;
                            }
                            .flight-list {
                                text-align: left;
                                margin: 20px 0;
                            }
                            .flight-card {
                                background: #f8f9fa;
                                border: 1px solid #e9ecef;
                                border-radius: 8px;
                                padding: 20px;
                                margin: 10px 0;
                                border-left: 4px solid #3498db;
                            }
                            .loading {
                                color: #666;
                                padding: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>✈️ Flight Reservation System</h1>
                                <p>Book your journey with ease</p>
                            </div>
                            <div class="content">
                                <h2>Available Flights</h2>
                                <div id="flights" class="loading">Loading flights...</div>
                            </div>
                        </div>
                        <script>
                            async function loadFlights() {
                                try {
                                    const response = await fetch('/api/flights');
                                    const flights = await response.json();
                                    const flightsDiv = document.getElementById('flights');
                                    
                                    if (flights.length === 0) {
                                        flightsDiv.innerHTML = '<p>No flights available.</p>';
                                        return;
                                    }
                                    
                                    let html = '';
                                    flights.forEach(flight => {
                                        html += `
                                            <div class="flight-card">
                                                <h3>${flight.airline} - ${flight.flightNumber}</h3>
                                                <p><strong>Route:</strong> ${flight.departure} → ${flight.arrival}</p>
                                                <p><strong>Time:</strong> ${flight.departureTime} - ${flight.arrivalTime}</p>
                                                <button class="btn" onclick="selectFlight('${flight.id}')">
                                                    Select Flight
                                                </button>
                                            </div>
                                        `;
                                    });
                                    flightsDiv.innerHTML = html;
                                } catch (error) {
                                    document.getElementById('flights').innerHTML = '<p>Error loading flights. Please try again.</p>';
                                }
                            }
                            
                            function selectFlight(flightId) {
                                fetch('/api/select-flight', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/json'},
                                    body: JSON.stringify({flightId: flightId})
                                }).then(() => {
                                    window.location.href = '/seats';
                                });
                            }
                            
                            // Load flights when page loads
                            loadFlights();
                        </script>
                    </body>
                    </html>
                """;
                sendHtmlResponse(exchange, response);
            }
        });
        
        // Seat selection page
        server.createContext("/seats", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Select Seat</title>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body { 
                                font-family: Arial, sans-serif; 
                                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                min-height: 100vh; 
                                padding: 20px; 
                            }
                            .container {
                                max-width: 1000px;
                                margin: 0 auto;
                                background: white;
                                border-radius: 15px;
                                box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                                overflow: hidden;
                            }
                            .header {
                                background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
                                color: white;
                                padding: 30px;
                                text-align: center;
                            }
                            .content {
                                padding: 30px;
                            }
                            .btn {
                                background: #3498db;
                                color: white;
                                border: none;
                                padding: 12px 25px;
                                border-radius: 6px;
                                cursor: pointer;
                                font-size: 1em;
                                text-decoration: none;
                                display: inline-block;
                                margin: 5px;
                                transition: background 0.3s;
                            }
                            .btn:hover {
                                background: #2980b9;
                            }
                            .seat-map {
                                display: grid;
                                grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
                                gap: 10px;
                                margin: 20px 0;
                            }
                            .seat {
                                padding: 15px;
                                border: none;
                                border-radius: 6px;
                                font-weight: bold;
                                cursor: pointer;
                                transition: all 0.3s;
                                text-align: center;
                            }
                            .seat.available {
                                background: #27ae60;
                                color: white;
                            }
                            .seat.available:hover {
                                background: #229954;
                                transform: scale(1.05);
                            }
                            .seat.taken {
                                background: #e74c3c;
                                color: white;
                                cursor: not-allowed;
                            }
                            .seat-price {
                                display: block;
                                font-size: 0.8em;
                                margin-top: 5px;
                            }
                            .nav-buttons {
                                text-align: center;
                                margin-top: 30px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>💺 Select Your Seat</h1>
                                <p>Choose your preferred seat</p>
                            </div>
                            <div class="content">
                                <h2 style="text-align: center; margin-bottom: 20px;">Available Seats</h2>
                                <div id="seatMap" style="text-align: center; padding: 20px;">Loading seats...</div>
                                <div class="nav-buttons">
                                    <button class="btn" onclick="window.location.href='/'">← Back to Flights</button>
                                </div>
                            </div>
                        </div>
                        <script>
                            async function loadSeats() {
                                try {
                                    const response = await fetch('/api/seats');
                                    const seats = await response.json();
                                    const seatMap = document.getElementById('seatMap');
                                    
                                    if (seats.length === 0) {
                                        seatMap.innerHTML = '<p>No seats available.</p>';
                                        return;
                                    }
                                    
                                    let html = '<div class="seat-map">';
                                    seats.forEach(seat => {
                                        const available = seat.available;
                                        html += `
                                            <button class="seat ${available ? 'available' : 'taken'}" 
                                                    ${!available ? 'disabled' : ''}
                                                    onclick="${available ? `selectSeat('${seat.id}')` : ''}">
                                                ${seat.id}
                                                <span class="seat-price">$${seat.price}</span>
                                            </button>
                                        `;
                                    });
                                    html += '</div>';
                                    seatMap.innerHTML = html;
                                } catch (error) {
                                    document.getElementById('seatMap').innerHTML = '<p>Error loading seats.</p>';
                                }
                            }
                            
                            function selectSeat(seatId) {
                                fetch('/api/select-seat', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/json'},
                                    body: JSON.stringify({seatId: seatId})
                                }).then(response => response.json())
                                  .then(result => {
                                    if (result.status === 'success') {
                                        alert('Seat ' + seatId + ' selected successfully!');
                                        window.location.href = '/confirm';
                                    } else {
                                        alert('Sorry, this seat is no longer available.');
                                    }
                                });
                            }
                            
                            loadSeats();
                        </script>
                    </body>
                    </html>
                """;
                sendHtmlResponse(exchange, response);
            }
        });
        
        // Confirmation page
        server.createContext("/confirm", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Confirm Reservation</title>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body { 
                                font-family: Arial, sans-serif; 
                                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                min-height: 100vh; 
                                padding: 20px; 
                                display: flex;
                                justify-content: center;
                                align-items: center;
                            }
                            .container {
                                max-width: 600px;
                                background: white;
                                border-radius: 15px;
                                box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                                overflow: hidden;
                                text-align: center;
                            }
                            .header {
                                background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
                                color: white;
                                padding: 40px;
                            }
                            .content {
                                padding: 40px;
                            }
                            .btn {
                                background: #27ae60;
                                color: white;
                                border: none;
                                padding: 15px 40px;
                                border-radius: 8px;
                                cursor: pointer;
                                font-size: 1.2em;
                                margin: 10px;
                                transition: background 0.3s;
                            }
                            .btn:hover {
                                background: #229954;
                            }
                            .btn-secondary {
                                background: #95a5a6;
                            }
                            .btn-secondary:hover {
                                background: #7f8c8d;
                            }
                            .icon {
                                font-size: 4em;
                                margin-bottom: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>✅ Confirm Reservation</h1>
                            </div>
                            <div class="content">
                                <div class="icon">📋</div>
                                <h2>Ready to Confirm?</h2>
                                <p style="margin: 20px 0; color: #666;">Please review your selection before confirming.</p>
                                <button class="btn" onclick="confirmReservation()">Confirm Reservation</button>
                                <br>
                                <button class="btn btn-secondary" onclick="window.location.href='/seats'">← Back to Seats</button>
                                <button class="btn btn-secondary" onclick="window.location.href='/'">← Back to Flights</button>
                            </div>
                        </div>
                        <script>
                            function confirmReservation() {
                                fetch('/api/confirm', {method: 'POST'})
                                    .then(response => response.json())
                                    .then(result => {
                                        if (result.status === 'success') {
                                            alert('Reservation confirmed successfully!');
                                            window.location.href = '/success';
                                        } else {
                                            alert('Reservation failed. Please try again.');
                                        }
                                    });
                            }
                        </script>
                    </body>
                    </html>
                """;
                sendHtmlResponse(exchange, response);
            }
        });
        
        // Success page
        server.createContext("/success", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Success</title>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body { 
                                font-family: Arial, sans-serif; 
                                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                min-height: 100vh; 
                                padding: 20px; 
                                display: flex;
                                justify-content: center;
                                align-items: center;
                            }
                            .container {
                                max-width: 600px;
                                background: white;
                                border-radius: 15px;
                                box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                                overflow: hidden;
                                text-align: center;
                            }
                            .header {
                                background: linear-gradient(135deg, #27ae60 0%, #229954 100%);
                                color: white;
                                padding: 40px;
                            }
                            .content {
                                padding: 40px;
                            }
                            .btn {
                                background: #3498db;
                                color: white;
                                border: none;
                                padding: 15px 30px;
                                border-radius: 8px;
                                cursor: pointer;
                                font-size: 1.1em;
                                margin: 10px;
                                transition: background 0.3s;
                            }
                            .btn:hover {
                                background: #2980b9;
                            }
                            .icon {
                                font-size: 4em;
                                color: #27ae60;
                                margin-bottom: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>🎉 Reservation Confirmed!</h1>
                            </div>
                            <div class="content">
                                <div class="icon">✅</div>
                                <h2>Booking Successful!</h2>
                                <p style="margin: 20px 0; color: #666;">Your flight reservation has been confirmed.</p>
                                <button class="btn" onclick="window.location.href='/'">Book Another Flight</button>
                            </div>
                        </div>
                    </body>
                    </html>
                """;
                sendHtmlResponse(exchange, response);
            }
        });
        
        // API endpoints
        setupApiEndpoints(server);
    }
    
    private static void setupApiEndpoints(HttpServer server) {
        // Flights API
        server.createContext("/api/flights", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String json = manager.getFlights().stream()
                        .map(f -> String.format(
                            "{\"id\":\"%s\",\"airline\":\"%s\",\"flightNumber\":\"%s\",\"departure\":\"%s\",\"arrival\":\"%s\",\"departureTime\":\"%s\",\"arrivalTime\":\"%s\"}",
                            f.getId(), f.getAirline(), f.getFlightNumber(), f.getDeparture(), f.getArrival(), 
                            f.getDepartureTime(), f.getArrivalTime()))
                        .collect(Collectors.joining(",", "[", "]"));
                    sendJsonResponse(exchange, json);
                } catch (Exception e) {
                    sendJsonResponse(exchange, "[]");
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method not allowed
            }
        });
        
        // Seats API
        server.createContext("/api/seats", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String json = manager.getSeats().stream()
                        .map(s -> String.format(
                            "{\"id\":\"%s\",\"row\":%d,\"letter\":\"%s\",\"available\":%s,\"price\":%.0f}",
                            s.getId(), s.getRow(), s.getLetter(), s.isAvailable(), s.getPrice()))
                        .collect(Collectors.joining(",", "[", "]"));
                    sendJsonResponse(exchange, json);
                } catch (Exception e) {
                    sendJsonResponse(exchange, "[]");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });
        
        // Select flight
        server.createContext("/api/select-flight", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readRequestBody(exchange);
                    String flightId = extractValue(body, "flightId");
                    
                    manager.getFlights().stream()
                        .filter(f -> f.getId().equals(flightId))
                        .findFirst()
                        .ifPresent(manager::selectFlight);
                    
                    sendJsonResponse(exchange, "{\"status\":\"success\"}");
                } catch (Exception e) {
                    sendJsonResponse(exchange, "{\"status\":\"error\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });
        
        // Select seat
        server.createContext("/api/select-seat", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readRequestBody(exchange);
                    String seatId = extractValue(body, "seatId");
                    
                    boolean success = manager.getSeats().stream()
                        .filter(s -> s.getId().equals(seatId) && s.isAvailable())
                        .findFirst()
                        .map(seat -> {
                            manager.selectSeat(seat);
                            return true;
                        })
                        .orElse(false);
                    
                    String response = success ? 
                        "{\"status\":\"success\"}" : 
                        "{\"status\":\"error\"}";
                    sendJsonResponse(exchange, response);
                } catch (Exception e) {
                    sendJsonResponse(exchange, "{\"status\":\"error\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });
        
        // Confirm reservation
        server.createContext("/api/confirm", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    boolean success = manager.confirmReservation();
                    String response = success ? 
                        "{\"status\":\"success\"}" : 
                        "{\"status\":\"error\"}";
                    sendJsonResponse(exchange, response);
                } catch (Exception e) {
                    sendJsonResponse(exchange, "{\"status\":\"error\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });
    }
    
    private static void sendHtmlResponse(HttpExchange exchange, String html) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(html.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    private static void sendJsonResponse(HttpExchange exchange, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, json.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining());
        }
    }
    
    private static String extractValue(String json, String key) {
        try {
            String search = "\"" + key + "\":\"";
            int start = json.indexOf(search) + search.length();
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) {
            return "";
        }
    }
}                          //javac *.java        //java SimpleHttpServer