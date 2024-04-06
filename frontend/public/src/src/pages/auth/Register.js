import {useEffect, useRef, useState} from "react";
import {useDispatch} from "react-redux";
import {Link, useNavigate} from "react-router-dom";
import {Alert, Form, Button} from "react-bootstrap";

import {useRegisterMutation} from "../../services/auth/authApiSliceService";
import {setCredentials} from "../../services/auth/authSliceService";

import {handleAvatarsStored, setAvatarsStoredCallback} from "../../providers/socket/socketHandlers";
import {useSocket} from "../../providers/socket/SocketProvider";

import AuthWrapper from "../../components/auth/AuthWrapper";


const Register = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const firstNameRef = useRef();
  const errorRef = useRef();

  const { socketConnection } = useSocket();
  const [formData, setFormData] = useState({
    first_name: '',
    last_name: '',
    email: '',
    username: '',
    password: '',
    password_confirmation: '',
  });
  const [errors, setErrors] = useState({});


  const [register, { isLoading }] = useRegisterMutation();

  useEffect(() => {
    const eventName = 'avatars.stored';
    const callBack = (data) => handleAvatarsStored(socketConnection, dispatch, data, eventName);
    setAvatarsStoredCallback(callBack);
    socketConnection.on(eventName, callBack);

  }, [socketConnection, dispatch]);

  useEffect(() => {
    firstNameRef?.current?.focus();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const form = e.currentTarget;

    if (form.checkValidity()){
      try {
        const result = await register(formData).unwrap();

        const access_token = result.data.authorization.access_token;
        const user = result.data.user;

        dispatch(setCredentials({ user: user, accessToken: access_token, rememberMe: true }));

        navigate('/welcome');
      }
      catch (errorData) {
        if (errorData.originalStatus) {
          setErrors({general: errorData.error});
        } else if(errorData.status === 422) {
          setErrors(errorData.data.errors);
        } else if (errorData.status === 400 || errorData.status === 500) {
          let status = errorData?.data?.data?.status;
          let message = errorData?.data?.data?.message;
          let error = errorData?.data?.data?.error;

          let errorMessage = `${status ? status : ''}: ${message ? message : ''}. ${error ? error : ''}`;

          setErrors({ general: errorMessage });
        } else {
          setErrors({ general: 'Register Failed' });
        }

        errorRef?.current?.focus();
      }
    }
    else {
      e.stopPropagation();
      setErrors({ general: 'Please fill out all required fields correctly.' });
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  return (
    <AuthWrapper>
      <section>
        {isLoading ? (
          <h1 className="text-center">Loading...</h1>
        ) : (
          <div>
            <Form onSubmit={handleSubmit}>
              <h3>Sign Up</h3>

              <Form.Group className="mb-3" controlId="first_name">
                <Form.Label>First name</Form.Label>

                <Form.Control
                  type="text"
                  placeholder="John"
                  ref={firstNameRef}
                  value={formData.first_name}
                  onChange={handleChange}
                  name="first_name"
                  isInvalid={errors.hasOwnProperty('first_name')}
                  onKeyDown={async (e) => {
                    if (e.key === 'Enter') {
                      await handleSubmit(e);
                    }
                  }}
                  required/>

                <Form.Control.Feedback type="invalid">
                  {errors && errors.hasOwnProperty('first_name') && errors.first_name[0]}
                </Form.Control.Feedback>
              </Form.Group>

              <Form.Group className="mb-3" controlId="last_name">
                <Form.Label>Last name</Form.Label>

                <Form.Control
                  type="text"
                  placeholder="Doe"
                  value={formData.last_name}
                  onChange={handleChange}
                  name="last_name"
                  isInvalid={errors.hasOwnProperty('last_name')}
                  onKeyDown={async (e) => {
                    if (e.key === 'Enter') {
                      await handleSubmit(e);
                    }
                  }}
                  required/>

                <Form.Control.Feedback type="invalid">
                  {errors && errors.hasOwnProperty('last_name') && errors.last_name[0]}
                </Form.Control.Feedback>
              </Form.Group>

              <Form.Group className="mb-3" controlId="username">
                <Form.Label>Username</Form.Label>

                <Form.Control
                  type="text"
                  placeholder="john_doe123"
                  value={formData.username}
                  onChange={handleChange}
                  name="username"
                  isInvalid={errors.hasOwnProperty('username')}
                  onKeyDown={async (e) => {
                    if (e.key === 'Enter') {
                      await handleSubmit(e);
                    }
                  }}
                  required/>

                <Form.Control.Feedback type="invalid">
                  {errors && errors.hasOwnProperty('username') && errors.username[0]}
                </Form.Control.Feedback>
              </Form.Group>

              <Form.Group className="mb-3" controlId="email">
                <Form.Label>Email address</Form.Label>

                <Form.Control
                  type="email"
                  placeholder="johndoe@gmail.com"
                  value={formData.email}
                  onChange={handleChange}
                  name="email"
                  isInvalid={errors.hasOwnProperty('email')}
                  onKeyDown={async (e) => {
                    if (e.key === 'Enter') {
                      await handleSubmit(e);
                    }
                  }}
                  required/>

                <Form.Control.Feedback type="invalid">
                  {errors && errors.hasOwnProperty('email') && errors.email[0]}
                </Form.Control.Feedback>
              </Form.Group>

              <Form.Group className="mb-3" controlId="password">
                <Form.Label>Password</Form.Label>
                <Form.Control
                  type="password"
                  placeholder="Enter password"
                  value={formData.password}
                  onChange={handleChange}
                  name="password"
                  isInvalid={errors.hasOwnProperty('password')}
                  onKeyDown={async (e) => {
                    if (e.key === 'Enter') {
                      await handleSubmit(e);
                    }
                  }}
                  required/>

                <Form.Control.Feedback type="invalid">
                  {errors && errors.hasOwnProperty('password') && errors.password[0]}
                </Form.Control.Feedback>
              </Form.Group>

              <Form.Group className="mb-3" controlId="password_confirmation">
                <Form.Label>Password Confirmation</Form.Label>
                <Form.Control
                  type="password"
                  placeholder="Enter password again"
                  value={formData.password_confirmation}
                  onChange={handleChange}
                  name="password_confirmation"
                  isInvalid={errors.hasOwnProperty('password_confirmation')}
                  onKeyDown={async (e) => {
                    if (e.key === 'Enter') {
                      await handleSubmit(e);
                    }
                  }}
                  required/>

                <Form.Control.Feedback type="invalid">
                  {errors && errors.hasOwnProperty('password_confirmation') && errors.password_confirmation[0]}
                </Form.Control.Feedback>
              </Form.Group>

              <div className="d-grid">
                <Button variant="primary" type="submit">
                  Sign Up
                </Button>
              </div>

              <p className="forgot-password text-right">
                Already registered <Link to="/login">sign in?</Link>
              </p>
            </Form>

            {errors && errors.hasOwnProperty('general') && (
              <Alert variant="danger">{errors.general}</Alert>
            )}
          </div>
        )}
      </section>
    </AuthWrapper>
  );
};

export default Register;