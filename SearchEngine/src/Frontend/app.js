function urlExists(url) {
    fetch(url, { method: 'HEAD' })
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

//====================================================================

let buttons = document.getElementsByClassName('search_button');
let searchfield = document.getElementById('searchField');


searchfield.onfocus = ()=>
{
    for(let btn of buttons)
    {
        btn.style.borderColor="#007bff";
    }
}

searchfield.addEventListener("focusout", function() {
    for(let btn of buttons)
    {
        btn.style.borderColor="rgba(0,0,0,0.3)";
    }
});


//=================================================================================

let find = document.getElementById('find');
let query = document.getElementById('searchField');

find.onclick = ()=>
{
    let q = "";
    q = query.value
 
    console.log(q);
 

}