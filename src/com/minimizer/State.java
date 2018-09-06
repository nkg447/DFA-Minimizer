package com.minimizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class State {
    public int id;
    //Map<String,State> transitions;
    public Map<String,HashSet<State>> transitions;

    public State(int i){
        id=i;
        transitions=new HashMap<>();
    }

    public void put(String key,State value){

        HashSet<State> set;
        if(transitions.containsKey(key)){
            set = transitions.get(key);

        }
        else{
            set=new HashSet<>();
        }
        set.add(value);
        transitions.put(key,set);

    }

    @Override
    public String toString() {
        return "state "+id;
    }


}
