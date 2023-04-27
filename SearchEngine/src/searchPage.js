import React from "react";
import ReactDOM from "react-dom/client";
import { useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
function urlExists(url) {
  return fetch(url, { method: 'GET' })
    .then(response => {
      if (response.ok) {
        return true;
      } else {
        return false;
      }
    })
    .catch(error => {
      return false;
    });
}


function SearchPage() {
  let query = "";
  const [theme, setTheme] = useState("light");
  const navigate = useNavigate();

  const sun = "https://www.uplooder.net/img/image/55/7aa9993fc291bc170abea048589896cf/sun.svg";
  const moon = "https://www.uplooder.net/img/image/2/addf703a24a12d030968858e0879b11e/moon.svg";

  function process_query()
{
  let q = query;
  /*
  if (q == "") return;
  if (q.indexOf(' ') == -1 && q.search("/[.com|http|https|.net]/i" != -1))
  { 
      if (q.search('/[http|https]') == -1)
      {
          let x = "https://";
          q = x + q
      }

      
      if (urlExists(q))
      {
          window.open(q,"_blank");
      }
  }
*/
     fetch("http://localhost:8080/query", {
      method: "POST",
      headers: {
        "Accept": "text/plain;charset=UTF-8",
        "Content-Type": "text/plain;charset=UTF-8",
        "withCredentials": true,
        "crossorigin": true,
        "mode": "no-cors",
      },
      body: q,
    })
    .then((R)=>{
      navigate("resultsPage")})
    .catch((error)=>{console.log(error);});
  /*
  setTimeout(() => {
    navigate("/resultsPage");
  }, 1000);
  }
  */
}
  function handleEnter(key)
{
    console.log(key.keyCode);
    if (key.keyCode == "13")
    {
      process_query();
    }
}

  function setPageTheme(container) {
    let themeIcon = document.getElementById("theme-icon");
    switch (theme) {
      case "dark":
        setLight(container.target,themeIcon);
        setTheme("light");
        break;
      case "light":
        setDark(container.target,themeIcon);
        setTheme("dark");
        break;
    }
  }
  function setLight(container,themeIcon) {
    root.style.setProperty(
      "--bs-dark",
      "linear-gradient(318.32deg, #c3d1e4 0%, #dde7f3 55%, #d4e0ed 100%)"
    );
    container.classList.remove("shadow-dark");
    container.classList.add("shadow-light");
    setTimeout(() => {
      themeIcon.classList.remove("change");
    }, 300);
    themeIcon.classList.add("change");
    themeIcon.src = sun;
  }
  function setDark(container,themeIcon) {
    root.style.setProperty("--bs-dark", "#212529");
    container.classList.remove("shadow-light");
    container.classList.add("shadow-dark");
    setTimeout(() => {
      themeIcon.classList.remove("change");
    }, 100);
    themeIcon.classList.add("change");
    themeIcon.src = moon;
  }

  return (

    <div className = { "cover " + theme}>

    <div class="theme-container shadow-light" onClick={(e)=>setPageTheme(e)}>
      <img id="theme-icon" key={"theme-icon"} src={sun} alt={"ERR"} />
    </div>

    <div class="container-fluid centered">
      <div class="row">
        
      { 
        theme == "light" ?
        
        <h1 class="SEName-light col-12">
          <span class="GBlue">D</span>
          <span class="GRed">o</span>
          <span class="GYellow">o</span>
          <span class="GBlue">d</span>
          <span class="GGreen">l</span>
          <span class="GRed">e</span>
        </h1>
        :
        <h1 class="SEName-dark col-12">
          D
          o
          o
          d
          l
          e        
        </h1>
      }
        <div class="col-12">
          <div class="input-group mb-3 mt-5 mb-5" style = {{width : 60 + '%', margin : "auto"}}>
            <input
              type="text"
              class={theme == "dark" ? "form-control searchField-dark": "form-control"}
              id="searchField"
              placeholder="Search Doodle or type a URL"
              onChange = {(e) => {query = e.target.value}}
              onKeyDown={(e)=>{handleEnter(e)}}
            />
          </div>
        </div>

        <div class="col-12 mt-1">

          <div class="container">
            <div class="row justify-content-center">
              <div class="col-12 col-lg-2 mt-2 mt-lg-0"><button id="find" class={"btn" + (theme == "dark" ? " btn-dark" :"")} onClick={(e)=>{process_query()}}>Doodle Search</button></div>
              <div class="col-12 col-lg-2 mt-2 mt-lg-0"><button class={(theme == "dark" ? "btn btn-dark" :"btn")}>i'm feeling lucky</button></div>
            </div>
          </div>

        </div>
      </div>
    </div>
    </div>

/*
    <div>
      <input
        type="text"
        onChange={(e) => {
          setQuery(e.target.value);
        }}
      ></input>
      <button
        onClick={process_query()}
      >
        search
      </button>
    </div>
*/
  );
}


export default SearchPage;
