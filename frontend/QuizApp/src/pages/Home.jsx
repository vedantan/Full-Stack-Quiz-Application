import React from "react";
import { useNavigate } from "react-router-dom";

function Home() {

  const navigate = useNavigate();

  return (

    <div style={{ textAlign: "center", marginTop: "80px" }}>

      <h1>Welcome to Quiz Application</h1>

      <p>Create Questions and Play Quiz</p>

      <div style={{ marginTop: "30px" }}>

        <button
          style={btn}
          onClick={() => navigate("/questions")}
        >
          View Questions
        </button>

        <button
          style={btn}
          onClick={() => navigate("/add-question")}
        >
          Add Question
        </button>

      </div>

    </div>
  );
}

const btn = {
  padding: "15px 30px",
  margin: "10px",
  fontSize: "16px",
  cursor: "pointer"
};

export default Home;