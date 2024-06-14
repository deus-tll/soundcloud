import {useNavigate, useParams} from "react-router-dom";
import {useGetSongByIdQuery, useUpdateSongMutation} from "../../services/song/songApiSliceService";
import {useGetAllPerformersQuery} from "../../services/performer/performerApiSliceService";
import {useEffect, useRef, useState} from "react";
import {Alert, Button, Container, Form} from "react-bootstrap";
import Select from "react-select";
import {useDispatch, useSelector} from "react-redux";
import {clearFileUploadState, selectFileUploadState} from "../../services/upload/uploadSliceService";
import {useCheckFileQuery} from "../../services/upload/uploadApiSliceService";
import {toastError, toastInfo, toastSuccess} from "../../helpers/toastNotification";
import UploadFile from "../../components/upload/UploadFile";
import {UploadingStates} from "./helpers";

const EditSong = () => {
  const { id } = useParams();
  const { data: song, error: errorGettingSong, isLoading } = useGetSongByIdQuery(id);
  const { data: performers, isLoading: isLoadingPerformers } = useGetAllPerformersQuery();
  const [name, setName] = useState('');
  const [selectedPerformers, setSelectedPerformers] = useState([]);
  const [cover, setCover] = useState(null);
  const [isWaitingForResponse, setIsWaitingForResponse] = useState(false);
  const [errors, setErrors] = useState({});

  const { fileId, uploadingState } = useSelector((state) => selectFileUploadState(state, id));

  const [updateSong] = useUpdateSongMutation();
  const { refetch: checkFileRefetch } = useCheckFileQuery();

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const nameRef = useRef();
  const errorRef = useRef();


  useEffect(() => {
    if (song) {
      setName(song.name);
      const performerOptions = song.performers.map(performer => ({
        value: performer.id,
        label: performer.name
      }));
      setSelectedPerformers(performerOptions);
    }
  }, [song]);

  useEffect(() => {
    nameRef?.current?.focus();
  }, []);

  const checkFileStatus = async (fileId) => {
    const interval = setInterval(async () => {
      try {
        const result = await checkFileRefetch({ fileId });
        if (result.data) {
          clearInterval(interval);
          await updateSongDetails();
        }
      } catch (error) {
        console.error('Failed to check file status: ', error);
      }
    }, 3000);
  };

  const updateSongDetails = async () => {
    const formData = new FormData();
    if (name) formData.append('name', name);
    if (selectedPerformers.length) formData.append('performerIds', selectedPerformers.map(p => p.value));
    if (fileId) formData.append('fileId', fileId);
    if (cover) formData.append('cover', cover);

    try {
      await updateSong({ id, data: formData }).unwrap();
      dispatch(clearFileUploadState({ id }));
      toastSuccess("Song was successfully updated!", 10000);
      navigate('/songs', { state: { refetch: true } });
    } catch (error) {
      let errorMessage;
      if (error.status) {
        const status = error?.data?.status;
        const message = error?.data?.message;

        if (error.data) {
          errorMessage = `${status ? status : ''}: ${message ? message : ''}.`;
        } else {
          errorMessage = "Something went wrong. Try later.";
        }

        setErrors({ general: errorMessage });
      } else {
        setErrors({ general: 'Editing failed.' });
      }

      errorRef?.current?.focus();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!name && selectedPerformers.length === 0 && !fileId && UploadingStates.WAITING_START === uploadingState && !cover) {
      toastError("At least one field must be filled in!");
      return;
    }

    if (fileId && UploadingStates.FINISHED === uploadingState) {
      setIsWaitingForResponse(true);
      await checkFileStatus(fileId);
    } else if (fileId && UploadingStates.IN_PROCESS === uploadingState) {
      toastError("File still uploading, please wait and then proceed!");
    }
    else if (UploadingStates.WAITING_START === uploadingState) {
      setIsWaitingForResponse(true);
      await updateSongDetails();
    }
  };

  if (isLoading) return <div>Loading...</div>;

  if (errorGettingSong) {
    let errorMessage;

    if (errorGettingSong.status) {
      const status = errorGettingSong?.data?.status;
      const message = errorGettingSong?.data?.message;

      if (errorGettingSong.data) {
        errorMessage = `${status ? status : ''}: ${message ? message : ''}.`;
      } else {
        errorMessage = "Something went wrong. Try later.";
      }
    } else {
      errorMessage = 'Editing failed';
    }

    return <Alert variant="danger">{errorMessage}</Alert>;
  }

  const performerOptions = performers?.map(p => ({ value: p.id, label: p.name })) || [];

  return (
    <Container>
      <h1>Edit Song</h1>
      <UploadFile songId={id}/>

      <Form onSubmit={handleSubmit}>
        <Form.Group controlId="name">
          <Form.Label>Title</Form.Label>
          <Form.Control
            type="text"
            ref={nameRef}
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </Form.Group>
        <Form.Group controlId="performerIds" className="mt-3">
          <Form.Label>Performers</Form.Label>
          <Select
            isMulti
            options={performerOptions}
            value={selectedPerformers}
            onChange={setSelectedPerformers}
            isLoading={isLoadingPerformers}
          />
        </Form.Group>
        <Form.Group controlId="cover" className="mt-3">
          <Form.Label>Cover</Form.Label>
          <Form.Control
            type="file"
            onChange={(e) => setCover(e.target.files[0])}
          />
        </Form.Group>

        <div className="d-flex align-items-center justify-content-center mt-3">
          {isWaitingForResponse ?
            <div className="spinner-border mt-3" role="status">
              <span className="sr-only"></span>
            </div>
            :
            <Button variant="primary" type="submit" className="mt-3 w-25">Update</Button>
          }
        </div>

        <div className="d-flex align-items-center justify-content-center">
          {errors && errors.hasOwnProperty('general') &&
            <Alert variant="danger" className="mt-3 mb-0">{errors.general}</Alert>}
        </div>
      </Form>
    </Container>
  );
}

export default EditSong;