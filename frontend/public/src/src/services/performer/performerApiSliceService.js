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
        getAllPerformers: builder.query({
            query: () => ({
                url: `/performers/all`,
                method: 'GET',
            })
        }),
        addPerformer: builder.mutation({
            query: data => ({
                url: '/performers',
                method: 'POST',
                body: data
            })
        }),
        updatePerformer: builder.mutation({
            query: ({ id, data }) => ({
                url: `/performers/${id}`,
                method: 'PUT',
                body: data
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
    useGetAllPerformersQuery,
    useAddPerformerMutation,
    useUpdatePerformerMutation,
    useDeletePerformerMutation
} = performerApiSliceService;