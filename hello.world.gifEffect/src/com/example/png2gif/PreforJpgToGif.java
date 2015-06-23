package com.example.png2gif;

import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.ant.liao.GifDecoder;
import com.example.adomin.Mapp;
import com.example.adomin.Point;
import com.example.utils.PicToPic;

public class PreforJpgToGif {
	protected int transparent = 1;
	protected int delay = 0;
	protected int repeat = -1;
	protected boolean[] usedEntry;
	protected int palSize;
	protected int dispose;
	protected int sample;
	protected int w;
	protected int h;
	protected boolean sizeSet;

	protected int colorDepth;
	protected OutputStream out;
	protected int transIndex;

	public PreforJpgToGif(int delay, int transparent) {
		this.transparent = transparent;
		boolean[] arrayOfBoolean = new boolean[256];
		this.usedEntry = arrayOfBoolean;
		this.palSize = 7;
		this.dispose = -1;
		this.sizeSet = false;
		this.sample = 10;
		this.delay = Math.round(delay / 10.0f);
	}

	public Object[] addFrame(Bitmap paramBitmap, Mapp app, boolean show) {
		Bitmap image = Bitmap.createBitmap(paramBitmap);
		if (paramBitmap == null) {
			return null;
		}
		if (show) {
			
			synchronized (app) {
				if (app.gifList != null) {
					for (GifDecoder gd : app.gifList) {

						Bitmap temp = gd.getFrameImage(Integer.parseInt(Thread
								.currentThread().getName()));
						for (Point p : gd.points) {
							image = PicToPic.picToPic(temp, image, p.x, p.y);
						}
					}
				}
				if (app.drawPic != null) {
					app.drawPic.uiShow(image);
				}
				if (app.picGif != null) {
					app.picGif.uiShow(image);

				}
			}
		}
		w = image.getWidth();
		h = image.getHeight();
		byte[] pixels = getImagePixels(image);

		Object[] obj = analyzePixels(pixels);
		return obj;
	}

	protected Object[] analyzePixels(byte[] pixels) {
		int len = pixels.length;

		byte[] indexedPixels = new byte[len / 3];

		int k = this.sample;

		NeuQuant nq = new NeuQuant(pixels, len, k);

		byte[] colorTab = nq.process();

		int l = 0;
		int i1 = colorTab.length;
		Object localObject;
		if (l >= i1) {
			l = 0;
			localObject = null;
		}

		for (int i = 0; i < colorTab.length; i += 3) {
			byte temp = colorTab[i];
			colorTab[i] = colorTab[i + 2];
			colorTab[i + 2] = temp;
			usedEntry[i / 3] = false;
		}
		int k1 = 0;

		for (int i = 0; i < len / 3; i++) {
			int index = nq.map(pixels[k1++] & 0xff, pixels[k1++] & 0xff,
					pixels[k1++] & 0xff);
			usedEntry[index] = true;
			indexedPixels[i] = (byte) index;
		}

		pixels = null;
		colorDepth = 8;
		palSize = 7;
		if (transparent != 0) {
			transIndex = findClosest(transparent, colorTab);
		}
		synchronized (PreforJpgToGif.this) {
			return new Object[] { indexedPixels, colorTab, transIndex };
		}

	}

	protected int findClosest(int paramInt, byte[] colorTab) {

		if (colorTab == null) {
			return -1;
		}
		int r = Color.red(paramInt);
		int g = Color.green(paramInt);
		int b = Color.blue(paramInt);
		int minpos = 0;
		int dmin = 256 * 256 * 256;
		int len = colorTab.length;

		for (int i = 0; i < len;) {
			int dr = r - (colorTab[i++] & 0xff);
			int dg = g - (colorTab[i++] & 0xff);
			int db = b - (colorTab[i] & 0xff);
			int d = dr * dr + dg * dg + db * db;
			int index = i / 3;
			if (usedEntry[index] && (d < dmin)) {
				dmin = d;
				minpos = index;
			}
			i++;
		}
		return minpos;
	}

	protected byte[] getImagePixels(Bitmap image) {

		byte[] pixels = new byte[w * h * 3];
		int[] arrayOfInt = new int[w * h];
		image.getPixels(arrayOfInt, 0, w, 0, 0, w, h);
		image = null;
		int pixIndex = 0;

		while (true) {

			if (pixIndex >= arrayOfInt.length)
				return pixels;

			pixels[pixIndex * 3] = (byte) Color.blue(arrayOfInt[pixIndex]);

			pixels[pixIndex * 3 + 1] = (byte) Color.green(arrayOfInt[pixIndex]);
			pixels[pixIndex * 3 + 2] = (byte) Color.red(arrayOfInt[pixIndex]);
			++pixIndex;
		}
	}

	public void setDispose(int code) {
		if (code >= 0) {
			dispose = code;
		}
	}

	public void setFrameRate(float fps) {
		if (fps != 0f) {
			delay = Math.round(100f / fps);
		}
	}

}
