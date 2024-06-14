import {toast} from "react-toastify";

export const toastError = (message, autoClose = 5000) => {
  toast.error(`Message: ${message}`, {
    position: "top-right",
    autoClose: autoClose
  });
};

export const toastSuccess = (message, autoClose = 5000) => {
  toast.success(`Message: ${message}`, {
    position: "top-right",
    autoClose: autoClose
  });
};

export const toastInfo = (message, autoClose = 5000) => {
  toast.info(`Message: ${message}`, {
    position: "top-right",
    autoClose: autoClose
  });
};