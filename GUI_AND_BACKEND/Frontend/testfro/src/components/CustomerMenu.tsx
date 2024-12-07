import React, { useEffect, useRef, useState } from "react";
import AvailableTicketsPopup from "./AvailableTicketsPopup";
import ViewBookedTicketsPopup from "./ViewBookedTicketsPopup";
import PurchaseTicketsPopup from "./PurchaseTicketsPopup";
import apiClient from "../api";
import { Client } from "@stomp/stompjs";
import { connectWebSocket } from "../websocket";
import LogDisplay from "./LogDisplay";
import TicketTable from "./TicketTable";

interface CustomerMenuProps {
  customerId: string;
  onClose: () => void; // Callback to handle menu closure
  showNotification: (message: string, isError?: boolean) => void;
}

interface Ticket {
  ticketId: number;
  eventName: string;
  price: number;
  timeDuration: string;
  date: string;
  vendorId: string;
  ticketStatus: string;
  customerId?: string | null;
}

const CustomerMenu: React.FC<CustomerMenuProps> = ({
  customerId,
  onClose,
  showNotification,
}) => {
  const [showPurchaseTicketsPopup, setShowPurchaseTicketsPopup] =
    useState(false);
  const [showViewBookedTicketsPopup, setShowViewBookedTicketsPopup] =
    useState(false);

  const [showAvailableTicketsPopup, setShowAvailableTicketsPopup] =
    useState(false);
  const [availableTicketsData, setAvailableTicketsData] = useState<Record<
    string,
    number
  > | null>(null);

  const handleViewAvailableTickets = async () => {
    try {
      const response = await apiClient.get(
        `/ticket-pool/available-tickets/event`
      );
      setAvailableTicketsData(response.data);
      setShowAvailableTicketsPopup(true);
    } catch (error) {
      showNotification(
        "Failed to fetch available tickets. Please try again.",
        true
      );
    }
  };

  const handleStopPurchase = async () => {
    try {
      const response = await apiClient.post(
        `/customers/${customerId}/stop-thread`
      );
      showNotification(response.data, false); // Green notification for success
    } catch (error: any) {
      const errorMessage =
        error.response?.data || "Failed to stop ticket purchase. Try again.";
      showNotification(errorMessage, true); // Red notification for error
    }
  };

  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [logMessages, setLogMessages] = useState<string[]>([]);
  const webSocketRef = useRef<Client | null>(null);

  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const response = await apiClient.get("/tickets/all");
        if (response.data.length === 0) {
          setTickets([]);
          showNotification("No tickets available in the pool.");
        } else {
          setTickets(response.data);
        }
      } catch (error) {
        console.error("Error fetching tickets:", error);
        showNotification("Failed to load tickets.", true);
      }
    };

    fetchTickets();

    // Connect WebSocket only if not already connected
    if (!webSocketRef.current) {
      webSocketRef.current = connectWebSocket(
        (newTickets: Ticket[]) => {
          setTickets(newTickets);
        },
        (logMessage: string) => {
          console.log("Received log message:", logMessage); // Debug log
          setLogMessages((prevMessages) => {
            if (!prevMessages.includes(logMessage)) {
              return [...prevMessages, logMessage];
            }
            return prevMessages;
          });
        }
      );
    }

    return () => {
      // Cleanup WebSocket connection on component unmount
      if (webSocketRef.current) {
        webSocketRef.current.deactivate();
        webSocketRef.current = null;
      }
    };
  }, []);

  return (
    <div className="container-menu">
      <div className="user-menu">
        <h3>Customer Menu For: {customerId}</h3>
        <button
          className="button"
          onClick={() => setShowPurchaseTicketsPopup(true)}
        >
          Purchase New Tickets
        </button>
        <button
          className="button"
          onClick={() => setShowViewBookedTicketsPopup(true)}
        >
          View All My Booked Tickets
        </button>
        <button className="button" onClick={handleViewAvailableTickets}>
          View Other Available Tickets
        </button>
        <button className="button" onClick={handleStopPurchase}>
          Stop Purchase of Tickets
        </button>
        <button className="close-button" onClick={onClose}>
          Close
        </button>
      </div>
      <div className="side-panel-overlay">
        <div className="half-height-overlay">
          <LogDisplay messages={logMessages}></LogDisplay>
        </div>
        <div className="half-height-overlay">
          <TicketTable tickets={tickets}></TicketTable>
        </div>
      </div>
      {showPurchaseTicketsPopup && (
        <PurchaseTicketsPopup
          customerId={customerId}
          onClose={() => setShowPurchaseTicketsPopup(false)}
          showNotification={showNotification}
        />
      )}

      {showViewBookedTicketsPopup && (
        <ViewBookedTicketsPopup
          customerId={customerId}
          onClose={() => setShowViewBookedTicketsPopup(false)}
          showNotification={showNotification}
        />
      )}

      {showAvailableTicketsPopup && (
        <AvailableTicketsPopup
          ticketsData={availableTicketsData}
          onClose={() => setShowAvailableTicketsPopup(false)}
        />
      )}
    </div>
  );
};

export default CustomerMenu;
