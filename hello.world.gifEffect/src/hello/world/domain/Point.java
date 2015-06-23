package hello.world.domain;

import java.io.Serializable;

public class Point implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 43333333333222251L;

	public Point() {
	}
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public Point(int x, int y,int bitmapStyle) {
		this.x = x;
		this.y = y;
		this.bitmapStyle = bitmapStyle;
	}

	public int x;
	public int y;
	public int pointD;
	public int pointColor;
	public int bitmapStyle;
	public boolean last;

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + ", radius=" + pointD
				+ ", paintStyle=" + pointColor + ", bitmapStyle=" + bitmapStyle
				+ ", drawWhat=" + drawWhat + "]";
	}

	public int drawWhat;
	public int paintStyle;

}