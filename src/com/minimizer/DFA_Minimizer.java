package com.minimizer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class DFA_Minimizer {

    static int start; //stores the id of the start state
    static Set<String> inputSymbols; // stores the set of input symbols
    static State dfa[]; //stores all the states data
    static Group final_states; //group of final states
    static Group non_final_states; //group of non-final states

    public static void minimize() throws IOException {

        //take all the inputs from file "before_minimization"
        Main.input("before_minimization");
        start = Main.start;
        inputSymbols = Main.inputSymbols;
        dfa = Main.dfa;
        final_states = Main.final_states;
        non_final_states = Main.non_final_states;


        //list of all groups
        List<Group> groups = new ArrayList<>();

        // stores the group of non final states
        non_final_states = new Group(1);


        for (State s : dfa) {       // for each state s in dfa
            /*
             * all states that are not present in final
             * state group are added in non-final group
             */
            if (!final_states.contains(s))
                non_final_states.add(s);
        }

        //add final and the non-final group in the list of groups
        groups.add(final_states);
        groups.add(non_final_states);

        //initial set of groups
        System.out.println("initially - \n" + groups);

        //remove all the states from the groups that are unreachable
        groups = removeUnreachableStates(groups, dfa, start);

        //set of groups after removing unreachable states
        System.out.println("removed unreachable - \n" + groups);
        System.out.println();

        //minimize all the groups
        groups = minimize(groups);

        //set of minimized groups
        System.out.println("minimized - \n" + groups);

        //draw the DFA before minimization
        new com.graphics.MainFrame(
                "before_minimization", "BEFORE MINIMIZATION");


        //map all different groups to a specific integer that is there id
        Map<Integer, Integer> map = new HashMap<>();
        int idx = 0;
        for (Group g : groups) {
            for (State s : g) {
                map.put(s.id, idx);
            }
            idx++;
        }

        PrintWriter pw = new PrintWriter(new FileWriter("after_minimization"));

        //store all the set of transitions after minimization
        Set<String> trans = new HashSet<>();
        for (Group g : groups) {
            for (State s : g) {
                for (Map.Entry<String, HashSet<State>> e : s.transitions.entrySet()) {
                    for (State state : e.getValue()) {
                        trans.add(map.get(s.id) + " " + e.getKey() + " " + map.get(state.id));
                    }

                }
            }
        }

        //write all the minimized DFA's data to the file named "after_minimization"
        pw.println(idx); //number of states after minimization
        pw.println("start " + map.get(start)); // print "start "+id of start state
        for (String s : trans) {
            pw.println(s);
        }

        //write all the ids of final state
        pw.print("final ");
        for (State s : final_states) {
            pw.print(" " + map.get(s.id));
        }
        pw.close();

        //draw the DFA after minimization
        new com.graphics.MainFrame(
                "after_minimization", "AFTER MINIMIZATION");
    }

    /*
     * Breadth First Search implementation to remove unreachable states
     * returns list of groups free from unreachable states
     * */
    private static List<Group> removeUnreachableStates(
            List<Group> groups, State dfa[], int start) {

        boolean reachable[] = new boolean[dfa.length];
        reachable[start] = true;
        List<State> queue = new LinkedList<>();
        queue.add(dfa[start]);

        while (!queue.isEmpty()) {
            State u = queue.remove(0);

            for (Map.Entry<String, HashSet<State>> e : u.transitions.entrySet()) {

                for (State s : e.getValue()) {
                    if (!reachable[s.id]) {
                        queue.add(s);
                        reachable[s.id] = true;
                    }
                }


            }
        }

        //remove all the states that are not visible/reachable
        System.out.println(Arrays.toString(reachable));
        for (int i = 0; i < dfa.length; i++) {
            if (!reachable[i]) {
                for (Group g : groups) {
                    g.remove(dfa[i]);
                }
            }
        }

        return groups;
    }

    /*
     * Recursive function
     * to minimize the number of states
     * until they can be divided
     *
     * return a list of groups of states what are similar
     * and can be showed as a single state
     */
    private static List<Group> minimize(List<Group> groups) {
        System.out.println(groups);

        //list of groups after division of groups
        List<Group> result = new ArrayList<>();

        int idx = 0;

        for (Group g : groups) { //for all Group g in groups
            if (g.size() > 1) {
                for (int i = 0; i < g.size(); i++) {

                    /*
                     * if the result group already contains the current
                      * group then we need not to check it
                     */
                    if (containsState(result, g.get(i))) {
                        continue;
                    }

                    /*
                     * a new group to store states that are
                     * not unique among the current group g
                     */
                    Group new_group = new Group(idx++);
                    new_group.add(g.get(i));

                    for (int j = i + 1; j < g.size(); j++) {

                        State state1 = g.get(i);
                        State state2 = g.get(j);

                        /*
                         * if any two states in the same group are not unique
                         * then add the second state to a new group
                         */
                        if (!areStatesUnique(groups, state1, state2)) {
                            new_group.add(state2);
                        }
                    }

                    //add the new group to the result
                    result.add(new_group);
                }
            }
            //when the group contains only one state
            else {
                result.add(new Group(g, idx++));
            }
        }

        // if already minimized
        if (groups.size() == result.size()) return result;

        // minimize further
        return minimize(result);

    }


    /*
    * A function to check weather given
    * two states are unique or not
    * */
    private static boolean areStatesUnique(List<Group> groups, State state1, State state2) {

        for (Map.Entry<String, HashSet<State>> e : state1.transitions.entrySet()) {
            for (State s : e.getValue()) {
                if (!containedBySameGroup(groups, s, state2.transitions.get(e.getKey()))) {
                    return true;
                }
            }
        }
        return false;

    }

    /*
    * returns true if the given state is in the given list of groups
    * */
    private static boolean containsState(List<Group> groups, State state) {
        for (Group g : groups) {
            if (g.contains(state)) return true;
        }
        return false;
    }

    /*
    * returns true if both the given states are in the same group
    *
    * Note - HashSet<State> s contains a single state only
    *       A set of states was only created for the sake of calculation for NFAs
    * */
    private static boolean containedBySameGroup(List<Group> groups, State s1, HashSet<State> s2) {
        for (Group g : groups) {
            if (g.contains(s1) && g.contains(s2)) {
                return true;
            }
        }
        return false;
    }
}
