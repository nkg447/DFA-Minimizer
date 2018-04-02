package com.graphics;

public class Circle {

	static int RADIUS = 20, DIAMETER = RADIUS * 2;
	int id;
	Point centre;
	double angle;
	boolean finalState;

	Circle(int i, double d, double e) {
		id = i;
		centre = new Point(d, e);
		finalState=false;
	}

	void setFinal(){
		finalState=true;
	}

	boolean isFinal(){
		return finalState;
	}

	@Override
	public String toString() {
		return "com.graphics.Circle " + id
				+ "{\n"
					+ "\t Centre - " + centre
				+ "\n}\n";
	}
}
