import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import Question from "./pages/Question";
import AddQuestion from "./pages/AddQuestion";
import Profile from "./pages/Profile";
import LeaderBoard from "./pages/LeaderBoard";
import Quiz from "./pages/Quiz";
import Login from "./pages/Login";
import PrivateRoute from "./pages/PrivateRoute";
import Signup from "./pages/Signup";


function App() {

  return (
    <>
    
    
      <BrowserRouter>

        <Navbar />

        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/questions" element={<Question />} />
          <Route path="/add-question" element={<AddQuestion />} />
          <Route path="/quiz" element={ <PrivateRoute> <Quiz/> </PrivateRoute>} />
          <Route path="/profile" element={ <PrivateRoute> <Profile/> </PrivateRoute>}/>
          <Route path="/leaderboard" element={<PrivateRoute> <LeaderBoard/> </PrivateRoute>}/>
          <Route path="/login" element={<Login/>}></Route>
          <Route path="/signup" element={<Signup/>}></Route>
        </Routes>

      </BrowserRouter>

      <ToastContainer
        position="bottom-right"
        autoClose={2500}
        //hideProgressBar={false}
        newestOnTop
        closeOnClick
        pauseOnHover
        theme="dark"
        
      />

    </>

  );
}

export default App;