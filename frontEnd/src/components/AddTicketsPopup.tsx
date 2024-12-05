import React, { useState } from "react";
import axios from "axios";

interface AddTicketsPopupProps {
  vendorId: string;
  onClose: () => void;
  showNotification: (message: string, isError?: boolean) => void;
}

const AddTicketsPopup: React.FC<AddTicketsPopupProps> = ({
  vendorId,
  onClose,
  showNotification,
}) => {
  const [eventName, setEventName] = useState("");
  const [price, setPrice] = useState<number | "">("");
  const [timeDuration, setTimeDuration] = useState("");
  const [date, setDate] = useState("");
  const [batchSize, setBatchSize] = useState<number | "">("");

  const validateInputs = (): boolean => {
    if (!eventName || eventName.length > 50) {
      showNotification("Event Name must be between 1 and 50 characters.", true);
      return false;
    }
    if (!timeDuration || timeDuration.length > 50) {
      showNotification(
        "Time Duration must be between 1 and 50 characters.",
        true
      );
      return false;
    }
    if (
      !price ||
      !/^\d{1,8}(\.\d{1,2})?$/.test(price.toString()) ||
      parseFloat(price.toString()) <= 0
    ) {
      showNotification(
        "Price must be a positive number with up to 8 digits and 2 decimal places.",
        true
      );
      return false;
    }
    if (!date || new Date(date) < new Date("2024-01-01")) {
      showNotification("Date must be from January 1, 2024, onwards.", true);
      return false;
    }
    if (!batchSize || batchSize < 1 || batchSize > 100) {
      showNotification("Batch Size must be a number between 1 and 100.", true);
      return false;
    }
    return true;
  };

  const handleSubmit = async () => {
    if (!validateInputs()) return;

    const payload = {
      event_Name: eventName,
      price: parseFloat(price.toString()), // Ensure it's a floating-point value
      time_Duration: timeDuration,
      date: date,
      batch_Size: batchSize,
    };

    try {
      const response = await axios.post(
        `/api/vendors/${vendorId}/start-thread`,
        payload
      );
      showNotification(response.data, false); // Green notification
      onClose(); // Close popup on success
    } catch (error: any) {
      if (error.response?.data) {
        showNotification(error.response.data, true); // Red notification
      } else {
        showNotification("An error occurred. Please try again.", true);
      }
    }
  };

  const handleReset = () => {
    setEventName("");
    setPrice("");
    setTimeDuration("");
    setDate("");
    setBatchSize("");
  };

  return (
    <div className="popup-container">
      <div className="popup-tickets">
        <h3>Start Releasing Tickets To System</h3>
        <div className="form-group-tickets">
          <label>Event Name: </label>
          <input
            type="text"
            value={eventName}
            maxLength={50}
            onChange={(e) => setEventName(e.target.value)}
          />
        </div>
        <div className="form-group-tickets">
          <label>Price: </label>
          <input
            type="number"
            value={price}
            onChange={(e) => setPrice(Number(e.target.value))}
            placeholder="Max 8 digits, 2 decimals"
          />
        </div>
        <div className="form-group-tickets">
          <label>Time Duration: </label>
          <input
            type="text"
            value={timeDuration}
            maxLength={50}
            onChange={(e) => setTimeDuration(e.target.value)}
          />
        </div>
        <div className="form-group-tickets">
          <label>Date: </label>
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
          />
        </div>
        <div className="form-group-tickets">
          <label>Ticket Batch Size: </label>
          <input
            type="number"
            value={batchSize}
            onChange={(e) => setBatchSize(Number(e.target.value))}
            placeholder="1-100"
          />
        </div>
        <div className="popup-buttons">
          <button className="button" onClick={handleSubmit}>
            Submit
          </button>
          <button className="button" onClick={handleReset}>
            Reset
          </button>
          <button className="close-button" onClick={onClose}>
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default AddTicketsPopup;
