package com.example.application.controller;

import com.example.application.models.Flight;
import com.example.application.models.FlightBooking;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class FlightController {
    private List<Flight> flightList;
    private MongoCollection<Document> flightCollection;
    private MongoCollection<Document> flightBookingCollection;

    public FlightController() {
        try {
            // Attempt to connect to MongoDB
            MongoClient connectedClient = MongoClients.create("mongodb://localhost:27017/");

            // Perform pre-flight checks and handle potential issues
            if (!preFlightChecks(connectedClient)) {
                throw new RuntimeException("Failed to connect to MongoDB during pre-flight checks.");
            }

            System.out.println("=> Connection successful: " + preFlightChecks(connectedClient));
            flightCollection = connectedClient.getDatabase("Travel_Management_System").getCollection("flights");
            flightBookingCollection = connectedClient.getDatabase("Travel_Management_System").getCollection("flightBookings");
            // ... rest of the initialization logic using connectedClient
        } catch (MongoException e) {
            // Handle MongoException in case of connection issues
            throw new RuntimeException("Error connecting to MongoDB: " + e.getMessage());
        }


    }

    public Flight toFlight(Document doc){
        return new Flight(
                doc.getString("flightNumber"),
                doc.getString("airline"),
                doc.getDate("arrivalDate"),
                doc.getDate("departureDate"),
                doc.getString("departureAirport"),
                doc.getString("arrivalAirport"),
                doc.getInteger("numberOfSeatsInFlight"),
                doc.getDouble("price")
        );
    }

    public List<Flight> getAllFlights() {
        Bson filter = Filters.ne("numberOfSeatsInFlight", 0);
        flightList = flightCollection.find(filter).map(this::toFlight).into(new ArrayList<>());
        return flightList;
    }

    public void editSeats(String flightNumber, int seats)
    {
        Bson filter = Filters.eq("flightNumber", flightNumber);

        Bson update = Updates.set("numberOfSeatsInFlight", seats);

        UpdateResult result = flightCollection.updateOne(filter, update);

        if (result.getModifiedCount() == 1) {
            System.out.println("Successfully updated seats for flight " + flightNumber);
        } else {
            System.out.println("Failed to update seats for flight " + flightNumber);
        }
    }

    public void deleteFlightBooking(FlightBooking flightBooking){
        ObjectId id = new ObjectId(flightBooking.getId());
        Bson filter = Filters.eq("_id", id);
        flightBookingCollection.deleteOne(flightBookingCollection.find(filter).first());
    }

    public void updateFlightBooking(FlightBooking flightBooking) {
        ObjectId id = new ObjectId(flightBooking.getId());
        Bson filter = Filters.eq("_id", id);

        Document updateDoc = new Document();
        updateDoc.append("username", flightBooking.getUsername());
        updateDoc.append("flight_number", flightBooking.getFlightNumber());
        updateDoc.append("airline", flightBooking.getFlightName());
        updateDoc.append("departure_date", flightBooking.getDepartureDate());
        updateDoc.append("arrival_date", flightBooking.getArrivalDate());
        updateDoc.append("seats_booked", flightBooking.getSeatsBooked());
        updateDoc.append("total_price", flightBooking.getTotalPrice());
        updateDoc.append("date_booked", flightBooking.getDateBooked());
        updateDoc.append("paid", flightBooking.getPaid());

        flightBookingCollection.deleteOne(flightBookingCollection.find(filter).first());
    }


    static boolean preFlightChecks(MongoClient mongoClient) {
        Document pingCommand = new Document("ping", 1);
        Document response = mongoClient.getDatabase("admin").runCommand(pingCommand);
        System.out.println("=> Print result of the '{ping: 1}' command.");
        System.out.println(response.toJson(JsonWriterSettings.builder().indent(true).build()));
        return response.get("ok", Number.class).intValue() == 1;
    }
}
