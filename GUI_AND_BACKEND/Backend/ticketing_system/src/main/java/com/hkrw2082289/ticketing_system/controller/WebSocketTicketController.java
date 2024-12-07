//package com.hkrw2082289.ticketing_system.controller;

//import com.hkrw2082289.ticketing_system.model.TicketEntity;
//import com.hkrw2082289.ticketing_system.service.TicketService;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class WebSocketTicketController {
//    private final TicketService ticketService;
//
//    public WebSocketTicketController(TicketService ticketService) {
//        this.ticketService = ticketService;
//    }
//
//    @MessageMapping("/update-ticket")
//    @SendTo("/topic/ticket-updates")
//    public TicketEntity updateTicket(TicketEntity ticket) {
//        return ticketService.updateTicket(ticket);
//    }
//}
