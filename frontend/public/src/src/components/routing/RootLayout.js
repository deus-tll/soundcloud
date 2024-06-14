import {Outlet} from "react-router-dom";
import {Container, Nav, Navbar, NavLink} from "react-bootstrap";
import {ToastContainer} from "react-toastify";
import React from "react";

const RootLayout = () => {
  const appName = process.env.REACT_APP_NAME;

  return (
    <div className="container mt-4">
      <header className="mb-2">
        <Navbar bg="light" expand="lg">
          <Container>
            <Navbar.Brand href="/">Soundcloud</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
              <Nav className="me-auto">
                <NavLink href="/performers">Performers</NavLink>
                <NavLink href="/songs">Songs</NavLink>
                <NavLink href="/profile">Profile</NavLink>
              </Nav>
            </Navbar.Collapse>
          </Container>
        </Navbar>
      </header>

      <main style={{minHeight: 'calc(100vh - 90px)'}}>
        <ToastContainer />
        <Outlet/>
      </main>

      <footer className="mb-5">
        <p className="text-small">&copy; {new Date().getFullYear()} {appName}</p>
      </footer>
    </div>
  );
};

export default RootLayout;