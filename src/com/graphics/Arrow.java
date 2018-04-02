package com.graphics;

import java.util.ArrayList;
import java.util.List;

public class Arrow {
    static int POINTER_SIZE = Circle.RADIUS / 2;
    int id;
    List<Point> pts;
    Point p1, p2, p3;
    String input_symbol;
    Point string_pos;

    Arrow(int i) {
        id = i;
        pts = new ArrayList<>();
    }

    void addPoint(Point p) {
        pts.add(p);
    }

    Point getPointAt(int i) {
        return pts.get(i);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Arrow " + id
                + "{\n\t"
                    + "Points - " + pts + "\n\t"
                    + "p1 - " + p1 + "\n\t"
                    + "p2 - " + p2 + "\n\t"
                    + "p3 - " + p3 + "\n\t"
                    + "input symbol - " + input_symbol + "\n\t"
                    + "input symbol position - " + string_pos
                + "\n}\n";
    }

    @Override
    public boolean equals(Object obj) {
        Arrow o = (Arrow) obj;
        return o.pts.get(0).equals(this.pts.get(0))
                && o.pts.get(o.pts.size() - 1).equals(this.pts.get(this.pts.size() - 1));
    }

    boolean reverseEquals(Arrow o) {
        return o.pts.get(0).equals(this.pts.get(this.pts.size() - 1))
                && o.pts.get(o.pts.size() - 1).equals(this.pts.get(0));
    }
}
