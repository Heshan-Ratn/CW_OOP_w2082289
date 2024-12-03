import React, { useState } from "react";
import axios from "axios";
import "./index.css";
import "./App.css";
import WebSocketComponent from "./components/WebSocketComponent";
import FullWhiteScreen from "./components/FullWhiteScreen";
import HalfHeightDiv from "./components/HalfHeightDiv";
import HalfWidthDiv from "./components/HalfWidthDiv";
import SidePlanelDiv from "./components/SidePlanelDiv";
import PopUpConfigWindow from "./components/PopUpConfigWindow";
import SetAdminCredentialsPopup from "./components/SetAdminCredentialsPopup"; // Import the updated pop-up component
import EditConfigurationSettingsPopup from "./components/EditConfigurationSettingsPopup";
import AvailableTicketsPopup from "./components/AvailableTicketsPopup";
import BookedTicketsPopup from "./components/BookedTicketsPopup";
import LoginPopup from "./components/LoginPopup";
import SignUpPopup from "./components/SignUpPopup";
import SignInPopup from "./components/SignInPopup";

const App: React.FC = () => {
  const buttonTexts = [
    "Configure Settings",
    "Login",
    "View All Available Tickets",
    "View All Booked Tickets",
    "Start System",
    "Stop System",
    "Exit Program",
  ];

  // State for managing pop-up visibility
  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const [isAdminPopupOpen, setIsAdminPopupOpen] = useState(false);
  const [isConfigEditPopupOpen, setIsConfigEditPopupOpen] = useState(false);
  const [isConfigViewPopupOpen, setIsConfigViewPopupOpen] = useState(false);
  const [configData, setConfigData] = useState<any>(null); // State for holding configuration data
  const [notification, setNotification] = useState<string | null>(null); // State for global notifications
  const [notificationClass, setNotificationClass] = useState<string | null>(
    null
  ); // State for notification class (error or success)

  const [isAvailableTicketsPopupOpen, setIsAvailableTicketsPopupOpen] =
    useState(false);
  const [availableTicketsData, setAvailableTicketsData] = useState<Record<
    string,
    number
  > | null>(null);

  const [isBookedTicketsPopupOpen, setIsBookedTicketsPopupOpen] =
    useState(false);
  const [bookedTicketsData, setBookedTicketsData] = useState<Record<
    string,
    number
  > | null>(null);

  // Functions for opening and closing pop-ups
  const handleOpenPopup = () => setIsPopupOpen(true);
  const handleClosePopup = () => setIsPopupOpen(false);

  const handleOpenAdminPopup = () => setIsAdminPopupOpen(true);
  const handleCloseAdminPopup = () => setIsAdminPopupOpen(false);

  const handleOpenConfigEditPopup = () => setIsConfigEditPopupOpen(true);
  const handleCloseConfigEditPopup = () => setIsConfigEditPopupOpen(false);

  const handleOpenConfigViewPopup = () => {
    setIsConfigViewPopupOpen(true);

    // Fetch configuration data when the "View Configuration Settings" pop-up opens
    axios
      .get("/api/configuration/view-configuration")
      .then((response) => {
        setConfigData(response.data); // Set the fetched config data in state
      })
      .catch((error) => {
        console.error("Error fetching configuration:", error);
        showNotification("Failed to load configuration data.", true); // Show error notification
      });
  };

  //
  const handleOpenAvailableTicketsPopup = () => {
    setIsAvailableTicketsPopupOpen(true);
    axios
      .get("/api/ticket-pool/available-tickets/event")
      .then((response) => {
        setAvailableTicketsData(response.data);
      })
      .catch((error) => {
        console.error("Error fetching available tickets:", error);
        setAvailableTicketsData(null);
      });
  };

  const handleCloseAvailableTicketsPopup = () => {
    setIsAvailableTicketsPopupOpen(false);
  };

  //
  const handleOpenBookedTicketsPopup = () => {
    setIsBookedTicketsPopupOpen(true);
    axios
      .get("/api/ticket-pool/booked-tickets/event") // Endpoint for booked tickets
      .then((response) => {
        setBookedTicketsData(response.data); // Update state with the fetched data
      })
      .catch((error) => {
        console.error("Error fetching booked tickets:", error);
        setBookedTicketsData(null); // Reset the data in case of an error
      });
  };

  const handleCloseBookedTicketsPopup = () => {
    setIsBookedTicketsPopupOpen(false);
  };

  const handleCloseConfigViewPopup = () => setIsConfigViewPopupOpen(false);

  const showNotification = (message: string, isError: boolean = false) => {
    setNotification(message);

    // Apply conditional class based on isError value
    const newNotificationClass = isError ? "error" : "success";
    setNotificationClass(newNotificationClass); // Update notification class

    // Clear notification after 5 seconds
    setTimeout(() => {
      setNotification(null);
      setNotificationClass(null); // Reset class after timeout
    }, 5000);
  };

  const handleStopSystem = () => {
    axios
      .post("/api/admin/stop-all-activity")
      .then((response) => {
        console.log(response.data);
        showNotification(
          "All ticket operations have been stopped, click the 'Start System' button to start the ticket operations again."
        );
      })
      .catch((error) => {
        console.error("Error stopping the system:", error);
        showNotification("Failed to stop the system. Please try again.", true);
      });
  };

  const handleStartSystem = () => {
    axios
      .post("/api/admin/resume-all-activity")
      .then((response) => {
        console.log(response.data);
        showNotification("All ticket operations have been resumed.");
      })
      .catch((error) => {
        console.error("Error resuming the system:", error);
        showNotification(
          "Failed to resume the system. Please try again.",
          true
        );
      });
  };

  const [isLoginPopupOpen, setIsLoginPopupOpen] = useState(false); // State for Login Popup
  const [isSignUpPopupOpen, setIsSignUpPopupOpen] = useState(false); // State for Sign-Up Popup
  const [isSignInPopupOpen, setIsSignInPopupOpen] = useState(false); // State for Sign-In Popup

  const handleOpenLoginPopup = () => setIsLoginPopupOpen(true); // Open Login Popup
  const handleCloseLoginPopup = () => setIsLoginPopupOpen(false); // Close Login Popup

  const handleOpenSignUpPopup = () => {
    setIsLoginPopupOpen(false); // Hide Login Popup
    setIsSignUpPopupOpen(true); // Show Sign-Up Popup
  };

  const handleCloseSignUpPopup = () => {
    setIsSignUpPopupOpen(false); // Hide Sign-Up Popup
    setIsLoginPopupOpen(true); // Restore Login Popup
  };
  const handleOpenSignInPopup = () => {
    setIsLoginPopupOpen(false); // Hide Login Popup
    setIsSignInPopupOpen(true); // Show Sign-In Popup
  };

  const handleCloseSignInPopup = () => {
    setIsSignInPopupOpen(false); // Hide Sign-In Popup
    setIsLoginPopupOpen(true); // Restore Login Popup
  };
  return (
    <div className="App app-container" style={{ minWidth: "514px" }}>
      <FullWhiteScreen>
        <HalfWidthDiv
          buttonTexts={buttonTexts}
          onButtonClick={(buttonText) => {
            if (buttonText === "Configure Settings") handleOpenPopup();
            if (buttonText === "View All Available Tickets")
              handleOpenAvailableTicketsPopup();
            if (buttonText === "View All Booked Tickets")
              handleOpenBookedTicketsPopup();
            if (buttonText === "Stop System") handleStopSystem();
            if (buttonText === "Start System") handleStartSystem();
            if (buttonText === "Login") handleOpenLoginPopup();
          }}
        />

        <SidePlanelDiv>
          <HalfHeightDiv />
          <HalfHeightDiv />
        </SidePlanelDiv>
      </FullWhiteScreen>
      <WebSocketComponent />

      {/* Pop-up components for different functionalities */}

      {isAvailableTicketsPopupOpen && (
        <AvailableTicketsPopup
          ticketsData={availableTicketsData}
          onClose={handleCloseAvailableTicketsPopup}
        />
      )}

      {isBookedTicketsPopupOpen && (
        <BookedTicketsPopup
          ticketsData={bookedTicketsData}
          onClose={handleCloseBookedTicketsPopup}
        />
      )}

      {isPopupOpen && (
        <PopUpConfigWindow
          onClose={handleClosePopup}
          onSetAdminCredentials={handleOpenAdminPopup}
          onEditConfigSetting={handleOpenConfigEditPopup}
          onViewConfigSettings={handleOpenConfigViewPopup}
        />
      )}

      {isAdminPopupOpen && (
        <SetAdminCredentialsPopup
          onClose={handleCloseAdminPopup}
          showNotification={showNotification}
        />
      )}

      {isConfigEditPopupOpen && (
        <EditConfigurationSettingsPopup
          onClose={handleCloseConfigEditPopup}
          showNotification={showNotification}
        />
      )}

      {isConfigViewPopupOpen && (
        <PopUpConfigWindow
          onClose={handleCloseConfigViewPopup}
          text="View Configuration Settings"
          configData={configData} // Only pass configData to the view configuration pop-up
        />
      )}

      {isLoginPopupOpen && (
        <LoginPopup
          onSignUp={handleOpenSignUpPopup}
          onSignIn={handleOpenSignInPopup}
          onClose={handleCloseLoginPopup}
        />
      )}

      {isSignUpPopupOpen && (
        <SignUpPopup
          onClose={handleCloseSignUpPopup}
          showNotification={showNotification}
        />
      )}

      {isSignInPopupOpen && <SignInPopup onClose={handleCloseSignInPopup} />}

      {/* Global notification bar */}
      {notification && (
        <div className={`notification-bar ${notificationClass}`}>
          {notification}
        </div>
      )}
    </div>
  );
};

export default App;
