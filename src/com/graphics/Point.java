package com.graphics;

public class Point {
	int x, y;

	Point(double d, double e) {
		x = (int) d;
		y = (int) e;
	}

	double getDistFrom(Point p) {
		double dist = Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2);
		return Math.sqrt(dist);
	}

	static Point getPointbet(Point p1,Point p2,double ratio){
		return new Point((p2.x*ratio+p1.x)/(ratio+1),(p2.y*ratio+p1.y)/(ratio+1));
	}


	@Override
	public String toString() {
		return "( " + x + ", " + y + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		Point o=(Point)obj;
		return Math.abs(o.x-this.x)<=2 && Math.abs(o.y-this.y)<=2;
	}
}
