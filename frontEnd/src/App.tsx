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

  const handleCloseConfigViewPopup = () => setIsConfigViewPopupOpen(false);

  // // Function to show notifications globally
  // const showNotification = (message: string, isError: boolean = false) => {
  //   setNotification(message);

  //   // Apply conditional class based on isError value
  //   const notificationClass = isError ? "error" : "success";

  //   // Clear notification after 5 seconds
  //   setTimeout(() => setNotification(null), 5000); // Clear notification after 5 seconds

  //   // Update the notification class if needed
  //   setNotificationClass(notificationClass);
  // };

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

  return (
    <div className="App app-container" style={{ minWidth: "514px" }}>
      <FullWhiteScreen>
        <HalfWidthDiv
          buttonTexts={buttonTexts}
          onButtonClick={handleOpenPopup}
        />
        <SidePlanelDiv>
          <HalfHeightDiv />
          <HalfHeightDiv />
        </SidePlanelDiv>
      </FullWhiteScreen>
      <WebSocketComponent />

      {/* Pop-up components for different functionalities */}
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
