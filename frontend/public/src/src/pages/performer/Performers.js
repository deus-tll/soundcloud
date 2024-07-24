import {useEffect, useState} from "react";
import {useDeletePerformerMutation, useGetPerformersQuery} from "../../services/performer/performerApiSliceService";
import {Alert, Button, Container, Pagination, Table} from "react-bootstrap";
import { LinkContainer } from 'react-router-bootstrap';
import {toast} from "react-toastify";
import DeleteModal from "../../components/reusable/DeleteModal";
import {useLocation} from "react-router-dom";

const Performers = () => {
    const [page, setPage] = useState(0);
    const { data, error: errorGettingPerformers, isLoading, refetch } = useGetPerformersQuery({ page });
    const [deletePerformer] = useDeletePerformerMutation();
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [selectedPerformer, setSelectedPerformer] = useState(null);
    const location = useLocation();

    const performers = data?.content || [];
    const totalPages = data?.totalPages || 1;

    useEffect(() => {
        if (location.state?.refetch) {
            refetch();
            window.history.replaceState({}, document.title)
        }
    }, [location.state, refetch]);

    const handleDelete = (performer) => {
        setSelectedPerformer(performer);
        setShowDeleteModal(true);
    };

    const confirmDelete = async () => {
        try {
            await deletePerformer(selectedPerformer.id);
            setShowDeleteModal(false);
            refetch();
        } catch (error) {
            let errorMessage;

            if(errorGettingPerformers.status) {
                let status = errorGettingPerformers?.data?.status;
                let message = errorGettingPerformers?.data?.message;

                if(errorGettingPerformers.data) {
                    errorMessage = `${status ? status : ''}: ${message ? message : ''}.`;
                }
                else {
                    errorMessage = "Something went wrong. Try later"
                }
            }
            else {
                errorMessage = 'Deleting performer failed';
            }

            toast.success(`Couldn't delete performer with id [${selectedPerformer.id}] and name [${selectedPerformer.name}]. ${errorMessage}`, {
                position: "top-right",
                autoClose: 5000
            });
        }
    };

    if (isLoading) return <div>Loading...</div>;
    if (errorGettingPerformers) {
        let errorMessage;

        if(errorGettingPerformers.status) {
            let status = errorGettingPerformers?.data?.status;
            let message = errorGettingPerformers?.data?.message;

            if(errorGettingPerformers.data) {
                errorMessage = `${status ? status : ''}: ${message ? message : ''}.`;
            }
            else {
                errorMessage = "Something went wrong. Try later"
            }
        }
        else {
            errorMessage = 'Getting page of performers failed';
        }

        return <Alert variant="danger">{errorMessage}</Alert>;
    }

    return (
        <Container>
            <h1 className="text-center">List Of All Performers</h1>
            <div className="d-flex align-items-center justify-content-center mt-5">
                <LinkContainer to="/performers/add">
                    <Button variant="primary" className="mb-3">Add Performer</Button>
                </LinkContainer>
            </div>

            <Table striped bordered hover>
                <thead>
                <tr>
                    <th>Photo</th>
                    <th>Name</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {performers.map(performer => (
                    <tr key={performer.id}>
                        <td><img src={performer.photoUrl} alt={performer.name} width="50"/></td>
                        <td>{performer.name}</td>
                        <td>
                            <LinkContainer to={`/performers/edit/${performer.id}`}>
                                <Button variant="warning" className="me-2">Update</Button>
                            </LinkContainer>
                            <Button variant="danger" onClick={() => handleDelete(performer)}>Delete</Button>
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

export default Performers;