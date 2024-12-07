import React, { ReactNode } from "react";

interface FullWhiteScreenProps {
  children: ReactNode; // Defines the type for child elements
}

const FullWhiteScreen: React.FC<FullWhiteScreenProps> = ({ children }) => {
  return (
    <div className="fullscreen">
      {children} {/* Render the children passed to the component */}
    </div>
  );
};

export default FullWhiteScreen;
