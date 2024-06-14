import {apiSliceService} from "../api/apiSliceService";

export const uploadApiSliceService = apiSliceService.injectEndpoints({
  endpoints: builder => ({
    requestUpload: builder.mutation({
      query: () => ({
        url: '/upload/request',
        method: 'POST'
      }),
      transformResponse: (response) => {
        return response.fileId;
      }
    }),
    checkFile: builder.query({
      query: fileId => ({
        url: `/upload/check-file/${fileId}`,
        method: 'GET'
      })
    })
  })
});

export const { useRequestUploadMutation, useCheckFileQuery } = uploadApiSliceService;