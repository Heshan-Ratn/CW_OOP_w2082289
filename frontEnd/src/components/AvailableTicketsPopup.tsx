import React from "react";

interface AvailableTicketsPopupProps {
  ticketsData: Record<string, number> | null; // Ticket data as a key-value pair
  onClose: () => void; // Function to close the popup
}

const AvailableTicketsPopup: React.FC<AvailableTicketsPopupProps> = ({
  ticketsData,
  onClose,
}) => {
  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>All The Tickets Available For Purchase</h2>
        <div className="scrollable-div">
          {ticketsData && Object.keys(ticketsData).length > 0 ? (
            Object.entries(ticketsData).map(([event, count], index) => (
              <p key={index}>
                {event}: {count}
              </p>
            ))
          ) : (
            <p>No tickets available in the system</p>
          )}
        </div>
        <button className="close-button" onClick={onClose}>
          Close
        </button>
      </div>
    </div>
  );
};

export default AvailableTicketsPopup;
