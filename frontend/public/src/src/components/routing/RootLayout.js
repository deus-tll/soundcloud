import {Outlet} from "react-router-dom";

const RootLayout = () => {
  const appName = process.env.REACT_APP_NAME;

  return (
    <div className="container mt-4">
      <main style={{minHeight: 'calc(100vh - 90px)'}}>
        <Outlet/>
      </main>

      <footer className="mb-5">
        <p className="text-small">&copy; {new Date().getFullYear()} {appName}</p>
      </footer>
    </div>
  );
};

export default RootLayout;