import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const SOCKET_URL = 'http://localhost:8080/ws';

export const connectWebSocket = (
  onTicketUpdate: (data: any) => void,
  onLogMessage: (data: any) => void
) => {
  const client = new Client({
    brokerURL: SOCKET_URL,
    connectHeaders: {},
    debug: (str) => console.log(str),
    reconnectDelay: 5000,
    webSocketFactory: () => new SockJS(SOCKET_URL),
    onConnect: () => {
      console.log('Connected to WebSocket');

      // Subscribe to the /topic/ticketpool for ticket updates
      client.subscribe('/topic/ticketpool', (message) => {
        try {
          const ticketData = JSON.parse(message.body);
          onTicketUpdate(ticketData);  // Callback for ticket update
        } catch (error) {
          console.error('Error parsing WebSocket message for ticket pool:', error);
        }
      });

      // Subscribe to the /topic/logs for log messages
      client.subscribe('/topic/logs', (message) => {
        try {
          const logMessage = message.body;  // Assuming the log message is a plain string
          onLogMessage(logMessage);  // Callback for log message
        } catch (error) {
          console.error('Error processing WebSocket message for logs:', error);
        }
      });
    },
    onDisconnect: () => console.log('Disconnected from WebSocket'),
    onStompError: (frame) => {
      console.error('STOMP error: ', frame);
    },
  });

  client.activate();
  return client;
};

