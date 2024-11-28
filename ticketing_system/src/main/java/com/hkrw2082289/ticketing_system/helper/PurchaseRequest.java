package com.hkrw2082289.ticketing_system.helper;

import lombok.Data;

@Data
public class PurchaseRequest {
    private String customerId;
    private int ticketToBook;
    private String eventName;

    public PurchaseRequest(String customerId, int ticketToBook, String eventName) {
        this.customerId = customerId;
        this.ticketToBook = ticketToBook;
        this.eventName = eventName;
    }
}
