public class Ticket {
    private String ticketId;
    private String eventDetails;
    private double price;
    private String vendorId;

    public Ticket(String ticketId, String eventDetails, double price, String vendorId) {
        this.ticketId = ticketId;
        this.eventDetails = eventDetails;
        this.price = price;
        this.vendorId = vendorId;
    }

    public String getTicketId() { return ticketId; }
    public String getEventDetails() { return eventDetails; }
    public double getPrice() { return price; }
}

