package dev.SearchEngine.SearchEngine;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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
    return krkr;
    }

@PostMapping("/query")
public String create (@RequestBody String searchQuery){
    searchCall MoA = new searchCall(10,searchQuery);
    ArrayList<String> tempArr = new ArrayList<String>();
     tempArr =  MoA.main();
      krkr = tempArr;
  //  krkr.add(searchQuery);
    return searchQuery;
}


}
