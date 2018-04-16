package com.minimizer;


import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    static int start; //stores the id of the start state
    static Set<String> inputSymbols; // stores the set of input symbols
    static State dfa[]; //stores all the states data
    static Group final_states; //group of final states
    static Group non_final_states; //group of non-final states

    public static void input(String file) throws IOException {
        BufferedReader scan = null;
        try {
            scan = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int n = Integer.parseInt(scan.readLine());
        dfa = new State[n];
        for (int i = 0; i < n; i++) {
            dfa[i] = new State(i);
        }

        String temp;
        inputSymbols = new HashSet<>();

        temp = scan.readLine();
        start = Integer.parseInt(temp.substring(6));

        temp = scan.readLine();
        while (!temp.startsWith("final")) {
            StringTokenizer st = new StringTokenizer(temp);

            int from = Integer.parseInt(st.nextToken());
            String at = st.nextToken();
            int to = Integer.parseInt(st.nextToken());
            dfa[from].put(at, dfa[to]);
            inputSymbols.add(at);
            temp = scan.readLine();
        }


        final_states = new Group(0);

        StringTokenizer st = new StringTokenizer(temp);
        st.nextToken();
        while (st.hasMoreTokens()) {
            final_states.add(dfa[Integer.parseInt(st.nextToken())]);
        }

    }

    static void inputDFA() throws IOException {
        System.out.println("1 - NFA to DFA");
        System.out.println("2 - Minimize DFA");

        int choice = Integer.parseInt(br.readLine());

        if (choice == 1) {
            NFA_to_DFA.nfa_to_dfa();
        } else {
            DFA_Minimizer.minimize();
        }

        if (choice == 1) {

            storeDFAToDB("nfa");
            storeDFAToDB("dfa");
        } else {

            storeDFAToDB("before_minimization");
            storeDFAToDB("after_minimization");
        }
    }

    private static void storeDFAToDB(String file) {
        System.out.println("storing to DB from " + file);
        int type = getType(file);
        Connection conn = JDBC.getConnection();
        Statement stmt;

        try {
            stmt = conn.createStatement();
            BufferedReader scan = null;
            try {
                scan = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            int no_of_states = Integer.parseInt(scan.readLine());
            int start = Integer.parseInt(scan.readLine().substring(6));
            System.out.println("Enter a description for the DFA");
            String description = br.readLine();

            System.out.println("Enter a regex for the DFA");
            String regex = br.readLine();


            int id = 0;

            ResultSet count = stmt.executeQuery("select count(*) from automatas");
            while (count.next()) {
                id = count.getInt("count(*)") + 1;
            }
            System.out.println("id - " + id);

            String temp = scan.readLine();
            while (!temp.startsWith("final")) {
                StringTokenizer st = new StringTokenizer(temp);

                int from = Integer.parseInt(st.nextToken());
                String at = st.nextToken();
                int to = Integer.parseInt(st.nextToken());

                stmt.execute("INSERT INTO transitions VALUES(" +
                        id + ", " +
                        from + ", \"" +
                        at + "\", " +
                        to +
                        ");");

                temp = scan.readLine();
            }

            String final_states = temp.substring(6);

            stmt.execute("INSERT INTO automatas VALUES(" +
                    id + "," +
                    type + "," +
                    "\"" + description + "\"," +
                    "\"" + regex + "\"," +
                    no_of_states + "," +
                    start + "," +
                    id + "," +
                    "\"" + final_states + "\"" +
                    ");");


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("response stored into db");
    }

    private static int getType(String file) {
        if (file.equals("dfa")) {
            return 2;
        }
        if (file.equals("before_minimization"))
            return 1;
        if (file.equals("after_minimization"))
            return 2;
        return 3;

    }

    static void fromDB() throws IOException {
        Connection con = JDBC.getConnection();

        System.out.println(con);
        Statement stmt;

        try {
            stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("select * from automatas");

            while (rs.next()) {
                System.out.println("id - " + rs.getInt("id") +
                        "\t " + rs.getString("description"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        selectDB();
    }

    private static void selectDB() throws IOException {
        System.out.println("\n\nfrom the above select one Automata");
        int id = Integer.parseInt(br.readLine());
        Connection con = JDBC.getConnection();

        System.out.println(con);
        Statement stmt;

        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from automatas where id=" + id);
            String out = "";

            while (rs.next()) {
                int type = rs.getInt("type");

                String file = displayDFAType(type);
                out += rs.getString("no_of_states") + "\n";
                out += "start " + rs.getString("start_state") + "\n";
                ResultSet trans = con.createStatement().executeQuery("select * from transitions where id=" + rs.getInt("transitions"));

                while (trans.next()) {
                    out += trans.getInt("from_id") + " " + trans.getString("input_symbol") + " " + trans.getInt("to_id") + "\n";

                }
                out += "final ";
                for (String i : rs.getString("final_states").split(" ")) {
                    out += i + " ";
                }

                if (file.equals("after_minimization")) {
                    System.out.println("you have chosen a minimized dfa");
                    file = "before_minimization";
                }

                PrintWriter pw = new PrintWriter(new FileWriter(file));
                pw.println(out);
                pw.close();

                if (file.equals("nfa")) {
                    NFA_to_DFA.nfa_to_dfa();
                } else {
                    DFA_Minimizer.minimize();
                }
            }


        } catch (Exception e) {

        }

    }

    private static String displayDFAType(int type) {
        switch (type) {
            case 1:
                System.out.println("DFA detected");
                return "before_minimization";
            case 2:
                System.out.println("min DFA detected");
                return "after_minimization";
            case 3:
                System.out.println("NFA detected");
                return "nfa";
        }
        return null;
    }


    public static void main(String[] args) throws IOException {

        System.out.println("1 - Choose from Database");
        System.out.println("2 - Input new DFA");

        int choice = Integer.parseInt(br.readLine());
        if (choice == 2)
            inputDFA();
        else
            fromDB();


    }


}
