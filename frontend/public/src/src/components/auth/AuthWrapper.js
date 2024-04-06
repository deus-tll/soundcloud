import "./styles/auth.css"

const AuthWrapper = (props) => {
  return (
    <div className="auth-wrapper">
      <div className="auth-inner">
        {props.children}
      </div>
    </div>
  );
};

export default AuthWrapper;