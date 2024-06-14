import {createSelector, createSlice} from "@reduxjs/toolkit";

const initialState = JSON.parse(localStorage.getItem('fileUploadState')) || {};

const uploadSliceService = createSlice({
  name: 'fileUploadState',
  initialState,
  reducers: {
    setFileUploadState: (state, action) => {
      const { songId, fileId, uploadingState } = action.payload;
      state[songId] = { fileId, uploadingState };
      localStorage.setItem('fileUploadState', JSON.stringify(state));
    },
    clearFileUploadState: (state, action) => {
      const { songId } = action.payload;
      delete state[songId];
      localStorage.setItem('fileUploadState', JSON.stringify(state));
    }
  }
});

export const { setFileUploadState, clearFileUploadState } = uploadSliceService.actions;

export default uploadSliceService.reducer;

export const selectFileUploadState = createSelector(
  state => state.fileUploadState, // selector function to get the uploadState slice
  (_, songId) => songId, // second argument (songId) passed to the selector
  (uploadState, songId) => uploadState[songId] || {} // memoized result function
);