import {Route, Routes} from "react-router-dom";
import Login from "../../pages/auth/Login";
import Register from "../../pages/auth/Register";
import RootLayout from "./RootLayout";
import Home from "../../pages/Home";
import RequireAuth from "./RequireAuth";
import Welcome from "../../pages/Welcome";
import AddPerformer from "../../pages/performer/AddPerformer";
import EditPerformer from "../../pages/performer/EditPerformer";
import Performers from "../../pages/performer/Performers";

const MainRouter = () => {
  return (
    <Routes>
      {/* public routes to individual pages */}
      <Route path="login" element={<Login/>}/>
      <Route path="register" element={<Register/>}/>

      {/* protected routes to individual pages */}

      {/**/}


      <Route path="/" element={<RootLayout/>}>
        {/* public routes inside RootLayout */}
        <Route index element={<Home/>}/>

        {/* protected routes inside RootLayout */}
        <Route element={<RequireAuth/>}>
          <Route path="welcome" element={<Welcome/>}/>

          <Route path="performers" element={<Performers/>}/>
          <Route path="performers/add" element={<AddPerformer/>}/>
          <Route path="performers/edit/:id" element={<EditPerformer/>}/>
        </Route>
      </Route>
    </Routes>
  );
};

export default MainRouter;