import { apiSliceService } from "../api/apiSliceService";


export const authApiSliceService = apiSliceService.injectEndpoints({
  endpoints: builder => ({
    login: builder.mutation({
      query: data => ({
        url: '/auth/sign-in',
        method: 'POST',
        body: { ...data }
      })
    }),
    register: builder.mutation({
      query: data => ({
        url: '/auth/sign-up',
        method: 'POST',
        body: { ...data }
      })
    }),
    // verifyEmail: builder.mutation({
    //   query: data => ({
    //     url: '/auth/verify-email',
    //     method: 'POST',
    //     body: { ...data }
    //   })
    // }),
    // resendEmailVerificationLink: builder.mutation({
    //   query: data => ({
    //     url: '/auth/resend-email-verification-link',
    //     method: 'POST',
    //     body: { ...data }
    //   })
    // }),
  })
});

export const {
  useLoginMutation,
  useRegisterMutation,
  // useVerifyEmailMutation,
  // useResendEmailVerificationLinkMutation
} = authApiSliceService