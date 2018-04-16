package com.graphics;

import java.awt.Dimension;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class DataCalculator {

    private int n;
    private Point centre_main = new Point(0, 0);
    private Circle states[];
    private State autometa[];
    private List<Arrow> transition;
    private SelfLoop sls[];
    private String input[][];
    private int start;

    private static int sizeX = 0, sizeY = 0;

    DataCalculator(String file) {
        try {
            getData(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        transitionCalculations();

    }

    static Dimension getDimensions() {
        System.out.println("size - " + sizeX + " " + sizeY);

        // set Dimension with (50 + com.graphics.Circle.DIAMETER) as padding
        return new Dimension(sizeX + 50 + Circle.DIAMETER, sizeY + 50 + Circle.DIAMETER);
    }

    private void getData(String file) throws IOException {
        BufferedReader scan = null;
        try {
            scan = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        n = Integer.parseInt(scan.readLine());
        autometa = new State[n];
        input = new String[n][n];

        stateCalculations();
        int self_loop_count = 0;

        for (int i = 0; i < n; i++)
            autometa[i] = new State(i);

        String temp;
        temp = scan.readLine();
        start=Integer.parseInt(temp.substring(6));


        temp = scan.readLine();
        while (!temp.startsWith("final")) {
            StringTokenizer st = new StringTokenizer(temp);

            int from = Integer.parseInt(st.nextToken());
            String at = st.nextToken();
            int to = Integer.parseInt(st.nextToken());

            if (input[from][to] == null) {
                input[from][to] = at;
            } else {
                input[from][to] = input[from][to] + ", " + at;
            }

            if (from == to)
                self_loop_count++;

            autometa[from].addTransition(at, autometa[to]);
            temp=scan.readLine();
        }

        StringTokenizer st = new StringTokenizer(temp);
        st.nextToken();
        while (st.hasMoreTokens()) {
            states[Integer.parseInt(st.nextToken())].setFinal();
        }
        sls = new SelfLoop[self_loop_count];
        transition = new ArrayList<>();
        scan.close();

//        for (String s[] : input) {
//            System.out.println(Arrays.toString(s));
//        }
    }

    State[] getStates() {
        return autometa;
    }

    private void stateCalculations() {
        int radiusRatio = n / 4+1;
        if (n % 4 != 0) {
            radiusRatio++;
        }

        int radius_main = 2 * Circle.RADIUS * radiusRatio;

        // coordinates of the main circle is set to the radius with 50pts as padding
        centre_main.x = 50 + radius_main;
        centre_main.y = centre_main.x;

        System.out.println("radius_main - " + radius_main);

        states = new Circle[n];
        double theta = 360 / n;

        for (int i = 0; i < n; i++) {

            // the centre of each state's circle is set to the theta*i degree position from
            // the Main circle's 3'o clock
            states[i] = new Circle(i, centre_main.x + radius_main * Math.cos(Math.toRadians(theta * i)),
                    centre_main.y + radius_main * Math.sin(Math.toRadians(theta * i)));
            states[i].angle = theta * i;

            // System.out.println(states[i]);
        }

        // Dimension calculation
        for (Circle c : states) {
            if (c.centre.x > sizeX) {
                sizeX = c.centre.x;
            }
            if (c.centre.y > sizeY) {
                sizeY = c.centre.y;
            }
        }

    }

    private void transitionCalculations() {
        int tidx = 0, sidx = 0;
        double theta;
        Point p, q1, q2, q;
        for (int i = 0; i < n; i++) {
            // System.out.println(autometa[i].transitions);
            for (Entry<String, HashSet<State>> e : autometa[i].transitions.entrySet()) {
                for (State state : e.getValue()){
                    // selfLoop det.
                    if (i == state.id) {

                        p = getPointWRT(states[i].centre, Math.toRadians(states[i].angle), Circle.RADIUS, 1, 1);
                        q1 = getPointRotation(p, states[i].centre, 45);
                        q2 = getPointRotation(p, states[i].centre, -45);
                        double radius = p.getDistFrom(q1);

                        theta = states[i].angle;
                        sls[sidx] = new SelfLoop(sidx, p, radius, theta);
                        sls[sidx].string_pos=getPointWRT(states[i].centre, Math.toRadians(states[i].angle), 1.5*Circle.RADIUS, 1, 1);
                        sls[sidx].end = q2;

                        theta = calTheta(sls[sidx].end, states[i].centre);
                        q1 = sls[sidx].end;
                        q2 = states[i].centre;
                        int Xm = 1, Ym = 1;
                        if ((q2.y < q1.y && q2.x > q1.x) || (q2.y > q1.y && q2.x > q1.x)) {
                            Xm = Ym = -1;
                        }

                        q = getPointWRT(states[i].centre, theta, Circle.RADIUS + Arrow.POINTER_SIZE, Xm, Ym);
                        // System.out.println("q -" + q);

                        sls[sidx].p1 = getPointRotation(q, sls[sidx].end, 80);
                        sls[sidx].p2 = getPointRotation(q, sls[sidx].end, 350);
                        sls[sidx].input_symbol=input[i][state.id];
                        sidx++;
                        continue;
                    }

                    Arrow a = new Arrow(tidx);
                    q1 = states[i].centre;
                    q2 = states[state.id].centre;

                    theta = calTheta(q1, q2);
                    // System.out.println("theta - " + Math.toDegrees(theta));

                    int Xm = 1, Ym = 1;
                    if ((q2.y < q1.y && q2.x > q1.x) || (q2.y > q1.y && q2.x > q1.x)) {
                        Xm = Ym = -1;
                    }

                    if(q1.y==q2.y && q1.x<q2.x){
                        Xm=-1;
                        Ym=0;

                    }

                    p = getPointWRT(q2, theta, Circle.RADIUS, Xm, Ym);
                    q = getPointWRT(p, theta, Arrow.POINTER_SIZE, Xm, Ym);
                    // System.out.println("q - " + q);



                    a.addPoint(getPointWRT(p, theta, (int) (p.getDistFrom(q1) - Circle.RADIUS), Xm, Ym));
                    a.addPoint(p);

                    a.string_pos=Point.getPointbet(a.getPointAt(0),p,0.25);
                    a.input_symbol=input[i][state.id];

                    a.p1 = getPointRotation(q, p, 45);
                    a.p2 = getPointRotation(q, p, 315);
                    a.p3 = p;

                    if (containReverse(a)) {
                        q = a.getPointAt(0);
                        double dm = p.getDistFrom(q) / 2;
                        double d = Circle.RADIUS;

                        theta = Math.atan(d / dm) + calTheta(p, q);
                        d = Math.sqrt((d * d) + (dm * dm));

                        q1 = q;
                        q2 = p;
                        Xm = 1;
                        Ym = 1;
                        if ((q2.y < q1.y && q2.x > q1.x) || (q2.y > q1.y && q2.x > q1.x)) {
                            Xm = Ym = -1;
                        }


                        q = getPointWRT(p, theta, d, Xm, Ym);
                        a.pts.add(1, q);

                        a.p3 = getPointWRT(p, theta, d - Arrow.POINTER_SIZE, Xm, Ym);
                        a.p1 = getPointRotation(q, a.p3, 45);
                        a.p2 = getPointRotation(q, a.p3, 315);

                        a.string_pos=Point.getPointbet(a.getPointAt(0),a.p3,0.5);
                        a.input_symbol=input[i][state.id];

                    }

                    if (!transition.contains(a)) {
                        transition.add(a);
                        tidx++;
                    }
                }



            }
        }
    }

    private boolean containReverse(Arrow a) {
        for (Arrow i : transition) {
            if (i.reverseEquals(a))
                return true;
        }
        return false;
    }

    private Point getPointRotation(Point of, Point wrt, double theta) {
        Point p = new Point(of.x - wrt.x, of.y - wrt.y);
        double ct = Math.cos(Math.toRadians(theta));
        double st = Math.sin(Math.toRadians(theta));

        int px = (int) (p.x * ct - p.y * st);
        int py = (int) (p.x * st + p.y * ct);

        p.x = px + wrt.x;
        p.y = py + wrt.y;
        return p;
    }

    private Point getPointWRT(Point p, double theta, double d, int Xm, int Ym) {
        // System.out.println(p.y + Ym * d * Math.sin(theta));
        return new Point(p.x + Xm * d * Math.cos(theta), p.y + Ym * d * Math.sin(theta));
    }

    private static double calTheta(Point p1, Point p2) {
        double slope;
        try {
            slope = (double) (p1.y - p2.y) / (p1.x - p2.x);
        } catch (Exception e) {
            return Math.toRadians(90);
        }

        return Math.atan(slope);
    }


    Circle[] getCirclesData() {
        return states;
    }

    List<Arrow> getArrowsData() {
        return transition;
    }

    SelfLoop[] getSelfLoops() {
        return sls;
    }

    int getStart(){
        return start;
    }

}
