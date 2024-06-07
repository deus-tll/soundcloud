import {apiSliceService} from "../api/apiSliceService";

export const performerApiSliceService = apiSliceService.injectEndpoints({
    endpoints: builder => ({
        getPerformers: builder.query({
            query: ({ page = 0, size = 10 }) => ({
                url: `/performers?page=${page}&size=${size}`,
                method: 'GET',
            })
        }),
        getPerformerById: builder.query({
            query: id => ({
                url: `/performers/${id}`,
                method: 'GET'
            })
        }),
        addPerformer: builder.mutation({
            query: data => ({
                url: '/performers',
                method: 'POST',
                body: data,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
        }),
        updatePerformer: builder.mutation({
            query: ({ id, data }) => ({
                url: `/performers/${id}`,
                method: 'PUT',
                body: data,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
        }),
        deletePerformer: builder.mutation({
            query: id => ({
                url: `/performers/${id}`,
                method: 'DELETE'
            })
        })
    })
});

export const {
    useGetPerformersQuery,
    useGetPerformerByIdQuery,
    useAddPerformerMutation,
    useUpdatePerformerMutation,
    useDeletePerformerMutation
} = performerApiSliceService;