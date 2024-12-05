// import React from "react";

// interface VendorMenuProps {
//   vendorId: string;
//   onClose: () => void; // Callback to handle menu closure
//   showNotification: (message: string, isError?: boolean) => void;
// }

// const VendorMenu: React.FC<VendorMenuProps> = ({
//   vendorId,
//   onClose,
//   showNotification,
// }) => {
//   return (
//     <div className="container-menu">
//       <div className="user-menu">
//         <h3>Vendor Menu For: {vendorId}</h3>
//         <button className="button">Start Adding Tickets</button>
//         <button className="button">View Added Tickets</button>
//         <button className="button">View Other Available Tickets</button>
//         <button className="button">Stop Ticket Release</button>
//         <button className="button">Start Ticket Release</button>
//         <button className="close-button" onClick={onClose}>
//           Close
//         </button>
//       </div>
//       <div className="side-panel-overlay">
//         <div className="half-height-overlay">
//           Lorem ipsum dolor sit amet consectetur adipisicing elit. Corrupti
//           repellendus eius, sint non corporis suscipit aperiam, eveniet
//           consectetur harum accusamus voluptates totam ut laudantium quidem, sit
//           id! Quos, quibusdam perferendis?
//         </div>
//         <div className="half-height-overlay">
//           Lorem ipsum dolor, sit amet consectetur adipisicing elit. Ea
//           architecto officia nihil! Illo, sapiente quod? Sunt deleniti
//           voluptatum voluptates exercitationem eos laborum voluptatibus
//           provident odio accusantium! Quo qui id earum!
//         </div>
//       </div>
//     </div>
//   );
// };

// export default VendorMenu;

import React, { useState } from "react";
import AddTicketsPopup from "./AddTicketsPopup";
import ViewAddedTicketsPopup from "./ViewAddedTicketsPopup";
import AvailableTicketsPopup from "./AvailableTicketsPopup";
import axios from "axios";

interface VendorMenuProps {
  vendorId: string;
  onClose: () => void; // Callback to handle menu closure
  showNotification: (message: string, isError?: boolean) => void;
}

const VendorMenu: React.FC<VendorMenuProps> = ({
  vendorId,
  onClose,
  showNotification,
}) => {
  const [showAddTicketsPopup, setShowAddTicketsPopup] = useState(false);

  const [showViewAddedTicketsPopup, setShowViewAddedTicketsPopup] =
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

  const handleStopTicketRelease = async () => {
    try {
      const response = await axios.post(`/api/vendors/${vendorId}/stop-thread`);
      // Show success notification
      showNotification(response.data, false); // Green background
    } catch (error: any) {
      // Extract the error message if available
      const errorMessage =
        error.response?.data || "Failed to stop ticket release. Try again.";
      // Show error notification
      showNotification(errorMessage, true); // Red background
    }
  };

  return (
    <div className="container-menu">
      <div className="user-menu">
        <h3>Vendor Menu For: {vendorId}</h3>
        <button className="button" onClick={() => setShowAddTicketsPopup(true)}>
          Start Adding Tickets
        </button>
        <button
          className="button"
          onClick={() => setShowViewAddedTicketsPopup(true)}
        >
          View All My Added Tickets
        </button>
        <button className="button" onClick={handleViewAvailableTickets}>
          View Other Available Tickets
        </button>
        <button className="button" onClick={handleStopTicketRelease}>
          Stop Ticket Release
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
      {showAddTicketsPopup && (
        <AddTicketsPopup
          vendorId={vendorId}
          onClose={() => setShowAddTicketsPopup(false)}
          showNotification={showNotification}
        />
      )}

      {showViewAddedTicketsPopup && (
        <ViewAddedTicketsPopup
          vendorId={vendorId}
          onClose={() => setShowViewAddedTicketsPopup(false)}
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

export default VendorMenu;
