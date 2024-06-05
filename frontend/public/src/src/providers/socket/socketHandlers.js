import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

//// Default Events
////////////////////////
export const handleConnect = () => {
  console.log('Connected to socket server');
};

export const handleDisconnect = () => {
  console.log('Disconnected from socket server');
};

export const handleMyNameIs = (data) => {
  console.log('Connect to server: ' + data);
};

export const handlePing = (data) => {
  console.log('Ping from server:', data);
};

export const avatarIsReady = (data) => {
  console.log('Avatar is ready:', data);
  toast.success(`Avatar is ready! Message: ${data.message}`, {
    position: "top-right",
    autoClose: 3000
  });
};