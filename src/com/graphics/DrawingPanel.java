package com.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DrawingPanel extends JPanel {

    private DataCalculator d;

    DrawingPanel() {
        d= MainFrame.getDataCalculator();
        setLayout(null);
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("clicked at - " + e.getPoint());
            }
        });

        JLabel l=new JLabel("starting state - "+d.getStart());
        l.setBounds(10,10,100,20);
        add(l);
    }

    private Color getColor(int i) {
        switch (i % 5) {
            case 0:
                return Color.BLUE;

            case 1:
                return Color.GREEN;

            case 2:
                return Color.MAGENTA;

            case 3:
                return Color.GRAY;

            case 4:
                return Color.ORANGE;
        }
        return Color.BLACK;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Circle[] states = d.getCirclesData();


        g.setColor(Color.BLACK);
        for (Circle c : states) {
            drawCircle(g, c);
        }


        SelfLoop sls[] = d.getSelfLoops();
        for (SelfLoop s : sls) {
            drawSelfLoops(g, s);
        }

        List<Arrow> trans = d.getArrowsData();
        for (Arrow a : trans) {
            drawArrow(g, a);
        }

        for (Arrow a : trans) {
            drawString(g, a);
        }

    }

    private void drawString(Graphics g, Arrow a) {
        int mul;
        if (a.input_symbol.length() % 2 == 0) {
            mul = a.input_symbol.length() / 2;
        } else {
            mul = a.input_symbol.length() / 2 + 1;
        }

        g.setColor(Color.WHITE);
        g.fillRect(a.string_pos.x, a.string_pos.y - 10, 10 * mul, 10);

        g.setColor(getColor(a.id));
        g.drawString(a.input_symbol, a.string_pos.x, a.string_pos.y);

    }

    private void drawSelfLoops(Graphics g, SelfLoop s) {
        System.out.println(s);
        g.setColor(getColor(s.id));

        g.drawArc(s.centre.x - s.radius, s.centre.y - s.radius, 2 * s.radius, 2 * s.radius, 112 - s.theta, -224);
        g.drawLine(s.p1.x, s.p1.y, s.end.x, s.end.y);
        g.drawLine(s.p2.x, s.p2.y, s.end.x, s.end.y);


        int mul;
        if (s.input_symbol.length() % 2 == 0) {
            mul = s.input_symbol.length() / 2;
        } else {
            mul = s.input_symbol.length() / 2 + 1;
        }

        g.setColor(Color.WHITE);
        g.fillRect(s.string_pos.x, s.string_pos.y - 10, 10 * mul, 10);

        g.setColor(getColor(s.id));
        g.drawString(s.input_symbol, s.string_pos.x, s.string_pos.y);

    }

    private void drawArrow(Graphics g, Arrow a) {
        System.out.println(a);
        Color c = getColor(a.id);
        g.setColor(c);
        for (int i = 1; i < a.pts.size(); i++) {
            g.drawLine(a.getPointAt(i - 1).x, a.getPointAt(i - 1).y, a.getPointAt(i).x, a.getPointAt(i).y);
        }

        g.drawLine(a.p1.x, a.p1.y, a.p3.x, a.p3.y);
        g.drawLine(a.p2.x, a.p2.y, a.p3.x, a.p3.y);


    }

    private void drawCircle(Graphics g, Circle c) {
        System.out.println(c);

        if (c.isFinal()) {
            g.drawArc(c.centre.x - Circle.RADIUS + 5, c.centre.y - Circle.RADIUS + 5, Circle.DIAMETER - 10, Circle.DIAMETER - 10, 0, 360);
        }

        g.drawArc(c.centre.x - Circle.RADIUS, c.centre.y - Circle.RADIUS, Circle.DIAMETER, Circle.DIAMETER, 0, 360);
        g.drawString(c.id + "", c.centre.x, c.centre.y);

    }

}
