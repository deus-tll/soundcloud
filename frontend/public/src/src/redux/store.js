import {configureStore} from "@reduxjs/toolkit";
import {thunk} from "redux-thunk";

import {rootReducer} from "./rootReducer";
import {apiSliceService} from "../services/api/apiSliceService";


export const store = configureStore({
  reducer: rootReducer,
  middleware: getDefaultMiddleware => getDefaultMiddleware().concat(thunk, apiSliceService.middleware),
  devTools: true
});