import {createSlice} from "@reduxjs/toolkit";


const authSliceService = createSlice({
  name: 'auth',
  initialState: { user: null, token: null },
  reducers: {
    setCredentials: (state, action) => {
      const { user, accessToken, rememberMe } = action.payload;

      state.user = user;
      state.token = accessToken;

      if (rememberMe) {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('user', JSON.stringify(user));
      } else {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('user');
      }
    },
    logOut: (state, action) => {
      state.user = null;
      state.token = null;

      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
    },
    updateUserFields: (state, action) => {
      const { fieldsToUpdate } = action.payload;

      Object.keys(fieldsToUpdate).forEach(field => {
        state.user[field] = fieldsToUpdate[field];
      });

      localStorage.setItem('user', JSON.stringify(state.user));
    }
  },
});

export const { setCredentials, logOut, updateUserFields } = authSliceService.actions;

export default authSliceService.reducer;

export const selectCurrentUser = (state) => state.auth.user;
export const selectCurrentToken = (state) => state.auth.token;