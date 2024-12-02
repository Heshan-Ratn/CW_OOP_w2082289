import React from "react";

interface HalfWidthDivProps {
  buttonTexts?: string[]; // Optional prop for button texts
  onButtonClick: () => void; // Function to handle button clicks
}

const HalfWidthDiv: React.FC<HalfWidthDivProps> = ({
  buttonTexts,
  onButtonClick,
}) => {
  return (
    <div className="half-width">
      {/* {buttonTexts ? (
        buttonTexts.map((text, index) => (
          <button key={index} className="button">
            {text}
          </button>
        ))
      ) : (
        <p>No buttons here!</p>
      )} */}
      {buttonTexts ? (
        buttonTexts.map((text, index) => (
          <button
            key={index}
            className="button"
            onClick={text === "Configure Settings" ? onButtonClick : undefined}
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
