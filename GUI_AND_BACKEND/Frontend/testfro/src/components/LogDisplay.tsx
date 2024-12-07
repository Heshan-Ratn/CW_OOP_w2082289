import React, { useEffect, useRef } from "react";

interface LogDisplayProps {
  messages: string[]; // Array of log messages
}

const LogDisplay: React.FC<LogDisplayProps> = ({ messages }) => {
  const logEndRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (logEndRef.current) {
      logEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  return (
    <div className="log-display">
      <ul>
        {messages.map((msg, index) => (
          <li key={index}>{msg}</li>
        ))}
      </ul>
      {/* Invisible element to ensure the view scrolls to the end */}
      <div ref={logEndRef}></div>
    </div>
  );
};

export default LogDisplay;
