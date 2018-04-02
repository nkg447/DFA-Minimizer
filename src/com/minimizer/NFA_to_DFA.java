package com.minimizer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class NFA_to_DFA {

    static int start; //stores the id of the start state
    static Set<String> inputSymbols; // stores the set of input symbols
    static State dfa[]; //stores all the states data
    static Group final_states; //group of final states
    static Group non_final_states; //group of non-final states

    public static void nfa_to_dfa() throws IOException {

        //take all the inputs from file "nfa"
        Main.input("nfa");
        start=Main.start;
        inputSymbols=Main.inputSymbols;
        dfa=Main.dfa;
        final_states=Main.final_states;
        non_final_states=Main.non_final_states;

        List<Group> groups = new ArrayList<>();

        String output = "";
        output = output + "start " + start + "\n";

        Map<String, Integer> map = new HashMap<>();

        map.put(start + "", 0);
        int idx = 1;
        LinkedList<String> queue = new LinkedList<>();
        queue.add(start + "");

        String fstate = "final";
        boolean finalState;

        while (!queue.isEmpty()) {
            char state[] = queue.poll().toCharArray();

            for (String is : inputSymbols) {
                String tmp = "";
                finalState = false;

                for (char ch : state) {
                    //System.out.println(dfa[ch - 48]);
                    if (dfa[ch - 48].transitions.get(is) != null)
                        for (State i : dfa[ch - 48].transitions.get(is)) {
                            tmp += i.id;
                            if (final_states.contains(i))
                                finalState = true;
                        }
                }
                char array[] = tmp.toCharArray();
                Arrays.sort(array);
                tmp = new String(array);

                if (!map.containsKey(tmp)) {
                    queue.add(tmp);
                    map.put(tmp, idx++);

                    if (finalState) {
                        fstate = fstate + " " + map.get(tmp);
                    }
                }

                output = output + map.get(String.valueOf(state)) + " " + is + " " + map.get(tmp) + "\n";

            }
        }

        output = idx + "\n" + output;
        output = output + fstate+"\n";

        System.out.println(output);

        PrintWriter pw = new PrintWriter(new FileWriter("dfa"));
        pw.println(output);
        pw.close();
        new com.graphics.MainFrame("nfa", "NFA");
        new com.graphics.MainFrame("dfa", "DFA");



    }
}
