package com.minimizer;

import java.io.*;
import java.util.*;

public class DFA_Minimizer {

    private static void minimize() throws IOException {
        BufferedReader scan = null;
        try {
            scan = new BufferedReader(new FileReader("before_minimization"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int n = Integer.parseInt(scan.readLine());
        State dfa[] = new State[n];
        for (int i = 0; i < n; i++) {
            dfa[i] = new State(i);
        }

        String temp;

        temp = scan.readLine();
        int start = Integer.parseInt(temp.substring(6));

        temp = scan.readLine();
        while (!temp.startsWith("final")) {
            StringTokenizer st = new StringTokenizer(temp);

            int from = Integer.parseInt(st.nextToken());
            String at = st.nextToken();
            int to = Integer.parseInt(st.nextToken());
            dfa[from].put(at, dfa[to]);
            temp = scan.readLine();
        }

        List<Group> groups = new ArrayList<>();

        Group f = new Group(0);
        StringTokenizer st = new StringTokenizer(temp);
        st.nextToken();
        while (st.hasMoreTokens()) {
            f.add(dfa[Integer.parseInt(st.nextToken())]);
        }

        Group nf = new Group(1);
        for (State s : dfa) {
            if (!f.contains(s))
                nf.add(s);
        }

        groups.add(f);
        groups.add(nf);
        System.out.println("initially - \n" + groups);
        groups = removeUnreachableStates(groups, dfa, start);
        System.out.println("removed unreachable - \n" + groups);
        System.out.println();
        groups = minimize(groups);
        System.out.println("minimized - \n" + groups);

        new com.graphics.MainFrame("before_minimization", "BEFORE MINIMIZATION");

        //mapping

        Map<Integer, Integer> map = new HashMap<>();
        int idx = 0;
        for (Group g : groups) {
            for (State s : g) {
                map.put(s.id, idx);
            }
            idx++;
        }

        PrintWriter pw = new PrintWriter(new FileWriter("after_minimization"));

        pw.println(idx);
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
        pw.println("start " + map.get(start));
        for (String s : trans) {
            pw.println(s);
        }

        pw.print("final ");
        for (State s : f) {
            pw.print(" " + map.get(s.id));
        }
        pw.close();
        new com.graphics.MainFrame("after_minimization", "AFTER MINIMIZATION");
    }


    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        System.out.println("1 - NFA to DFA");
        System.out.println("2 - Minimize DFA");

        int choice = sc.nextInt();

        if (choice == 1) {
            nfa_to_dfa();
        } else {
            minimize();
        }

    }

    private static void nfa_to_dfa() throws IOException {

        BufferedReader scan = null;
        try {
            scan = new BufferedReader(new FileReader("input"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int n = Integer.parseInt(scan.readLine());
        State dfa[] = new State[n];
        for (int i = 0; i < n; i++) {
            dfa[i] = new State(i);
        }

        String temp;

        temp = scan.readLine();
        int start = Integer.parseInt(temp.substring(6));

        Set<String> inputSymbols = new HashSet<>();

        temp = scan.readLine();
        while (!temp.startsWith("final")) {
            StringTokenizer st = new StringTokenizer(temp);

            int from = Integer.parseInt(st.nextToken());
            String at = st.nextToken();
            int to = Integer.parseInt(st.nextToken());
            inputSymbols.add(at);
            dfa[from].put(at, dfa[to]);
            temp = scan.readLine();
        }

        List<Group> groups = new ArrayList<>();

        Set<State> f = new HashSet<>();
        StringTokenizer st = new StringTokenizer(temp);
        st.nextToken();
        while (st.hasMoreTokens()) {
            f.add(dfa[Integer.parseInt(st.nextToken())]);
        }

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
                            if (f.contains(i))
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
        new com.graphics.MainFrame("input", "NFA");
        new com.graphics.MainFrame("dfa", "DFA");



    }


    private static List<Group> removeUnreachableStates(List<Group> groups, State dfa[], int start) {
        //breadth first search to get reachable states
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


    private static List<Group> minimize(List<Group> groups) {
        System.out.println(groups);
        List<Group> result = new ArrayList<>();
        boolean unique;
        int idx = 0;
        for (Group g : groups) {
            if (g.size() > 1) {
                for (int i = 0; i < g.size(); i++) {

                    if (containsState(result, g.get(i))) {
                        continue;
                    }

                    Group new_group = new Group(idx++);
                    new_group.add(g.get(i));

                    for (int j = i + 1; j < g.size(); j++) {
                        unique = true;
                        for (Map.Entry<String, HashSet<State>> e : g.get(i).transitions.entrySet()) {

                            for (State s : e.getValue()) {
                                if (!containedBySameGroup(groups, s, g.get(j).transitions.get(e.getKey()))) {
                                    unique = false;

                                }
                            }


                        }
                        if (unique) {
                            new_group.add(g.get(j));
                        }
                    }
                    result.add(new_group);
                }
            } else {
                result.add(new Group(g, idx++));
            }
        }

        if (groups.size() == result.size()) return result;

        return minimize(result);

    }

    private static boolean containsState(List<Group> result, State state) {
        for (Group g : result) {
            if (g.contains(state)) return true;
        }
        return false;
    }

    private static boolean containedBySameGroup(List<Group> groups, State s1, HashSet<State> s2) {
        for (Group g : groups) {
            if (g.contains(s1) && g.contains(s2)) {
                return true;
            }
        }
        return false;
    }
}
