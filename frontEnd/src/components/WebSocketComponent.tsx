import React, { useEffect } from "react";

const WebSocketComponent: React.FC = () => {
  useEffect(() => {
    // Create a WebSocket connection
    const socket = new WebSocket("ws://localhost:8080/ws");

    // Handle WebSocket events
    socket.onopen = () => {
      console.log("Connected to WebSocket");
      socket.send("Hello from the client!"); // Send a message to the server
    };

    socket.onmessage = (event) => {
      console.log("Received message:", event.data); // Log messages received from the server
    };

    socket.onerror = (error) => {
      console.error("WebSocket error:", error);
    };

    socket.onclose = () => {
      console.log("WebSocket connection closed");
    };

    // Cleanup the WebSocket connection when the component unmounts
    return () => {
      socket.close();
    };
  }, []);

  return (
    <div className="floatIgnore">
      <h2>WebSocket Client</h2>
      <p>Check the console for WebSocket connection status and messages.</p>
    </div>
  );
};

export default WebSocketComponent;
