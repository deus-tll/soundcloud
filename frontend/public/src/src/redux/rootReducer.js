import {combineReducers} from "redux";
import authReducer from "../services/auth/authSliceService";
import {apiSliceService} from "../services/api/apiSliceService";


export const rootReducer = combineReducers({
  [apiSliceService.reducerPath]: apiSliceService.reducer,
  auth: authReducer
});