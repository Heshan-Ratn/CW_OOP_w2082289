import React, { useState } from "react";
import axios from "axios";
import apiClient from "../api";

interface SignUpVendorPopupProps {
  onClose: () => void;
  showNotification: (message: string, isError?: boolean) => void;
}

const SignUpVendorPopup: React.FC<SignUpVendorPopupProps> = ({
  onClose,
  showNotification,
}) => {
  const [vendorId, setVendorId] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSignUp = async () => {
    const vendorIdRegex = /^[A-Za-z]{4}\d{3}$/;
    if (!vendorIdRegex.test(vendorId)) {
      showNotification(
        "Vendor ID must be 4 letters followed by 3 digits.",
        true
      );
      return;
    }
    if (password.length < 8 || password.length > 12) {
      showNotification("Password must be 8-12 characters long.", true);
      return;
    }

    setIsLoading(true);
    try {
      const response = await apiClient.post("/vendors/signup", {
        vendorId,
        password,
      });
      showNotification(response.data); // Success
      onClose(); // Close popup on success
    } catch (error: any) {
      const errorMessage =
        error.response?.data || "Failed to sign up. Please try again.";
      showNotification(errorMessage, true); // Error
    } finally {
      setIsLoading(false);
    }
  };

  const handleReset = () => {
    setVendorId("");
    setPassword("");
  };

  return (
    <div className="popup-container">
      <div className="popup-content ">
        <h2>Sign up as a Vendor</h2>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleSignUp();
          }}
        >
          <div>
            <label>Username (4 letters + 3 digits):</label>
            <input
              type="text"
              value={vendorId}
              onChange={(e) => setVendorId(e.target.value)}
              maxLength={7}
              required
            />
          </div>
          <div>
            <label>Password (8-12 characters):</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <div className="form-actions">
            <button className="button" type="submit" disabled={isLoading}>
              {isLoading ? "Signing Up..." : "Submit"}
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

export default SignUpVendorPopup;
