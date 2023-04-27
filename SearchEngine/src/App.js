import React, { useState, useEffect } from "react";

import "./App.css";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import SearchPage from "./searchPage";
import ResultsPage from "./resultsPage";

function App() {
  return (
    <React.StrictMode>
      <Router>
        <Routes>
          <Route exact path="/" element={<SearchPage />}></Route>
          <Route exact path="/resultsPage" element={<ResultsPage />}></Route>
        </Routes>
      </Router>
    </React.StrictMode>
  );
}

export default App;
