package com.graphics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class State {
	int id;
	//string - input symbol
    //HashSet<> - states
	Map<String,HashSet<State>> transitions;
	
	State(int i){
		id=i;
		transitions=new HashMap<>();
	}

	
	void addTransition(String key,State s){
		HashSet<State> set;
		if(transitions.containsKey(key)){
			set = transitions.get(key);

		}
		else{
			set=new HashSet<>();
		}
		set.add(s);
		transitions.put(key,set);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return id+"";
	}
}
