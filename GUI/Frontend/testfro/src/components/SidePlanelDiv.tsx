import React, { ReactNode } from "react";

interface SidePlanelDivProps {
  children?: ReactNode;
}

const SidePlanelDiv: React.FC<SidePlanelDivProps> = ({ children }) => {
  return <div className="side-planel">{children}</div>;
};

export default SidePlanelDiv;
