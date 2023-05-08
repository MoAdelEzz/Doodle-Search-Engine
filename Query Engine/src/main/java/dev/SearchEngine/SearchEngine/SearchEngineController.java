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
    String[] arr = new String[1];
     tempArr =  MoA.main(arr);
      krkr = tempArr;
  //  krkr.add(searchQuery);
    return searchQuery;
}


}
