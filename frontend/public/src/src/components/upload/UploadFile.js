import {useEffect, useState} from "react";
import {Upload} from 'tus-js-client';
import {useDispatch, useSelector} from "react-redux";
import {selectCurrentToken} from "../../services/auth/authSliceService";
import {selectFileUploadState, setFileUploadState} from "../../services/upload/uploadSliceService";
import {Alert, Button, Form, ProgressBar} from "react-bootstrap";
import {useRequestUploadMutation} from "../../services/upload/uploadApiSliceService";
import {toastError, toastInfo, toastSuccess} from "../../helpers/toastNotification";
import {UploadingStates} from "../../pages/song/helpers";



const UploadFile = ({ songId }) => {
  const [file, setFile] = useState({});
  const [upload, setUpload] = useState(null);
  const [requestedFileId, setRequestedFileId] = useState("");
  const [uploadPercentage, setUploadPercentage] = useState(0);
  const [errorMessage, setErrorMessage] = useState('');
  const [isUploading, setIsUploading] = useState(false);

  const { fileId } = useSelector((state) => selectFileUploadState(state, songId));
  const token = useSelector(selectCurrentToken);

  const [requestUpload] = useRequestUploadMutation();

  const dispatch = useDispatch();

  useEffect(() => {
    if(!fileId) {
      try {
        requestUpload().unwrap().then((newFileId) => {
          dispatch(setFileUploadState({songId, fileId: newFileId, uploadingState: UploadingStates.WAITING_START }));
          setRequestedFileId(newFileId);
        });
      }
      catch (error) {
        toastError('Request for uploading failed, try later. ' + error.message);
      }
    }
    else {
      setRequestedFileId(fileId);
    }
  }, [dispatch, fileId, requestUpload, songId]);

  useEffect(() => {
    const upload = new Upload(file, {
      endpoint: `${process.env.REACT_APP_API_BASE_URL}/upload/file`,
      retryDelays: [0, 3000, 5000, 10000, 20000],
      chunkSize: 1000000,
      metadata: {
        filename: file.name,
        filetype: file.type,
        fileId: requestedFileId,
      },
      headers: {
        Authorization: `Bearer ${token}`
      },
      onError: (error) => {
        setErrorMessage('Upload failed: ' + error.message);
        setIsUploading(false);
      },
      onProgress: (bytesUploaded, bytesTotal) => {
        const percentage = ((bytesUploaded / bytesTotal) * 100).toFixed(2);
        setUploadPercentage(percentage);
      },
      onSuccess: () => {
        toastSuccess("The file was successfully uploaded.");
        setIsUploading(false);
        dispatch(setFileUploadState({songId, fileId: requestedFileId, uploadingState: UploadingStates.FINISHED }));
      },
    });

    setUpload(upload);
  }, [dispatch, file, requestedFileId, songId, token]);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if(selectedFile){
      setFile(selectedFile);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();

    if (!file) {
      toastError("Please select a file to upload.");
      return;
    }

    if (!requestedFileId) {
      toastError('Request for uploading failed, try later.');
      return;
    }

    dispatch(setFileUploadState({songId, fileId: requestedFileId, uploadingState: UploadingStates.IN_PROCESS }));
    setIsUploading(true);

    upload.findPreviousUploads().then((previousUploads) => {
      if (previousUploads.length) {
        toastInfo("Resuming previous upload.");
        upload.resumeFromPreviousUpload(previousUploads[0]);
      } else {
        toastInfo("Starting upload.");
      }
    });

    upload.start();
  };

  return (
    <Form onSubmit={handleUpload}>
      <Form.Group controlId="audioFile">
        <Form.Label>Audio File</Form.Label>

        <Form.Control type="file" onChange={handleFileChange}/>

        <section className="mt-3 mb-3">
          <p>Upload Progress: {uploadPercentage}%</p>
          <ProgressBar now={uploadPercentage} label={`${uploadPercentage}%`}/>
        </section>

        <div className="d-flex align-items-center justify-content-center">
          <Button variant="primary" type="submit" className="mt-3 w-25" disabled={isUploading}>Upload</Button>
        </div>

        <div className="d-flex align-items-center justify-content-center">
          {errorMessage && <Alert variant="danger" className="mt-3 mb-0">{errorMessage}</Alert>}
        </div>

      </Form.Group>
    </Form>
  );
}

export default UploadFile;