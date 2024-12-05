// import React, { useState } from "react";
// import CustomerSignInPopup from "./CustomerSignInPopup";
// import VendorSignInPopup from "./VendorSignInPopup";

// interface SignInPopupProps {
//   onClose: () => void; // Function to close the Sign-In Popup
//   showNotification: (message: string, isError?: boolean) => void; // Notification handler
// }

// const SignInPopup: React.FC<SignInPopupProps> = ({
//   onClose,
//   showNotification,
// }) => {
//   const [showCustomerSignIn, setShowCustomerSignIn] = useState(false);
//   const [showVendorSignIn, setShowVendorSignIn] = useState(false);
//   const [customerId, setCustomerId] = useState<string | null>(null);
//   const [vendorId, setVendorId] = useState<string | null>(null);
//   const [showVendorMenu, setShowVendorMenu] = useState(false);
//   const [showCustomerMenu, setShowCustomerMenu] = useState(false);

//   const handleVendorSignInSuccess = (vendorId: string) => {
//     setVendorId(vendorId); // Store vendorId temporarily
//     setShowVendorMenu(true); // Open Vendor Menu
//   };

//   const handleCustomerSignInSuccess = (customerId: string) => {
//     setCustomerId(customerId); // Store customerId temporarily
//     setShowCustomerMenu(true); // Open Customer Menu
//   };

//   const handleMenuCloseForVendor = () => {
//     setShowVendorMenu(false); // Close Vendor Menu
//     setShowCustomerMenu(false); // Close Customer Menu
//     setShowVendorSignIn(true); // Open Vendor Sign-In again
//   };
//   const handleMenuCloseForCustomer = () => {
//     setShowVendorMenu(false); // Close Vendor Menu
//     setShowCustomerMenu(false); // Close Customer Menu
//     setShowCustomerSignIn(true); // Open Customer Sign-In again
//   };

//   return (
//     <div className="popup-container">
//       <div className="popup">
//         <h3>Sign In</h3>
//         <div className="popup-buttons">
//           <button className="button" onClick={() => setShowVendorSignIn(true)}>
//             Sign in as Vendor
//           </button>
//           <button
//             className="button"
//             onClick={() => setShowCustomerSignIn(true)}
//           >
//             Sign in as Customer
//           </button>
//           <button className="close-button" onClick={onClose}>
//             Close
//           </button>
//         </div>
//       </div>

//       {showCustomerSignIn && (
//         <CustomerSignInPopup
//           onClose={() => setShowCustomerSignIn(false)}
//           showNotification={showNotification}
//           onSignInSuccess={handleCustomerSignInSuccess} // Pass success handler
//         />
//       )}

//       {showVendorSignIn && (
//         <VendorSignInPopup
//           onClose={() => setShowVendorSignIn(false)}
//           showNotification={showNotification}
//           onSignInSuccess={handleVendorSignInSuccess} // Pass success handler
//         />
//       )}

//       {showVendorMenu && vendorId && (
//         <div className="container-menu">
//           <div className="user-menu">
//             <h3>Vendor Menu For: {vendorId}</h3> {/* Vendor Menu heading */}
//             <button className="button">Start Adding Tickets</button>
//             <button className="button">View Added Tickets</button>
//             <button className="button">View Other Available Tickets</button>
//             <button className="button">Stop Ticket Release</button>
//             <button className="button">Start Ticket Release</button>
//             <button className="close-button" onClick={handleMenuCloseForVendor}>
//               Close
//             </button>
//           </div>
//         </div>
//       )}

//       {showCustomerMenu && customerId && (
//         <div className="container-menu">
//           <div className="user-menu">
//             <h3>Customer Menu For: {customerId}</h3>{" "}
//             {/* Customer Menu heading */}
//             <button className="button">Purchase New Tickets</button>
//             <button className="button">View All My Booked Tickets</button>
//             <button className="button">View Other Available Tickets</button>
//             <button className="button">Stop Purchase of Tickets</button>
//             <button className="button">Start Purchase of Tickets</button>
//             <button
//               className="close-button"
//               onClick={handleMenuCloseForCustomer}
//             >
//               Close
//             </button>
//           </div>
//         </div>
//       )}
//     </div>
//   );
// };

// export default SignInPopup;

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
