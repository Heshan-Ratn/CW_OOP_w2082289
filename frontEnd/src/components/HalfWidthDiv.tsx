// import React from "react";

// interface HalfWidthDivProps {
//   buttonTexts?: string[]; // Optional prop for button texts
//   onButtonClick: () => void; // Function to handle button clicks
// }

// const HalfWidthDiv: React.FC<HalfWidthDivProps> = ({
//   buttonTexts,
//   onButtonClick,
// }) => {
//   return (
//     <div className="half-width">
//       {buttonTexts ? (
//         buttonTexts.map((text, index) => (
//           <button
//             key={index}
//             className="button"
//             onClick={text === "Configure Settings" ? onButtonClick : undefined}
//           >
//             {text}
//           </button>
//         ))
//       ) : (
//         <p>No buttons here!</p>
//       )}
//     </div>
//   );
// };

// export default HalfWidthDiv;

import React from "react";

interface HalfWidthDivProps {
  buttonTexts?: string[]; // Optional prop for button texts
  onButtonClick: (buttonText: string) => void; // Function to handle button clicks
}

const HalfWidthDiv: React.FC<HalfWidthDivProps> = ({
  buttonTexts,
  onButtonClick,
}) => {
  return (
    <div className="half-width">
      {buttonTexts ? (
        buttonTexts.map((text, index) => (
          <button
            key={index}
            className="button"
            onClick={() => onButtonClick(text)} // Pass the button text to the handler
          >
            {text}
          </button>
        ))
      ) : (
        <p>No buttons here!</p>
      )}
    </div>
  );
};

export default HalfWidthDiv;
