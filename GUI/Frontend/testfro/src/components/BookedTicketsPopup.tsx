import React from "react";

interface BookedTicketsPopupProps {
  ticketsData: Record<string, number> | null; // Booked tickets data as key-value pairs
  onClose: () => void; // Function to close the popup
}

const BookedTicketsPopup: React.FC<BookedTicketsPopupProps> = ({
  ticketsData,
  onClose,
}) => {
  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>All Booked Tickets Stored in the System</h2>
        <div className="scrollable-div">
          {ticketsData && Object.keys(ticketsData).length > 0 ? (
            Object.entries(ticketsData).map(([event, count], index) => (
              <p key={index}>
                {event}: {count}
              </p>
            ))
          ) : (
            <p>There are no booked ticket records in the system yet.</p>
          )}
        </div>
        <button className="close-button" onClick={onClose}>
          Close
        </button>
      </div>
    </div>
  );
};

export default BookedTicketsPopup;
