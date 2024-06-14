import {apiSliceService} from "../api/apiSliceService";

export const songApiSliceService = apiSliceService.injectEndpoints({
  endpoints: builder => ({
    getSongs: builder.query({
      query: ({ page = 0, size = 10 }) => ({
        url: `/songs?page=${page}&size=${size}`,
        method: 'GET',
      })
    }),
    getSongById: builder.query({
      query: id => ({
        url: `/songs/${id}`,
        method: 'GET'
      })
    }),
    addSong: builder.mutation({
      query: data => ({
        url: '/songs',
        method: 'POST',
        body: data
      })
    }),
    updateSong: builder.mutation({
      query: ({ id, data }) => ({
        url: `/songs/${id}`,
        method: 'PUT',
        body: data
      })
    }),
    deleteSong: builder.mutation({
      query: id => ({
        url: `/songs/${id}`,
        method: 'DELETE'
      })
    })
  })
});

export const {
  useGetSongsQuery,
  useGetSongByIdQuery,
  useAddSongMutation,
  useUpdateSongMutation,
  useDeleteSongMutation
} = songApiSliceService;