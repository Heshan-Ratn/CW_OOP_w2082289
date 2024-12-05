import React, { useState } from "react";
import SignUpVendorPopup from "./SignUpVendorPopup";
import SignUpCustomerPopup from "./SignUpCustomerPopup";

interface SignUpPopupProps {
  onClose: () => void; // Function to close the Sign-Up Popup.
  showNotification: (message: string, isError?: boolean) => void; // Notification handler
}

const SignUpPopup: React.FC<SignUpPopupProps> = ({
  onClose,
  showNotification,
}) => {
  const [isVendorSignUpOpen, setIsVendorSignUpOpen] = useState(false);
  const [isCustomerSignUpOpen, setIsCustomerSignUpOpen] = useState(false);

  return (
    <>
      <div className="popup-container">
        <div className="popup">
          <h3>Sign Up</h3>
          <div className="popup-buttons">
            <button
              className="button"
              onClick={() => setIsVendorSignUpOpen(true)} // Open the Vendor sign-up popup
            >
              Sign up as Vendor
            </button>
            <button
              className="button"
              onClick={() => setIsCustomerSignUpOpen(true)}
            >
              Sign up as Customer
            </button>
            <button className="close-button" onClick={onClose}>
              Close
            </button>
            {/* <button
              className="close-button"
              onClick={() => {
                setIsVendorSignUpOpen(false);
                setIsCustomerSignUpOpen(false);
                onClose(); // Reopen LoginPopup
              }}
            >
              Close
            </button> */}
          </div>
        </div>
      </div>
      {isVendorSignUpOpen && (
        <SignUpVendorPopup
          onClose={() => setIsVendorSignUpOpen(false)} // Close the vendor sign-up popup
          showNotification={showNotification}
        />
      )}

      {isCustomerSignUpOpen && (
        <SignUpCustomerPopup
          onClose={() => setIsCustomerSignUpOpen(false)} // Close the Customer sign-up popup
          showNotification={showNotification}
        />
      )}
    </>
  );
};

export default SignUpPopup;
