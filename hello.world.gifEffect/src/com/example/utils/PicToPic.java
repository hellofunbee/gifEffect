package com.example.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.paints.Paints;

public class PicToPic {
	public static Bitmap picToPic(Bitmap little, Bitmap big, float l, float t) {
		l = l - little.getWidth() / 2;
		t = t - little.getHeight() / 2;
		Canvas c = new Canvas(big);
		c.drawBitmap(little, l, t, Paints.getPaint());
		return big;

	}
}
