import {Container, Nav, Navbar, NavLink} from "react-bootstrap";

const Home = () => {
    return (
        <section>
            <Navbar bg="light" expand="lg">
                <Container>
                    <Navbar.Brand href="/">Soundcloud</Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav" />
                    <Navbar.Collapse id="basic-navbar-nav">
                        <Nav className="me-auto">
                            <NavLink to="/performers">Performers</NavLink>
                            <NavLink to="/songs">Songs</NavLink>
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>

            <section>
                <h1>HOME</h1>
            </section>
        </section>
    );
}
export default Home