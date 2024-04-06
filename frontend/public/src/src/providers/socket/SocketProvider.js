import {createContext, useContext, useEffect} from 'react';
import {useSelector} from "react-redux";

import useSocketConnection from "./useSocketConnection";
import {selectCurrentToken} from "../../services/auth/authSliceService";


const SocketContext = createContext();
export const useSocket = () => useContext(SocketContext);

const SocketProvider = ({ children }) => {
  const token = useSelector(selectCurrentToken);
  const socketConnection = useSocketConnection();

  useEffect(() => {
    socketConnection.connect(token);

    return () => {
      socketConnection.close();
    };
  }, [socketConnection, token]);

  return (
    <SocketContext.Provider value={{ socketConnection }}>
      {children}
    </SocketContext.Provider>
  );
};

export default SocketProvider;