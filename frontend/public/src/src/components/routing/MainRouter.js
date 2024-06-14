import {Route, Routes} from "react-router-dom";
import Login from "../../pages/auth/Login";
import Register from "../../pages/auth/Register";
import RootLayout from "./RootLayout";
import Home from "../../pages/Home";
import RequireAuth from "./RequireAuth";
import AddPerformer from "../../pages/performer/AddPerformer";
import EditPerformer from "../../pages/performer/EditPerformer";
import Performers from "../../pages/performer/Performers";
import Songs from "../../pages/song/Songs";
import AddSong from "../../pages/song/AddSong";
import EditSong from "../../pages/song/EditSong";
import Profile from "../../pages/Profile/Profile";

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
        <Route path="profile" element={<Profile/>}/>

        {/* protected routes inside RootLayout */}
        <Route element={<RequireAuth/>}>
          <Route path="performers" element={<Performers/>}/>
          <Route path="performers/add" element={<AddPerformer/>}/>
          <Route path="performers/edit/:id" element={<EditPerformer/>}/>

          <Route path="songs" element={<Songs />} />
          <Route path="songs/add" element={<AddSong />} />
          <Route path="songs/edit/:id" element={<EditSong />} />
        </Route>
      </Route>
    </Routes>
  );
};

export default MainRouter;