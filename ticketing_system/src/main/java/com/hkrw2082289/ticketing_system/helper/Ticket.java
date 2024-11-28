//package com.hkrw2082289.ticketing_system.helper;
//
//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import lombok.Data;
//import java.util.Random;
//
//@Data
//@JsonPropertyOrder({ "ticketId", "vendorId", "eventName", "price", "timeDuration", "date", "ticketStatus", "customerId" })
//public class Ticket {
//    private String ticketId;
//    private String vendorId;
//    private String eventName;
//    private double price;
//    private String timeDuration;
//    private String date;
//    private String ticketStatus = "Available";
//    private String customerId = null;
//
//    public Ticket(String vendorId, String eventName, double price, String timeDuration, String date) {
//        this.vendorId = vendorId;
//        this.eventName = eventName;
//        this.price = price;
//        this.timeDuration = timeDuration;
//        this.date = date;
//    }
//
//    // Generate ticketId
//    public void generateTicketId() {
//        Random random = new Random();
//        int randomDigits = random.nextInt(900) + 100; // Generate a 3-digit number
//        String prefix = vendorId.substring(0, 4);     // Extract first 4 characters of vendorId
//        this.ticketId = prefix + "T" + randomDigits;
//    }
//}
//
