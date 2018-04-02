package com.minimizer;

import java.util.ArrayList;

public class Group extends ArrayList<State> {

    private int id;

    Group(int i) {
        id = i;
    }

    Group(Group g, int i) {
        id=i;
        for(State s : g){
            add(s);
        }

    }

    @Override
    public boolean add(State state) {
        if(this.contains(state))
            return false;
        return super.add(state);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Group " + id + "\t" + super.toString();
    }
}
