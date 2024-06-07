import {Button, Modal} from "react-bootstrap";

const DeleteModal = ({ whatToDelete, show, onHide, onConfirm }) => {
    return (
        <Modal show={show} onHide={onHide}>
            <Modal.Header closeButton>
                <Modal.Title>Confirm Delete</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                Are you sure you want to delete this {whatToDelete}?
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onHide}>Cancel</Button>
                <Button variant="danger" onClick={onConfirm}>Delete</Button>
            </Modal.Footer>
        </Modal>
    );
};

export default DeleteModal;