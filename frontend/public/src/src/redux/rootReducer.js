import {combineReducers} from "redux";
import authReducer from "../services/auth/authSliceService";
import uploadStateReducer from "../services/upload/uploadSliceService";
import {apiSliceService} from "../services/api/apiSliceService";


export const rootReducer = combineReducers({
  [apiSliceService.reducerPath]: apiSliceService.reducer,
  auth: authReducer,
  fileUploadState: uploadStateReducer
});