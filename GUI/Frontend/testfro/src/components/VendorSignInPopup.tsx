import React, { useState } from "react";
import apiClient from "../api";
import VendorMenu from "./VendorMenu";

interface VendorSignInPopupProps {
  onClose: () => void;
  showNotification: (message: string, isError?: boolean) => void;
}

const VendorSignInPopup: React.FC<VendorSignInPopupProps> = ({
  onClose,
  showNotification,
}) => {
  const [vendorId, setVendorId] = useState("");
  const [password, setPassword] = useState("");
  const [showVendorMenu, setShowVendorMenu] = useState(false);

  const validateInputs = (): boolean => {
    const vendorIdRegex = /^[A-Za-z]{4}[0-9]{3}$/;
    const passwordRegex = /^.{8,12}$/;

    if (!vendorIdRegex.test(vendorId)) {
      showNotification(
        "Invalid Vendor ID: Must be 4 letters followed by 3 digits.",
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
      const response = await apiClient.post("/vendors/signin", {
        vendorId,
        password,
      });

      if (response.data.success) {
        const extractedVendorId = response.data.data.vendorId; // Extract vendorId from response
        setVendorId(extractedVendorId);
        showNotification(response.data.message, false); // Success Notification
        setShowVendorMenu(true); // Open the Vendor Menu
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
          <h3>Sign In as Vendor</h3>
          <div className="form-group">
            <label>Username: </label>
            <input
              type="text"
              value={vendorId}
              onChange={(e) => setVendorId(e.target.value)}
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
                setVendorId("");
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

      {/* Render VendorMenu if sign-in is successful */}
      {showVendorMenu && (
        <VendorMenu
          vendorId={vendorId}
          onClose={() => setShowVendorMenu(false)} // Handle menu closure
          showNotification={showNotification}
        />
      )}
    </>
  );
};

export default VendorSignInPopup;
