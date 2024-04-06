import {useDispatch} from "react-redux";
import {setCredentials} from "../../services/auth/authSliceService";


const AuthProvider = ({ children }) => {
  const dispatch = useDispatch();

  const accessToken = localStorage.getItem('accessToken');
  const storedUser = localStorage.getItem('user');

  if (storedUser && accessToken) {
    dispatch(setCredentials({ user: JSON.parse(storedUser), accessToken, rememberMe: true }));
  }

  return (
    <>
      {children}
    </>
  );
};

export default AuthProvider;