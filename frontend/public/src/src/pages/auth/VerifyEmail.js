import {useEffect, useState} from "react";
import {useDispatch} from "react-redux";
import {useNavigate} from "react-router-dom";
import {Alert} from "react-bootstrap";

import {useVerifyEmailMutation} from "../../services/auth/authApiSliceService";
import {updateUserFields} from "../../services/auth/authSliceService";


const VerifyEmail = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [verifyEmail, { isLoading }] = useVerifyEmailMutation();
  const [errors, setErrors] = useState({});
  const [countdown, setCountdown] = useState(5);
  const [verificationSuccess, setVerificationSuccess] = useState(false);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");
    const email = params.get("email");

    const handleVerifyEmail = async (token, email) => {
      try {
        const result = await verifyEmail({ token, email }).unwrap();

        const email_verified_at = result.data.email_verified_at;
        const updated_at = result.data.updated_at;

        const fieldsToUpdate = {
          email_verified_at,
          updated_at
        };

        dispatch(updateUserFields({ fieldsToUpdate }));

        setVerificationSuccess(true);
      } catch (errorData) {
        if (errorData.originalStatus) {
          setErrors({ general: errorData.error });
        } else if (errorData.status === 422) {
          setErrors(errorData.data.errors);
        } else if (
          errorData.status === 404 ||
          errorData.status === 500 ||
          errorData.status === 417
        ) {
          let status = errorData?.data?.data?.status;
          let message = errorData?.data?.data?.message;
          let error = errorData?.data?.data?.error;

          let errorMessage = `${status ? status : ""}: ${
            message ? message : ""
          }. ${error ? error : ""}`;

          setErrors({ general: errorMessage });
        } else {
          setErrors({ general: "Email Verification Failed" });
        }
      }
    };

    if (token && email) {
      (async () => {
        await handleVerifyEmail(token, email);
        console.log("Email Verified!");
      })();
    }
  }, [dispatch, verifyEmail]);

  useEffect(() => {
    let timer;

    const startTimer = () => {
      timer = setInterval(() => {
        setCountdown((prevCountdown) => prevCountdown - 1);
      }, 1000);
    };

    const delayTimerStart = () => {
      setTimeout(startTimer, 1500);
    };

    if (verificationSuccess) {
      delayTimerStart();
    }

    return () => clearInterval(timer);
  }, [verificationSuccess]);

  useEffect(() => {
    if(verificationSuccess && countdown === 0){
      navigate('/');
    }
  }, [verificationSuccess, countdown, navigate]);

  return (
    <section>
      {isLoading ? (
        <h1 className="text-center">Loading...</h1>
      ) : (
        <div>
          {verificationSuccess ? (
            <>
              <Alert variant="success">Email successfully verified!</Alert>
              <p>Redirecting in {countdown} seconds...</p>
            </>
          ) : (
            <>
              {errors && errors.hasOwnProperty('general') && (
                <Alert variant="danger">{errors.general}</Alert>
              )}
            </>
          )}
        </div>
      )}
    </section>
  );
};

export default VerifyEmail;