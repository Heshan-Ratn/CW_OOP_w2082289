import React, { useState } from "react";
import AvailableTicketsPopup from "./AvailableTicketsPopup";
import ViewBookedTicketsPopup from "./ViewBookedTicketsPopup";
import PurchaseTicketsPopup from "./PurchaseTicketsPopup";
import axios from "axios";

interface CustomerMenuProps {
  customerId: string;
  onClose: () => void; // Callback to handle menu closure
  showNotification: (message: string, isError?: boolean) => void;
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
      const response = await axios.get(
        `/api/ticket-pool/available-tickets/event`
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
      const response = await axios.post(
        `/api/customers/${customerId}/stop-thread`
      );
      showNotification(response.data, false); // Green notification for success
    } catch (error: any) {
      const errorMessage =
        error.response?.data || "Failed to stop ticket purchase. Try again.";
      showNotification(errorMessage, true); // Red notification for error
    }
  };

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
          Lorem ipsum dolor sit amet consectetur adipisicing elit. Corrupti
          repellendus eius, sint non corporis suscipit aperiam, eveniet
          consectetur harum accusamus voluptates totam ut laudantium quidem, sit
          id! Quos, quibusdam perferendis?
        </div>
        <div className="half-height-overlay">
          Lorem ipsum dolor, sit amet consectetur adipisicing elit. Ea
          architecto officia nihil! Illo, sapiente quod? Sunt deleniti
          voluptatum voluptates exercitationem eos laborum voluptatibus
          provident odio accusantium! Quo qui id earum!
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
