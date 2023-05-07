import React, { useEffect } from "react";
import ReactDOM from "react-dom/client";
import { useState } from "react";
import axios from "axios";
import { Navigate } from "react-router-dom";

function ResultsPage() {
  const [results, setResults] = useState([]);
  
  axios.get("http://localhost:8080/").then((response) => {
    //console.log(response.data);
    setResults(response.data);
  });

  //console.log(results);
  const arrayDataItems = results.map((item) => <li> <a href={item}>{item}</a></li>);
  return <ul>{arrayDataItems}</ul>;
}

export default ResultsPage;
