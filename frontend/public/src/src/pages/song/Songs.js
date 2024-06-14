import React, {useEffect, useState} from "react";
import {useDeleteSongMutation, useGetSongsQuery} from "../../services/song/songApiSliceService";
import {toast} from "react-toastify";
import {Alert, Button, Container, Pagination, Table} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";
import DeleteModal from "../../components/reusable/DeleteModal";
import {Link, useLocation} from "react-router-dom";

const Songs = () => {
  const [page, setPage] = useState(0);
  const { data, error: errorGettingSongs, isLoading, refetch } = useGetSongsQuery({ page });
  const [deleteSong] = useDeleteSongMutation();
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedSong, setSelectedSong] = useState(null);
  const location = useLocation();

  const songs = data?.content || [];
  console.log(songs);
  const totalPages = data?.totalPages || 1;

  useEffect(() => {
    if (location.state?.refetch) {
      refetch();
      window.history.replaceState({}, document.title)
    }
  }, [location.state, refetch]);

  const handleDelete = (song) => {
    setSelectedSong(song);
    setShowDeleteModal(true);
  };

  const confirmDelete = async () => {
    try {
      await deleteSong(selectedSong.id);
      setShowDeleteModal(false);
      refetch();
    } catch (error) {
      let errorMessage;

      if (errorGettingSongs?.status) {
        const status = errorGettingSongs.data?.status;
        const message = errorGettingSongs.data?.message;

        if (errorGettingSongs.data) {
          errorMessage = `${status ? status : ''}: ${message ? message : ''}.`;
        } else {
          errorMessage = "Something went wrong. Try later";
        }
      } else {
        errorMessage = 'Deleting song failed';
      }

      toast.error(`Couldn't delete song with id [${selectedSong.id}] and name [${selectedSong.name}]. ${errorMessage}`, {
        position: "top-right",
        autoClose: 5000
      });
    }
  };

  if (isLoading) return <div>Loading...</div>;
  if (errorGettingSongs) {
    let errorMessage;

    if (errorGettingSongs?.status) {
      const status = errorGettingSongs.data?.status;
      const message = errorGettingSongs.data?.message;

      if (errorGettingSongs.data) {
        errorMessage = `${status ? status : ''}: ${message ? message : ''}.`;
      } else {
        errorMessage = "Something went wrong. Try later";
      }
    } else {
      errorMessage = 'Getting page of songs failed';
    }

    return <Alert variant="danger">{errorMessage}</Alert>;
  }

  return (
    <Container>
      <h1 className="text-center">Songs</h1>
      <div className="d-flex align-items-center justify-content-center mt-5">
        <LinkContainer to="/songs/add">
          <Button variant="primary" className="mb-3">Add Song</Button>
        </LinkContainer>
      </div>
      <Table striped bordered hover>
        <thead>
        <tr>
          <th>Cover</th>
          <th>Title</th>
          <th>Url</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        {songs.map((song) => (
          <tr key={song.id}>
            <td><img src={song.coverUrl} alt={song.name} width="50"/></td>
            <td>{song.name}</td>
            <td>
              <Link to={song.url}>Download</Link>
            </td>
            <td>
              <LinkContainer to={`/songs/edit/${song.id}`}>
                <Button variant="warning" className="me-2">Update</Button>
              </LinkContainer>
              <Button variant="danger" onClick={() => handleDelete(song)}>Delete</Button>
            </td>
          </tr>
        ))}
        </tbody>
      </Table>
      <Pagination>
        {Array.from({length: totalPages}, (_, number) => (
          <Pagination.Item key={number} active={number === page} onClick={() => setPage(number)}>
            {number + 1}
          </Pagination.Item>
        ))}
      </Pagination>
      <DeleteModal
        show={showDeleteModal}
        onHide={() => setShowDeleteModal(false)}
        onConfirm={confirmDelete}
      />
    </Container>
  );
}

export default Songs;