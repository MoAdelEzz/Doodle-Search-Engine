import React from "react";
import ReactDOM from "react-dom/client";
import { useState } from "react";
import Axios from "axios";
import { Link, Navigate, useNavigate } from "react-router-dom";

function SearchPage() {
  const [query, setQuery] = useState();
  const navigate = useNavigate();

  return (
    <div>
      <input
        type="text"
        onChange={(e) => {
          setQuery(e.target.value);
        }}
      ></input>
      <button
        onClick={async () => {
          await fetch("http://localhost:8080/query", {
            method: "POST",
            headers: {
              Accept: "application/json",
              "Content-Type": "application/json",
              withCredentials: true,
              crossorigin: true,
              mode: "no-cors",
            },
            body: query,
          });
          setTimeout(() => {
            navigate("/resultsPage");
          }, 1000);
        }}
      >
        search
      </button>
    </div>
  );
}

export default SearchPage;
