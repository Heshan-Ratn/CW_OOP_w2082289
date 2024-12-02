// import React from "react";

// const HalfHeightDiv: React.FC = () => {
//   return <div className="half-height">This is a half-height div.</div>;
// };

// export default HalfHeightDiv;

import React, { ReactNode } from "react";

interface HalfHeightDivProps {
  children?: ReactNode; // Optional children prop
}

const HalfHeightDiv: React.FC<HalfHeightDivProps> = ({ children }) => {
  return <div className="half-height">{children}</div>;
};

export default HalfHeightDiv;
