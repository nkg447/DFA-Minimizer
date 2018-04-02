package com.minimizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class State {
    int id;
    //Map<String,State> transitions;
    Map<String,HashSet<State>> transitions;

    State(int i){
        id=i;
        transitions=new HashMap<>();
    }

    void put(String key,State value){

        HashSet<State> set;
        if(transitions.containsKey(key)){
            set = transitions.get(key);

        }
        else{
            set=new HashSet<>();
        }
        set.add(value);
        transitions.put(key,set);

        //transitions.put(key,value);
    }

    @Override
    public String toString() {
        return "state "+id;
    }


}
