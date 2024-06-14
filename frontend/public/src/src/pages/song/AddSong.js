import {useEffect, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import {useAddSongMutation} from "../../services/song/songApiSliceService";
import {useGetAllPerformersQuery} from "../../services/performer/performerApiSliceService";
import {Alert, Button, Container, Form} from "react-bootstrap";
import Select from 'react-select';
import UploadFile from "../../components/upload/UploadFile";
import {useDispatch, useSelector} from "react-redux";
import {
  clearFileUploadState,
  selectFileUploadState
} from "../../services/upload/uploadSliceService";
import {useCheckFileQuery} from "../../services/upload/uploadApiSliceService";
import {toastInfo, toastSuccess} from "../../helpers/toastNotification";
import {UploadingStates} from "./helpers";

const AddSong = () => {
  const songId = 'new-song';
  const [name, setName] = useState('');
  const [performerIds, setPerformerIds] = useState([]);
  const [cover, setCover] = useState(null);
  const [isWaitingForResponse, setIsWaitingForResponse] = useState(false);
  const [errors, setErrors] = useState({});

  const { fileId, uploadingState } = useSelector((state) => selectFileUploadState(state, songId));

  const [addSong] = useAddSongMutation();
  const { refetch: checkFileRefetch } = useCheckFileQuery();
  const { data: performers, isLoading: isLoadingPerformers } = useGetAllPerformersQuery();

  const dispatch = useDispatch();

  const navigate = useNavigate();

  const nameRef = useRef();
  const errorRef = useRef();

  useEffect(() => {
    nameRef?.current?.focus();

    const checkUploadStatus = () => {
      if (!fileId) return;

      if (UploadingStates.FINISHED === uploadingState) {
        toastInfo("Upload of your file already finished. You can fill in other fields and add song, or you can choose another file to upload.", 10000);
      }
      else if (UploadingStates.IN_PROCESS === uploadingState) {
        toastInfo("Upload of your file was interrupted. You can choose same file as before to resume upload, or you can choose another file to upload.", 10000);
      }
    }

    checkUploadStatus();
  }, [dispatch]);

  const checkFileStatus = async (fileId) => {
    const interval = setInterval(async () => {
      try {
        const result = await checkFileRefetch({ fileId });
        if (result.data) {
          clearInterval(interval);
          await createSong();
        }
      } catch (error) {
        console.error('Failed to check file status: ', error);
      }
    }, 3000);
  };

  const createSong = async () => {
    const formData = new FormData();
    formData.append('name', name);
    formData.append('performerIds', performerIds.map(p => p.value));
    formData.append('fileId', fileId);
    formData.append('cover', cover);

    try {
      const result = await addSong(formData).unwrap();

      console.log(result);

      dispatch(clearFileUploadState({ songId }));
      toastSuccess("Song was successfully created!", 10000);
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
        setErrors({ general: 'Adding failed.' });
      }

      errorRef?.current?.focus();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (fileId && UploadingStates.FINISHED === uploadingState) {
      setIsWaitingForResponse(true);
      await checkFileStatus(fileId);
    } else {
      toastInfo("You need to upload audio file first!", 10000);
    }
  };

  const performerOptions = performers?.map(p => ({ value: p.id, label: p.name })) || [];

  return (
    <Container>
      <h1>Add Song</h1>
      <UploadFile songId={songId}/>

      <Form onSubmit={handleSubmit}>
        <Form.Group controlId="name">
          <Form.Label>Title</Form.Label>
          <Form.Control
            type="text"
            ref={nameRef}
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </Form.Group>
        <Form.Group controlId="performerIds" className="mt-3">
          <Form.Label>Performers</Form.Label>
          <Select
            isMulti
            options={performerOptions}
            value={performerIds}
            onChange={setPerformerIds}
            isLoading={isLoadingPerformers}
          />
        </Form.Group>
        <Form.Group controlId="cover" className="mt-3">
          <Form.Label>Cover</Form.Label>
          <Form.Control
            type="file"
            onChange={(e) => setCover(e.target.files[0])}
            required
          />
        </Form.Group>

        <div className="d-flex align-items-center justify-content-center mt-3">
          {isWaitingForResponse ?
            <div className="spinner-border mt-3" role="status">
              <span className="sr-only"></span>
            </div>
            :
            <Button variant="primary" type="submit" className="mt-3 w-25">Add</Button>
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

export default AddSong;