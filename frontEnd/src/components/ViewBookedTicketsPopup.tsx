import React, { useEffect, useState } from "react";
import axios from "axios";

interface ViewBookedTicketsPopupProps {
  customerId: string;
  onClose: () => void;
  showNotification: (message: string, isError?: boolean) => void;
}

const ViewBookedTicketsPopup: React.FC<ViewBookedTicketsPopupProps> = ({
  customerId,
  onClose,
  showNotification,
}) => {
  const [tickets, setTickets] = useState<Record<string, number> | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const response = await axios.get(
          `/api/ticket-pool/booked-tickets/customer/${customerId}`
        );
        setTickets(response.data);
      } catch (error) {
        showNotification(
          "Failed to fetch tickets. Please try again later.",
          true
        );
      } finally {
        setLoading(false);
      }
    };

    fetchTickets();
  }, [customerId, showNotification]);

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h3>All Tickets Booked by Customer: {customerId}</h3>
        {loading ? (
          <p>Loading...</p>
        ) : (
          <div className="scrollable-div">
            {tickets && Object.keys(tickets).length > 0 ? (
              Object.entries(tickets).map(([eventName, count]) => (
                <p key={eventName}>
                  {eventName} : {count}
                </p>
              ))
            ) : (
              <p>There are no tickets booked by the customer.</p>
            )}
          </div>
        )}
        <button className="close-button" onClick={onClose}>
          Close
        </button>
      </div>
    </div>
  );
};

export default ViewBookedTicketsPopup;
