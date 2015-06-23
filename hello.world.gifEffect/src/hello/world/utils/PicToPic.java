package hello.world.utils;

import hello.world.paints.Paints;
import android.graphics.Bitmap;
import android.graphics.Canvas;


public class PicToPic {
	public static Bitmap picToPic(Bitmap little, Bitmap big, float l, float t) {
		l = l - little.getWidth() / 2;
		t = t - little.getHeight() / 2;
		Canvas c = new Canvas(big);
		c.drawBitmap(little, l, t, Paints.getPaint());
		return big;

	}
}
