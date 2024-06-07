import {useEffect, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import {useAddPerformerMutation} from "../../services/performer/performerApiSliceService";
import {Alert, Button, Container, Form} from "react-bootstrap";

const AddPerformer = () => {
    const [name, setName] = useState('');
    const [isUser, setIsUser] = useState(false);
    const [photo, setPhoto] = useState(null);
    const navigate = useNavigate();
    const [addPerformer] = useAddPerformerMutation();

    const nameRef = useRef();
    const errorRef = useRef();

    const [errors, setErrors] = useState({});

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
            await addPerformer(formData).unwrap();
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
                setErrors({ general: 'Adding failed' });
            }

            errorRef?.current?.focus();
        }
    };

    return (
        <Container>
            <h1>Add Performer</h1>
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
                        required
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

export default AddPerformer;