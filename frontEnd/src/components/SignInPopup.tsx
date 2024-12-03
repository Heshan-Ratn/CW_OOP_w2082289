import React from "react";

interface SignInPopupProps {
  onClose: () => void; // Function to close the Sign-In Popup
}

const SignInPopup: React.FC<SignInPopupProps> = ({ onClose }) => {
  return (
    <div className="popup-container">
      <div className="popup">
        <h3>Sign In</h3>
        <div className="popup-buttons">
          <button
            className="button"
            onClick={() => console.log("Sign in as Vendor clicked")}
          >
            Sign in as Vendor
          </button>
          <button
            className="button"
            onClick={() => console.log("Sign in as Customer clicked")}
          >
            Sign in as Customer
          </button>
          <button className="close-button" onClick={onClose}>
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default SignInPopup;
