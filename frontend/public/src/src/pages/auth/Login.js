import {useEffect, useRef, useState} from 'react';
import {useDispatch} from 'react-redux';
import {Link, useNavigate} from 'react-router-dom';
import {Alert, Button, Form} from 'react-bootstrap';

import {useLoginMutation} from '../../services/auth/authApiSliceService';
import {setCredentials} from '../../services/auth/authSliceService';

import AuthWrapper from '../../components/auth/AuthWrapper';


const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const emailRef = useRef();
  const errorRef = useRef();

  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [rememberMe, setRememberMe] = useState(false);
  const [errors, setErrors] = useState({});

  const [login, { isLoading }] = useLoginMutation();

  useEffect(() => {
    emailRef?.current?.focus();
  }, []);

  useEffect(() => {
    setErrors({});
  }, [formData.email, formData.password]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const form = e.currentTarget;

    if (form.checkValidity()) {
      try {
        const result = await login(formData).unwrap();
        const access_token = result.data.authorization.access_token;
        const user = result.data.user;

        user.avatars = result.data.avatars;

        dispatch(setCredentials({ user, accessToken: access_token, rememberMe }));

        navigate('/welcome');
      }
      catch (errorData) {
        if (errorData.originalStatus) {
          setErrors({general: errorData.error});
        } else if(errorData.status === 422) {
          setErrors(errorData.data.errors);
        } else if (errorData.status === 401 || errorData.status === 500) {
          let status = errorData?.data?.data?.status;
          let message = errorData?.data?.data?.message;
          let error = errorData?.data?.data?.error;

          let errorMessage = `${status ? status : ''}: ${message ? message : ''}. ${error ? error : ''}`;

          setErrors({ general: errorMessage });
        } else {
          setErrors({ general: 'Login Failed' });
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
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
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
              <h3>Sign In</h3>

              <Form.Group className="mb-3" controlId="email">
                <Form.Label>Email address</Form.Label>

                <Form.Control
                  type="email"
                  placeholder="Enter email"
                  ref={emailRef}
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

              <div className="mb-3">
                <div className="custom-control custom-checkbox">
                  <label className="custom-control-label" htmlFor="customCheck1">
                    <input
                      type="checkbox"
                      className="custom-control-input"
                      id="customCheck1"
                      checked={rememberMe}
                      onChange={(e) => setRememberMe(e.target.checked)}/>
                    <span className="ps-1">Remember me</span>
                  </label>
                </div>
              </div>

              <div className="d-grid">
                <Button type="submit" variant="primary">
                Submit
                </Button>
              </div>

              <div className="d-flex align-items-center justify-content-between">
                <p className="forgot-password text-right">
                  Forgot <Link to="/reset-password">password?</Link>
                </p>

                <p className="forgot-password text-right">
                  Don't have an account yet <Link to="/register">sign up?</Link>
                </p>
              </div>
            </Form>

            {errors && errors.hasOwnProperty('general') && <Alert variant="danger">{errors.general}</Alert>}
          </div>
        )}
      </section>
    </AuthWrapper>
  );
};

export default Login;