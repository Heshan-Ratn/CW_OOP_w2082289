import React, { useState } from "react";
import axios from "axios";

interface PurchaseTicketsPopupProps {
  customerId: string;
  onClose: () => void;
  showNotification: (message: string, isError?: boolean) => void;
}

const PurchaseTicketsPopup: React.FC<PurchaseTicketsPopupProps> = ({
  customerId,
  onClose,
  showNotification,
}) => {
  const [eventName, setEventName] = useState("");
  const [ticketsToBook, setTicketsToBook] = useState<number | "">("");

  const handleReset = () => {
    setEventName("");
    setTicketsToBook("");
  };

  const handleSubmit = async () => {
    if (!eventName || ticketsToBook === "") {
      showNotification("Please fill in all fields before submitting.", true);
      return;
    }

    if (ticketsToBook < 0 || ticketsToBook > 100) {
      showNotification("Tickets to book must be between 0 and 100.", true);
      return;
    }

    const payload = {
      eventName,
      ticketToBook: ticketsToBook,
    };

    try {
      const response = await axios.post(
        `/api/customers/${customerId}/start-thread`,
        payload
      );
      showNotification(response.data, false); // Green notification for success
      onClose(); // Close the popup
    } catch (error: any) {
      const errorMessage =
        error.response?.data || "Failed to purchase tickets. Please try again.";
      showNotification(errorMessage, true); // Red notification for failure
    }
  };

  return (
    <div className="popup-container">
      <div className="popup-tickets">
        <h2>Start Purchasing New Tickets from System</h2>
        <div className="form-group-tickets">
          <label>Event Name: </label>
          <input
            type="text"
            value={eventName}
            onChange={(e) => setEventName(e.target.value)}
            placeholder="Enter event name"
          />
        </div>
        <div className="form-group-tickets">
          <label>Total Tickets to Book (0-100): </label>
          <input
            type="number"
            value={ticketsToBook}
            onChange={(e) => setTicketsToBook(Number(e.target.value) || "")}
            placeholder="Enter total tickets"
            min="0"
            max="100"
          />
        </div>
        <div className="popup-buttons">
          <button className="button" onClick={handleSubmit}>
            Submit
          </button>
          <button className="button" onClick={handleReset}>
            Reset
          </button>
          <button className="close-button" onClick={onClose}>
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default PurchaseTicketsPopup;
