import React from 'react';
import ReactDOM from 'react-dom/client';

import {Provider} from "react-redux";
import {BrowserRouter, Routes, Route} from "react-router-dom";

import {store} from "./redux/store";
import AuthProvider from "./providers/auth/AuthProvider";
import SocketProvider from "./providers/socket/SocketProvider";

import './index.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'react-toastify/dist/ReactToastify.css';

import App from './App';


const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
      <Provider store={store}>
          <AuthProvider>
              <SocketProvider>
                  <BrowserRouter>
                      <Routes>
                          <Route path="/*" element={<App />}/>
                      </Routes>
                  </BrowserRouter>
              </SocketProvider>
          </AuthProvider>
      </Provider>
  </React.StrictMode>
);