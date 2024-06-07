import {useNavigate, useParams} from "react-router-dom";
import {useGetPerformerByIdQuery, useUpdatePerformerMutation} from "../../services/performer/performerApiSliceService";
import {useEffect, useRef, useState} from "react";
import {Alert, Button, Container, Form} from "react-bootstrap";

const EditPerformer = () => {
    const { id } = useParams();
    const { data: performer, error: errorGettingPerformer, isLoading } = useGetPerformerByIdQuery(id);
    const [name, setName] = useState('');
    const [isUser, setIsUser] = useState(false);
    const [photo, setPhoto] = useState(null);
    const navigate = useNavigate();
    const [updatePerformer] = useUpdatePerformerMutation();
    const nameRef = useRef();
    const errorRef = useRef();

    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (performer) {
            setName(performer.name);
            setIsUser(performer.isUser);
        }
    }, [performer]);

    useEffect(() => {
        nameRef?.current?.focus();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('name', name);
        formData.append('isUser', isUser);
        formData.append('photo', photo);

        try {
            await updatePerformer({ id, data: formData }).unwrap();
            navigate('/performers');
        } catch (error) {
            if(error.status) {
                let status = error?.data?.status;
                let message = error?.data?.message;
                let errorMessage;

                if(error.data) {
                    errorMessage = `${status ? status : ''}: ${message ? message : ''}.`;
                }
                else {
                    errorMessage = "Something went wrong. Try later"
                }

                setErrors({ general: errorMessage });
            }
            else {
                setErrors({ general: 'Editing Failed' });
            }

            errorRef?.current?.focus();
        }
    };

    if (isLoading) return <div>Loading...</div>;

    if (errorGettingPerformer) {
        let errorMessage;

        if(errorGettingPerformer.status) {
            let status = errorGettingPerformer?.data?.status;
            let message = errorGettingPerformer?.data?.message;

            if(errorGettingPerformer.data) {
                errorMessage = `${status ? status : ''}: ${message ? message : ''}.`;
            }
            else {
                errorMessage = "Something went wrong. Try later"
            }
        }
        else {
            errorMessage = 'Editing failed';
        }

        return <Alert variant="danger">{errorMessage}</Alert>;
    }

    return (
        <Container>
            <h1>Edit Performer</h1>
            <Form onSubmit={handleSubmit}>
                <Form.Group controlId="name">
                    <Form.Label>Name</Form.Label>
                    <Form.Control
                        type="text"
                        ref={nameRef}
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </Form.Group>
                <Form.Group controlId="isUser" className="mt-3">
                    <Form.Check
                        type="checkbox"
                        label="Is User"
                        checked={isUser}
                        onChange={(e) => setIsUser(e.target.checked)}
                    />
                </Form.Group>
                <Form.Group controlId="photo" className="mt-3">
                    <Form.Label>Photo</Form.Label>
                    <Form.Control
                        type="file"
                        onChange={(e) => setPhoto(e.target.files[0])}
                    />
                </Form.Group>

                <div className="d-flex align-items-center justify-content-center">
                    <Button variant="primary" type="submit" className="mt-3 w-25">Add</Button>
                </div>
            </Form>

            {errors && errors.hasOwnProperty('general') && <Alert variant="danger" className="mt-3 mb-0">{errors.general}</Alert>}
        </Container>
    );
}

export default EditPerformer;