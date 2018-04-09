package com.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {
    public static void main(String[] args) {
        Connection con = JDBC.getConnection();

        System.out.println(con);
        Statement stmt;

        try {
            stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("select * from automatas where id=1");

            while (rs.next()) {
                Automata a = new Automata(rs.getInt("id"),
                        rs.getInt("type"),
                        rs.getString("description"),
                        rs.getString("regx"),
                        rs.getInt("no_of_states"),
                        rs.getInt("start_state"));

                for(String i : rs.getString("final_states").split(" ")){
                    a.addFinalState(Integer.parseInt(i));
                }

                ResultSet trans = con.createStatement().executeQuery("select * from transitions where id=" + rs.getInt("transitions"));

                while (trans.next()) {
                    a.addTransition(trans.getInt("from_id"), trans.getString("input_symbol"), trans.getInt("to_id"));
                }



                System.out.println(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
