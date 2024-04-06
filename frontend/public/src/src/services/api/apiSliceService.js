import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
//import { setCredentials, logOut } from "../auth/authSliceService";


const baseQuery = fetchBaseQuery({
  baseUrl: process.env.REACT_APP_API_BASE_URL,
  credentials: 'include',
  prepareHeaders: (headers, { getState }) => {
    const token = getState().auth.token;

    if(token) {
      headers.set("Authorization", `Bearer ${token}`);
    }
    return headers;
  }
});

const baseQueryWithReAuth = async (args, api, extraOptions) => {
  let result = await baseQuery(args, api, extraOptions);

  // for the future option to add refresh token

  // if (result?.error?.status === 403) {
  //   console.log('sending refresh token');
  //
  //   const refreshResult = await baseQuery('auth/refresh-token', api, extraOptions);
  //   console.log(refreshResult);
  //
  //   if (!refreshResult?.error) {
  //     const { accessToken } = refreshResult.data;
  //     const user = api.getState().auth.user;
  //
  //     // Store the new token
  //     api.dispatch(setCredentials({ user, accessToken }));
  //
  //     // Retry the original query with new access token
  //     result = await baseQuery(args, api, extraOptions);
  //   } else {
  //     api.dispatch(logOut());
  //   }
  // }

  return result;
};

export const apiSliceService = createApi({
  baseQuery: baseQueryWithReAuth,
  endpoints: builder => ({})
})