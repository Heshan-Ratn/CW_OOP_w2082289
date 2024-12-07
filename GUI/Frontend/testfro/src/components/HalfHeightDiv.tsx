import React, { ReactNode } from "react";

interface HalfHeightDivProps {
  children?: ReactNode; // Optional children prop
}

const HalfHeightDiv: React.FC<HalfHeightDivProps> = ({ children }) => {
  return <div className="half-height">{children}</div>;
};

export default HalfHeightDiv;
