import React, { useState } from "react";
import axios from "axios";

interface EditConfigurationSettingsPopupProps {
  onClose: () => void;
  showNotification: (message: string, isError: boolean) => void; // Function to show notifications with error flag
}

const EditConfigurationSettingsPopup: React.FC<
  EditConfigurationSettingsPopupProps
> = ({ onClose, showNotification }) => {
  const [formData, setFormData] = useState({
    configAdminUser: "",
    configAdminPassword: "",
    ticketReleaseRate: "",
    customerRetrievalRate: "",
    maxTicketCapacity: "",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    // Allow numbers with up to 14 digits and 2 decimals for rates
    if (name === "ticketReleaseRate" || name === "customerRetrievalRate") {
      const regex = /^[0-9]{1,12}(\.[0-9]{0,2})?$/; // Validates 14 digits and 2 decimals
      if (regex.test(value) || value === "") {
        setFormData((prevData) => ({
          ...prevData,
          [name]: value.slice(0, 16), // Limit input to 14 digits and 2 decimals
        }));
      }
    } else {
      setFormData((prevData) => ({
        ...prevData,
        [name]: value.slice(0, 50), // Limit input to 50 characters
      }));
    }
  };

  const handleReset = () => {
    setFormData({
      configAdminUser: "",
      configAdminPassword: "",
      ticketReleaseRate: "",
      customerRetrievalRate: "",
      maxTicketCapacity: "",
    });
  };

  // const handleSubmit = async (e: React.FormEvent) => {
  //   e.preventDefault();
  //   if (
  //     !formData.configAdminUser ||
  //     !formData.configAdminPassword ||
  //     !formData.ticketReleaseRate ||
  //     !formData.customerRetrievalRate ||
  //     !formData.maxTicketCapacity
  //   ) {
  //     showNotification("All fields are required.", true); // Show error notification
  //     return;
  //   }

  //   const payload = {
  //     configAdminUser: formData.configAdminUser,
  //     configAdminPassword: formData.configAdminPassword,
  //     ticketReleaseRate: parseFloat(formData.ticketReleaseRate),
  //     customerRetrievalRate: parseFloat(formData.customerRetrievalRate),
  //     maxTicketCapacity: parseInt(formData.maxTicketCapacity, 10),
  //   };

  //   try {
  //     const response = await axios.put(
  //       "/api/configuration/update-ticket-settings",
  //       payload
  //       // formData
  //     );
  //     showNotification(`Success: ${response.data}`, false); // Success message
  //     onClose(); // Close the pop-up on success
  //   } catch (error: any) {
  //     console.error("Error updating configuration settings:", error);
  //     showNotification(
  //       error.response?.data || "Failed to update configuration settings",
  //       true // Show error notification in red
  //     );
  //   }
  // };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (
      !formData.configAdminUser ||
      !formData.configAdminPassword ||
      !formData.ticketReleaseRate ||
      !formData.customerRetrievalRate ||
      !formData.maxTicketCapacity
    ) {
      showNotification("All fields are required.", true); // Show error notification
      return;
    }

    const payload = {
      configAdminUser: formData.configAdminUser,
      configAdminPassword: formData.configAdminPassword,
      ticketReleaseRate: parseFloat(formData.ticketReleaseRate),
      customerRetrievalRate: parseFloat(formData.customerRetrievalRate),
      maxTicketCapacity: parseInt(formData.maxTicketCapacity, 10),
    };

    try {
      const response = await axios.put(
        "/api/configuration/update-ticket-settings",
        payload
      );
      showNotification(`Success: ${response.data}`, false); // Success notification
      onClose(); // Close the pop-up on success
    } catch (error: any) {
      console.error("Error updating configuration settings:", error);
      const errorMessage =
        error.response?.data || "Failed to update configuration settings";
      showNotification(errorMessage, true); // Show error notification
    }
  };

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>Edit Configuration Settings</h2>
        <form onSubmit={handleSubmit}>
          <div>
            <label>Admin Username:</label>
            <input
              type="text"
              name="configAdminUser"
              value={formData.configAdminUser}
              onChange={handleChange}
              required
            />
          </div>
          <div>
            <label>Admin Password:</label>
            <input
              type="password"
              name="configAdminPassword"
              value={formData.configAdminPassword}
              onChange={handleChange}
              required
            />
          </div>
          <div>
            <label>Ticket Release Rate:</label>
            <input
              type="number"
              name="ticketReleaseRate"
              value={formData.ticketReleaseRate}
              onChange={handleChange}
              step="0.01"
              min="0"
              max="99999999999999.99" // Max allowed value based on 14 digits, 2 decimals
              required
            />
          </div>
          <div>
            <label>Customer Retrieval Rate:</label>
            <input
              type="number"
              name="customerRetrievalRate"
              value={formData.customerRetrievalRate}
              onChange={handleChange}
              step="0.01"
              min="0"
              max="99999999999999.99" // Max allowed value based on 14 digits, 2 decimals
              required
            />
          </div>
          <div>
            <label>Max Ticket Capacity:</label>
            <input
              type="number"
              name="maxTicketCapacity"
              value={formData.maxTicketCapacity}
              onChange={handleChange}
              required
              min="0"
            />
          </div>
          <div className="form-actions">
            <button className="button" type="submit">
              Submit
            </button>
            <button className="button" type="button" onClick={handleReset}>
              Reset
            </button>
            <button className="button-close" type="button" onClick={onClose}>
              Close
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditConfigurationSettingsPopup;
