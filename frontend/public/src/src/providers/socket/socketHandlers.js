import {updateUserFields} from "../../services/auth/authSliceService";


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


//// Avatars Stored Event
////////////////////////
let avatarsStoredCallback;

export const setAvatarsStoredCallback = (callback) => {
  avatarsStoredCallback = callback;
};

export const handleAvatarsStored = (socketConnection, dispatch, data, eventName) => {
  const avatars = JSON.parse(data);
  console.log('Received avatars:', avatars);

  const fieldsToUpdate = {
    avatars
  };

  dispatch(updateUserFields({fieldsToUpdate}));

  if (avatarsStoredCallback) {
    socketConnection.off(eventName, avatarsStoredCallback);
  }
};
////////////////////////