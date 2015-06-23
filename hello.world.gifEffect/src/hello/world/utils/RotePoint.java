package hello.world.utils;

public class RotePoint {
	/**
	 * 计算点P(x,y)与X轴正方向的夹角
	 * 
	 * @param x横坐标
	 * @param y纵坐标
	 * @return 夹角弧度
	 */
	private static double radPOX(float x, float y) {
		// P在(0,0)的情况
		if (x == 0 && y == 0)
			return 0;

		// P在四个坐标轴上的情况：x正、x负、y正、y负
		if (y == 0 && x > 0)
			return 0;
		if (y == 0 && x < 0)
			return Math.PI;
		if (x == 0 && y > 0)
			return Math.PI / 2;
		if (x == 0 && y < 0)
			return Math.PI / 2 * 3;

		// 点在第一、二、三、四象限时的情况
		if (x > 0 && y > 0)
			return Math.atan(y / x);
		if (x < 0 && y > 0)
			return Math.PI - Math.atan(y / -x);
		if (x < 0 && y < 0)
			return Math.PI + Math.atan(-y / -x);
		if (x > 0 && y < 0)
			return Math.PI * 2 - Math.atan(-y / x);

		return 0;
	}

	/**
	 * 
	 * @param P
	 *            待旋转点坐标
	 * @param A
	 *            旋转中心坐标
	 * @param rad
	 *            旋转弧度
	 * @param isClockwise
	 *            true:顺时针
	 * @return
	 */
	public static MPoint rotatePoint(MPoint P, MPoint A, double rad,
			boolean isClockwise) {
		MPoint Temp1 = new MPoint(P.x - A.x, P.y - A.y);
		float lenO2Temp1 = Temp1.distanceTo(new MPoint(0, 0));
		float angT1OX = (float) radPOX(Temp1.x, Temp1.y);
		double angT2OX = angT1OX - (isClockwise ? 1 : -1) * rad;
		MPoint Temp2 = new MPoint((float) (lenO2Temp1 * Math.cos(angT2OX)),
				(float) (lenO2Temp1 * Math.sin(angT2OX)));
		return new MPoint(Temp2.x + A.x, Temp2.y + A.y);
	}

	public static int rad(MPoint A, MPoint B, MPoint C) {
		int r = 1;
		float c = A.distanceTo(B);
		float a = B.distanceTo(C);
		float b = C.distanceTo(A);
		if (a < 50 || b < 50) {
			return 0;
		}
		int rotaD = (int) (Math.acos((a * a + b * b - c * c) / (2 * a * b)) * 180 / Math.PI);
		double d1 = radPOX(A.x - C.x, -A.y + C.y);
		double d2 = radPOX(B.x - C.x, -B.y + C.y);

		if (d1 - d2 < -3) {
			return rotaD;
		}

		if (d1 - d2 > 3) {
			return -rotaD;
		}

		if (d1 < d2) {

			return -rotaD;
		} else {

			return rotaD;
		}

	}
	public static double radPOY(float x, float y) {
		// P在(0,0)的情况
		
		if (x == 0 && y == 0)
			return 0;

		// P在四个坐标轴上的情况：x正、x负、y正、y负
		if (y == 0 && x > 0)
			return Math.PI / 2;
		if (y == 0 && x < 0)
			return Math.PI/2*3;
		if (x == 0 && y > 0)
			return Math.PI ;
		if (x == 0 && y < 0)
			return 0;

		// 点在第一、二、三、四象限时的情况
		if (x > 0 && y > 0)
			return Math.atan(y / x)+Math.PI / 2;
		if (x < 0 && y > 0)
			return Math.PI - Math.atan(y / -x)+Math.PI / 2;
		if (x < 0 && y < 0)
			return Math.PI + Math.atan(-y / -x)+Math.PI / 2;
		if (x > 0 && y < 0)
			return Math.atan(-y / x);

		return 0;
	}

}
