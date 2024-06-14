import {toastSuccess} from "../../helpers/toastNotification";

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

export const avatarIsReady = (socketConnection, subscription, data) => {
  console.log('Avatar is ready:', data);
  toastSuccess(`Avatar is ready! Message: ${data.message}`, 5000);
  socketConnection.unsubscribeFromTopic(subscription);
};