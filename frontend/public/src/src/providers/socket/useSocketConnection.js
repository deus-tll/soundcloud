import {useState} from "react";
import {io} from 'socket.io-client';
import {handleConnect, handleDisconnect, handleMyNameIs, handlePing} from "./socketHandlers";

const SOCKET_SERVER_URL = process.env.REACT_APP_SOCKET_SERVER_URL;

const useSocketConnection = () => {
  const [socket, setSocket] = useState(null);

  const connect = (token) => {
    if (!socket && token) {
      const newSocket = io(SOCKET_SERVER_URL, {
        auth: {
          token: token
        }
      });

      newSocket.connect();

      newSocket.on('connect', handleConnect);
      newSocket.on('disconnect', handleDisconnect);
      newSocket.on('socket.myNameIs', handleMyNameIs);
      newSocket.on('ping', handlePing);

      setSocket(newSocket);
    }
  };

  const close = () => {
    socket?.off('connect', handleConnect);
    socket?.off('disconnect', handleDisconnect);
    socket?.off('socket.myNameIs', handleMyNameIs);
    socket?.off('ping', handlePing);

    socket?.close();
  };

  const on = (eventName, callBack) => {
    socket?.on(eventName, callBack);
  };

  const off = (eventName, callBack) => {
    socket?.off(eventName, callBack);
  };

  return { connect, close, on, off };
};

export default useSocketConnection;