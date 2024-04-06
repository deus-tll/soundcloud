import {Route, Routes} from "react-router-dom";
import Login from "../../pages/auth/Login";
import Register from "../../pages/auth/Register";
import RootLayout from "./RootLayout";
import Home from "../../pages/Home";
import RequireAuth from "./RequireAuth";
import Welcome from "../../pages/Welcome";
import VerifyEmail from "../../pages/auth/VerifyEmail";

const MainRouter = () => {
  return (
    <Routes>
      {/* public routes to individual pages */}
      <Route path="login" element={<Login/>}/>
      <Route path="register" element={<Register/>}/>

      {/* protected routes to individual pages */}
      <Route element={<RequireAuth/>}>
        <Route path="verify-email" element={<VerifyEmail/>}/>
      </Route>

      <Route path="/" element={<RootLayout/>}>
        {/* public routes inside RootLayout */}
        <Route index element={<Home/>}/>

        {/* protected routes inside RootLayout */}
        <Route element={<RequireAuth/>}>
          <Route path="welcome" element={<Welcome/>}/>
        </Route>
      </Route>
    </Routes>
  );
};

export default MainRouter;