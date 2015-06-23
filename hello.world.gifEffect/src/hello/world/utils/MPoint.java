package hello.world.utils;

public class MPoint {

	public float x, y;

	public MPoint(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float distanceTo(MPoint p) {
		return (float) Math.sqrt((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y));
	}

}
