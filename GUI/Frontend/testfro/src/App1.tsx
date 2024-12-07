import React, { useEffect, useState } from "react";
import { connectWebSocket } from "./websocket";
import TicketTable from "./components/TicketTable";

// Define the Ticket interface
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

const App1: React.FC = () => {
  const [tickets, setTickets] = useState<Ticket[]>([]);

  useEffect(() => {
    // Connect to WebSocket for real-time updates
    connectWebSocket((newTickets: Ticket[]) => {
      setTickets(newTickets); // Update tickets when receiving a WebSocket message
    });
  }, []);

  return (
    <div style={{ overflowY: "auto", maxHeight: "80vh", padding: "20px" }}>
      <TicketTable tickets={tickets} />
    </div>
  );
};

export default App1;
