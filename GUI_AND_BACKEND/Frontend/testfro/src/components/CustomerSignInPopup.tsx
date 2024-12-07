import React, { useState } from "react";
import apiClient from "../api";
import CustomerMenu from "./CustomerMenu";

interface CustomerSignInPopupProps {
  onClose: () => void;
  showNotification: (message: string, isError?: boolean) => void;
}

const CustomerSignInPopup: React.FC<CustomerSignInPopupProps> = ({
  onClose,
  showNotification,
}) => {
  const [customerId, setCustomerId] = useState("");
  const [password, setPassword] = useState("");
  const [showCustomerMenu, setShowCustomerMenu] = useState(false);

  const validateInputs = (): boolean => {
    const customerIdRegex = /^[A-Za-z]{4}[0-9]{3}$/; // Customer ID format: 4 letters + 3 digits
    const passwordRegex = /^.{8,12}$/; // Password length: 8-12 characters

    if (!customerIdRegex.test(customerId)) {
      showNotification(
        "Invalid Customer ID: Must be 4 letters followed by 3 digits.",
        true
      );
      return false;
    }
    if (!passwordRegex.test(password)) {
      showNotification(
        "Invalid Password: Must be between 8-12 characters.",
        true
      );
      return false;
    }
    return true;
  };

  const handleSubmit = async () => {
    if (!validateInputs()) return;

    try {
      const response = await apiClient.post("/customers/signin", {
        customerId,
        password,
      });

      if (response.data.success) {
        const extractedCustomerId = response.data.data.customerId; // Extract customerId from response
        setCustomerId(extractedCustomerId);
        showNotification(response.data.message, false); // Success Notification
        setShowCustomerMenu(true); // Open the Customer Menu
      } else {
        showNotification(response.data.message, true); // Error Notification
      }
    } catch (error: any) {
      if (
        error.response &&
        error.response.data &&
        error.response.data.message
      ) {
        showNotification(error.response.data.message, true);
      } else {
        showNotification("Error: Unable to sign in. Please try again.", true);
      }
    }
  };

  return (
    <>
      <div className="popup-container">
        <div className="popup">
          <h3>Sign In as Customer</h3>
          <div className="form-group">
            <label>Username: </label>
            <input
              type="text"
              value={customerId}
              onChange={(e) => setCustomerId(e.target.value)}
              maxLength={7}
            />
          </div>
          <div className="form-group">
            <label>Password: </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              maxLength={12}
            />
          </div>
          <div className="popup-buttons">
            <button className="button" onClick={handleSubmit}>
              Submit
            </button>
            <button
              className="button"
              onClick={() => {
                setCustomerId("");
                setPassword("");
              }}
            >
              Reset
            </button>
            <button className="close-button" onClick={onClose}>
              Close
            </button>
          </div>
        </div>
      </div>

      {/* Render CustomerMenu if sign-in is successful */}
      {showCustomerMenu && (
        <CustomerMenu
          customerId={customerId}
          onClose={() => setShowCustomerMenu(false)} // Handle menu closure
          showNotification={showNotification}
        />
      )}
    </>
  );
};

export default CustomerSignInPopup;
