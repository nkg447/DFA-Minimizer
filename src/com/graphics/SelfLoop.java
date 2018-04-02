package com.graphics;

public class SelfLoop {
    int id;
    Point centre;
    int radius;
    int theta;
    String input_symbol;
    Point string_pos;

    Point p1, p2, end;

    SelfLoop(int i, Point p, double r, double t) {
        id = i;
        centre = p;
        radius = (int) r;
        theta = (int) t;
    }

    @Override
    public String toString() {
        return "com.graphics.SelfLoop " + id
                + "{\n\t"
                    + " Centre - " + centre
                    + "\n\tRadius - " + radius
                    + "\n\tTheta - " + theta
                    + "\n\tP1 - " + p1
                    + "\n\tP2 - " + p2
                    + "\n\tEnd - " + end + "\n\t"
                    + "input symbol - " + input_symbol + "\n\t"
                    + "input symbol position - " + string_pos
                + "}\n";
    }
}
