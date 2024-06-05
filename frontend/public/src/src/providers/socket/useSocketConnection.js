import {useState} from "react";
import SockJS from 'sockjs-client';
import {Stomp} from '@stomp/stompjs';

const SOCKET_SERVER_URL = "http://localhost/websocket-private";

const useSocketConnection = () => {
  const [socket, setSocket] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const connect = (token) => {
    try {
      if(!socket && token) {
        const sock = new SockJS(SOCKET_SERVER_URL);
        const stompClient = Stomp.over(sock);

        const onConnect = (frame) => {
          console.log('Connected to WebSocket');
          console.log('frame: ' + frame);
          setIsConnected(true);
        };

        const onDisconnect = (error) => {
          console.error('Disconnected from WebSocket:', error);
          setIsConnected(false);
        };

        // const onBeforeConnect = () => {
        //   stompClient.setHeaders({
        //     Authorization: `Bearer ${token}`,
        //   });
        // };

        stompClient.connect({Authorization: `Bearer ${token}`}, onConnect, onDisconnect);

        console.info("connectHeaders: ", stompClient.connectHeaders);

        setSocket(stompClient);
      }
    }
    catch (error) {
      console.error('Error connecting to WebSocket:', error);
      setErrorMessage(error.message);
    }
  };

  const disconnect = () => {
    if (socket) {
      socket.disconnect();
      setSocket(null);
      setIsConnected(false);
    }
  };

  const subscribeToTopic = (topic, callback) => {
    if (socket && isConnected) {
      return socket.subscribe(topic, callback);
    }
    else {
      console.error('Failed to subscribe to topic: ' + topic);
      return null;
    }
  };

  const unsubscribeFromTopic = (subscription) => {
    if (subscription) {
      subscription.unsubscribe();
    }
    else {
      console.error('Failed to unsubscribe from topic: ' + subscription.id);
    }
  };

  return { isConnected, errorMessage, connect, disconnect, subscribeToTopic, unsubscribeFromTopic };
};

export default useSocketConnection;