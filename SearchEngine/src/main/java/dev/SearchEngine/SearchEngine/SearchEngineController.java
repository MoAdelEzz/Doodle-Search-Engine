package dev.SearchEngine.SearchEngine;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class SearchEngineController {
private ArrayList<String> krkr=new ArrayList<String >();

@GetMapping("/addOne")
public void haha(){
    krkr.add("fknnfjkdsnfk");
    krkr.add("sadssad");
    krkr.add("deddefvr");

}

    @GetMapping("/")
    public ArrayList<String> get (){

        System.out.println("here");
    ArrayList<String> s = new ArrayList<>();
    s.add("mohamed");
    s.add("adel");

    return krkr;
    }

@PostMapping("/query")
public String create (@RequestBody String searchQuery){
 queryEngine MoA = new queryEngine();
    ArrayList<String> tempArr = new ArrayList<String>();
     tempArr =  MoA.main(searchQuery);
      krkr = tempArr;
  //  krkr.add(searchQuery);
    return searchQuery;
}


}
