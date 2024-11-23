public class PurchaseRequest {
    private final String eventName;
    private final int ticketsToBook;

    public PurchaseRequest(String eventName, int ticketsToBook) {
        this.eventName = eventName;
        this.ticketsToBook = ticketsToBook;
    }

    public String getEventName() {
        return eventName;
    }

    public int getTicketsToBook() {
        return ticketsToBook;
    }
}
