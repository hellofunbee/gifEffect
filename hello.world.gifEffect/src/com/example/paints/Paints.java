package com.example.paints;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Paints {

	/**
	 * 带参画笔
	 * 
	 * @param alpha
	 * @param color
	 * @param strokeWidth
	 * @return
	 */

	public static Paint getPaint(int alpha, int color, int strokeWidth) {

		Paint paint = new Paint();
		paint.setAntiAlias(true);

		paint.setAlpha(alpha);

		paint.setColor(color);
		paint.setStyle(Style.FILL);
		paint.setStrokeWidth(strokeWidth);
		return paint;

	}

	public static Paint getPaint(int color, int strokeWidth) {

		Paint paint = new Paint();
		paint.setAlpha(255);
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		paint.setStrokeWidth(strokeWidth);
		return paint;

	}

	/**
	 * alpha 255,int gery , strokeWidth 10 无参画笔
	 * 
	 * @return
	 */
	public static Paint getPaint() {

		Paint paint = new Paint();
		paint.setAntiAlias(true);

		paint.setAlpha(255);

		paint.setColor(Color.GRAY);
		paint.setStyle(Style.FILL);
		paint.setStrokeWidth(10);
		return paint;

	}

	public static Paint no() {

		Paint paint = new Paint();

		paint.setAlpha(0);

		return paint;

	}

	public static Paint randomColor() {

		Paint paint = new Paint();
		paint.setAntiAlias(true);

		paint.setAlpha(255);

		paint.setColor(colors[(int) (Math.random() * colors.length)]);
		paint.setStyle(Style.FILL);
		paint.setStrokeWidth(10);
		return paint;

	}

	public static int[] colors = new int[] { Color.parseColor("#FFB6C1"),
			Color.parseColor("#FFC0CB"), Color.parseColor("#DC143C"),
			Color.parseColor("#FFF0F5"), Color.parseColor("#DB7093"),
			Color.parseColor("#FF69B4"), Color.parseColor("#FF1493"),
			Color.parseColor("#C71585"), Color.parseColor("#DA70D6"),
			Color.parseColor("#D8BFD8"), Color.parseColor("#DDA0DD"),
			Color.parseColor("#EE82EE"), Color.parseColor("#FF00FF"),
			Color.parseColor("#FF00FF"), Color.parseColor("#8B008B"),
			Color.parseColor("#800080"), Color.parseColor("#BA55D3"),
			Color.parseColor("#9400D3"), Color.parseColor("#9932CC"),
			Color.parseColor("#4B0082"), Color.parseColor("#8A2BE2"),
			Color.parseColor("#9370DB"), Color.parseColor("#7B68EE"),
			Color.parseColor("#6A5ACD"), Color.parseColor("#483D8B"),
			Color.parseColor("#E6E6FA"), Color.parseColor("#F8F8FF"),
			Color.parseColor("#0000FF"), Color.parseColor("#0000CD"),
			Color.parseColor("#191970"), Color.parseColor("#00008B"),
			Color.parseColor("#000080"), Color.parseColor("#4169E1"),
			Color.parseColor("#6495ED"), Color.parseColor("#B0C4DE"),
			Color.parseColor("#778899"), Color.parseColor("#708090"),
			Color.parseColor("#1E90FF"), Color.parseColor("#F0F8FF"),
			Color.parseColor("#4682B4"), Color.parseColor("#87CEFA"),
			Color.parseColor("#87CEEB"), Color.parseColor("#00BFFF"),
			Color.parseColor("#ADD8E6"), Color.parseColor("#B0E0E6"),
			Color.parseColor("#5F9EA0"), Color.parseColor("#F0FFFF"),
			Color.parseColor("#E1FFFF"), Color.parseColor("#AFEEEE"),
			Color.parseColor("#00FFFF"), Color.parseColor("#00FFFF"),
			Color.parseColor("#00CED1"), Color.parseColor("#2F4F4F"),
			Color.parseColor("#008B8B"), Color.parseColor("#008080"),
			Color.parseColor("#48D1CC"), Color.parseColor("#20B2AA"),
			Color.parseColor("#40E0D0"), Color.parseColor("#7FFFAA"),
			Color.parseColor("#00FA9A"), Color.parseColor("#F5FFFA"),
			Color.parseColor("#00FF7F"), Color.parseColor("#3CB371"),
			Color.parseColor("#2E8B57"), Color.parseColor("#F0FFF0"),
			Color.parseColor("#90EE90"), Color.parseColor("#98FB98"),
			Color.parseColor("#8FBC8F"), Color.parseColor("#32CD32"),
			Color.parseColor("#00FF00"), Color.parseColor("#228B22"),
			Color.parseColor("#008000"), Color.parseColor("#006400"),
			Color.parseColor("#7FFF00"), Color.parseColor("#7CFC00"),
			Color.parseColor("#ADFF2F"), Color.parseColor("#556B2F"),
			Color.parseColor("#6B8E23"), Color.parseColor("#FAFAD2"),
			Color.parseColor("#FFFFF0"), Color.parseColor("#FFFFE0"),
			Color.parseColor("#FFFF00"), Color.parseColor("#808000"),
			Color.parseColor("#BDB76B"), Color.parseColor("#FFFACD"),
			Color.parseColor("#EEE8AA"), Color.parseColor("#F0E68C"),
			Color.parseColor("#FFD700"), Color.parseColor("#FFF8DC"),
			Color.parseColor("#DAA520"), Color.parseColor("#FFFAF0"),
			Color.parseColor("#FDF5E6"), Color.parseColor("#F5DEB3"),
			Color.parseColor("#FFE4B5"), Color.parseColor("#FFA500"),
			Color.parseColor("#FFEFD5"), Color.parseColor("#FFEBCD"),
			Color.parseColor("#FFDEAD"), Color.parseColor("#FAEBD7"),
			Color.parseColor("#D2B48C"), Color.parseColor("#DEB887"),
			Color.parseColor("#FFE4C4"), Color.parseColor("#FF8C00"),
			Color.parseColor("#FAF0E6"), Color.parseColor("#CD853F"),
			Color.parseColor("#FFDAB9"), Color.parseColor("#F4A460"),
			Color.parseColor("#D2691E"), Color.parseColor("#8B4513"),
			Color.parseColor("#FFF5EE"), Color.parseColor("#A0522D"),
			Color.parseColor("#FFA07A"), Color.parseColor("#FF7F50"),
			Color.parseColor("#FF4500"), Color.parseColor("#E9967A"),
			Color.parseColor("#FF6347"), Color.parseColor("#FFE4E1"),
			Color.parseColor("#FA8072"), Color.parseColor("#FFFAFA"),
			Color.parseColor("#F08080"), Color.parseColor("#BC8F8F"),
			Color.parseColor("#CD5C5C"), Color.parseColor("#FF0000"),
			Color.parseColor("#A52A2A"), Color.parseColor("#B22222"),
			Color.parseColor("#8B0000"), Color.parseColor("#800000"),
			Color.parseColor("#FFFFFF"), Color.parseColor("#F5F5F5"),
			Color.parseColor("#DCDCDC"), Color.parseColor("#D3D3D3"),
			Color.parseColor("#C0C0C0"), Color.parseColor("#A9A9A9"),
			Color.parseColor("#808080"), Color.parseColor("#696969"),
			Color.parseColor("#000000") };

	public static Paint getPaint(int paintStyle) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
