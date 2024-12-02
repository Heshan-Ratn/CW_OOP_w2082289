import React, { useState } from "react";
import axios from "axios";

interface SetAdminCredentialsPopupProps {
  onClose: () => void;
  showNotification: (message: string, isError: boolean) => void; // Function to show notifications with error flag
}

const SetAdminCredentialsPopup: React.FC<SetAdminCredentialsPopupProps> = ({
  onClose,
  showNotification,
}) => {
  const [formData, setFormData] = useState({
    oldConfigAdminUser: "",
    oldConfigAdminPassword: "",
    newConfigAdminUser: "",
    newConfigAdminPassword: "",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value.slice(0, 50), // Limit input to 50 characters
    }));
  };

  const handleReset = () => {
    setFormData({
      oldConfigAdminUser: "",
      oldConfigAdminPassword: "",
      newConfigAdminUser: "",
      newConfigAdminPassword: "",
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (
      !formData.oldConfigAdminUser ||
      !formData.oldConfigAdminPassword ||
      !formData.newConfigAdminUser ||
      !formData.newConfigAdminPassword
    ) {
      showNotification("All fields are required.", true); // Show error notification
      return;
    }

    try {
      const response = await axios.put(
        "/api/configuration/update-admin-credentials",
        formData
      );
      showNotification(`Success: ${response.data}`, false); // Success message
      onClose(); // Close the pop-up on success
    } catch (error: any) {
      console.error("Error updating admin credentials:", error);
      showNotification(
        error.response?.data || "Failed to update admin credentials",
        true // Error message
      );
    }
  };

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>Set Admin Credentials</h2>
        <form onSubmit={handleSubmit}>
          <div>
            <label>Old Admin Username:</label>
            <input
              type="text"
              name="oldConfigAdminUser"
              value={formData.oldConfigAdminUser}
              onChange={handleChange}
              required
            />
          </div>
          <div>
            <label>Old Admin Password:</label>
            <input
              type="password"
              name="oldConfigAdminPassword"
              value={formData.oldConfigAdminPassword}
              onChange={handleChange}
              required
            />
          </div>
          <div>
            <label>New Admin Username:</label>
            <input
              type="text"
              name="newConfigAdminUser"
              value={formData.newConfigAdminUser}
              onChange={handleChange}
              required
            />
          </div>
          <div>
            <label>New Admin Password:</label>
            <input
              type="password"
              name="newConfigAdminPassword"
              value={formData.newConfigAdminPassword}
              onChange={handleChange}
              required
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

export default SetAdminCredentialsPopup;
