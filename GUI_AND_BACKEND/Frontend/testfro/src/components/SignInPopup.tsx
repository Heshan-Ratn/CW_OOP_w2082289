import React, { useState } from "react";
import CustomerSignInPopup from "./CustomerSignInPopup";
import VendorSignInPopup from "./VendorSignInPopup";

interface SignInPopupProps {
  onClose: () => void;
  showNotification: (message: string, isError?: boolean) => void;
}

const SignInPopup: React.FC<SignInPopupProps> = ({
  onClose,
  showNotification,
}) => {
  const [showCustomerSignIn, setShowCustomerSignIn] = useState(false);
  const [showVendorSignIn, setShowVendorSignIn] = useState(false);

  return (
    <div className="popup-container">
      <div className="popup">
        <h3>Sign In</h3>
        <div className="popup-buttons">
          <button className="button" onClick={() => setShowVendorSignIn(true)}>
            Sign in as Vendor
          </button>
          <button
            className="button"
            onClick={() => setShowCustomerSignIn(true)}
          >
            Sign in as Customer
          </button>
          <button className="close-button" onClick={onClose}>
            Close
          </button>
        </div>
      </div>

      {showCustomerSignIn && (
        <CustomerSignInPopup
          onClose={() => setShowCustomerSignIn(false)}
          showNotification={showNotification}
        />
      )}

      {showVendorSignIn && (
        <VendorSignInPopup
          onClose={() => setShowVendorSignIn(false)}
          showNotification={showNotification}
        />
      )}
    </div>
  );
};

export default SignInPopup;
