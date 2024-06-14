import {useEffect, useRef, useState} from "react";
import {useDispatch} from "react-redux";
import {Link, useNavigate} from "react-router-dom";
import {Alert, Form, Button} from "react-bootstrap";

import {useRegisterMutation} from "../../services/auth/authApiSliceService";
import {logOut, setCredentials} from "../../services/auth/authSliceService";

import AuthWrapper from "../../components/auth/AuthWrapper";
// import {useSocket} from "../../providers/socket/SocketProvider";
// import {toastSuccess} from "../../helpers/toastNotification";


const Register = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const usernameRef = useRef();
  const errorRef = useRef();

  //const { socketConnection } = useSocket();

  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    passwordConfirmation: '',
  });
  const [errors, setErrors] = useState({});


  const [register, { isLoading }] = useRegisterMutation();

  // useEffect(() => {
  //   const eventName = 'app/topic/avatar.ready';
  //   if (socketConnection.isConnected) {
  //     ((socketConnection) => {
  //       const subscription = socketConnection.subscribeToTopic(eventName, (data) => {
  //         console.log('Avatar is ready:', data);
  //         toastSuccess(`Avatar is ready! Message: ${data.message}`, 5000);
  //         socketConnection.unsubscribeFromTopic(subscription);
  //       });
  //     })(socketConnection);
  //   }
  // }, [socketConnection]);

  useEffect(() => {
    dispatch(logOut());
  }, [dispatch]);

  useEffect(() => {
    usernameRef?.current?.focus();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const form = e.currentTarget;

    if (form.checkValidity()){
      try {
        const result = await register(formData).unwrap();

        const accessToken = result.token;
        const user = result.user;

        dispatch(setCredentials({ user: user, accessToken: accessToken, rememberMe: true }));

        navigate('/profile');
      }
      catch (error) {
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
          setErrors({ general: 'Register failed' });
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

              <Form.Group className="mb-3" controlId="username">
                <Form.Label>Username</Form.Label>

                <Form.Control
                  type="text"
                  placeholder="john_doe123"
                  value={formData.username}
                  ref={usernameRef}
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

              <Form.Group className="mb-3" controlId="passwordСonfirmation">
                <Form.Label>Password Confirmation</Form.Label>
                <Form.Control
                  type="password"
                  placeholder="Enter password again"
                  value={formData.passwordConfirmation}
                  onChange={handleChange}
                  name="passwordConfirmation"
                  isInvalid={errors.hasOwnProperty('passwordConfirmation')}
                  onKeyDown={async (e) => {
                    if (e.key === 'Enter') {
                      await handleSubmit(e);
                    }
                  }}
                  required/>

                <Form.Control.Feedback type="invalid">
                  {errors && errors.hasOwnProperty('passwordConfirmation') && errors.passwordConfirmation[0]}
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

            {errors && errors.hasOwnProperty('general') && <Alert variant="danger" className="mt-3 mb-0">{errors.general}</Alert>}
          </div>
        )}
      </section>
    </AuthWrapper>
  );
};

export default Register;