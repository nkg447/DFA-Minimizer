package com.minimizer;

import java.util.HashSet;
import java.util.Set;

public class Automata {
    int id;
    int type;
    String description;
    String regx;
    int no_of_states;
    int start_state;
    State states[];
    Set<Integer> final_states;

    Automata(int id, int type, String desc, String regx, int no, int st) {
        no_of_states = no;
        states = new State[no_of_states];
        for (int i = 0; i < no_of_states; i++) {
            states[i] = new State(i);
        }
        final_states = new HashSet<>();
        this.id = id;
        this.type = type;
        description = desc;
        this.regx = regx;
        start_state = st;
    }

    void addTransition(int from, String at, int to) {
        states[from].put(at, states[to]);
    }

    void addFinalState(int state) {
        final_states.add(state);
    }

    @Override
    public String toString() {
        String s="";
        for(State state : states){
            s=s+"\t"+state.id+" "+state.transitions+" \n";
        }
        return "Automata"+id+" {\n" +
                "\t" + "type - " + type + "\n" +
                "\t" + "description - " + description + "\n" +
                "\t" + "regx - " + regx + "\n" +
                "\t" + "number of states - " + no_of_states + "\n" +
                "\t" + "start state - " + start_state + "\n" +
                "\t" + "states - " + s + "\n" +
                "\t" + "final states - " + final_states + "\n" +
                "}";
    }
}
