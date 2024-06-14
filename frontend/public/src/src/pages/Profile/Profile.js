import {useDispatch, useSelector} from "react-redux";
import {logOut, selectCurrentToken, selectCurrentUser} from "../../services/auth/authSliceService";
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {Button} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";

const Profile = () => {
  const [userData, setUserData] = useState({});

  const user = useSelector(selectCurrentUser);
  const token = useSelector(selectCurrentToken);

  const dispatch = useDispatch();

  useEffect(() => {
    if (user) {
      setUserData(user);
    } else {
      setUserData({});
    }
  }, [user]);

  return (
    <section>
      {user && token ? (
        <>
          <h1>Welcome {userData.username}!</h1>
          <p>Email: {userData.email}</p>
          <p>Role: {userData.role}</p>
          <p>Avatar: <img src={userData.avatarUrl} alt="avatar"/></p>

          <div className="d-flex align-items-center justify-content-center mt-3">
            <Button onClick={() => dispatch(logOut())} variant="danger" type="submit" className="mt-3 w-25">Logout</Button>
          </div>
        </>
      ) : (
        <>
          <h1 className="text-center mt-5">You are not logged in.</h1>
          <div className="d-flex align-items-center justify-content-center mt-5">
            <LinkContainer to="/register">
              <Button variant="primary" className="mb-3">Register</Button>
            </LinkContainer>

            <p className="ms-3 me-3">OR</p>

            <LinkContainer to="/login">
              <Button variant="primary" className="mb-3">Login</Button>
            </LinkContainer>
          </div>
        </>
      )}
    </section>
  );
};

export default Profile;