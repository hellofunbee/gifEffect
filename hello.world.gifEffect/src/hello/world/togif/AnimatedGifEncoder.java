package hello.world.togif;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Currency;

import android.graphics.Bitmap;
import android.graphics.Color;

public class AnimatedGifEncoder {
	protected int transparent = 1;
	protected int delay = 0;
	protected int repeat = -1;
	protected boolean[] usedEntry;
	protected int palSize;
	protected boolean closeStream;
	protected int dispose;
	protected boolean firstFrame;
	protected int sample;
	protected int w;
	protected int h;
	protected boolean sizeSet;

	protected int colorDepth;
	protected OutputStream out;
	protected boolean started;
	protected int transIndex;

	public AnimatedGifEncoder() {
		boolean[] arrayOfBoolean = new boolean[256];
		this.usedEntry = arrayOfBoolean;
		this.palSize = 7;
		this.dispose = -1;
		this.closeStream = false;
		this.firstFrame = true;
		this.sizeSet = false;
		this.sample = 10;
	}

	public Object[] addFrame(Bitmap paramBitmap,int transparent) {
		this.transparent = transparent;
		if (paramBitmap == null || !started) {
			return null;
		}

		w = paramBitmap.getWidth();
		h = paramBitmap.getHeight();
		Bitmap image = Bitmap.createBitmap(paramBitmap);
		byte[] pixels = getImagePixels(image);

		Object[] obj = analyzePixels(pixels);

		synchronized (AnimatedGifEncoder.this) {
			return obj;
		}

	}

	public void writeIn(byte[] indexedPixels, byte[] colorTab,int transIndex,int w,int h,int sx,int sy) {
		this.w = w;
		this.h = h;
		
		try {
			if (firstFrame) {
				writeLSD();
				writePalette(colorTab);
				if (repeat >= 0)
					writeNetscapeExt();
			}
			writeGraphicCtrlExt(transIndex);
			writeImageDesc(sx,sy);

			if (!firstFrame)
				writePalette(colorTab);
			writePixels(indexedPixels);
			this.firstFrame = false;

			indexedPixels = null;
			colorTab = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		synchronized (AnimatedGifEncoder.this) {
			return new Object[] { indexedPixels, colorTab ,transIndex};
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

	public boolean finish() {
		boolean ok = false;
		try {
			if (!started)
				return false;
			ok = true;
			started = false;
			try {
				out.write(0x3b); // gif trailer
				out.flush();
				if (closeStream) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				ok = false;
			}

			transIndex = 0;
			out = null;

			closeStream = false;
			firstFrame = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ok;
	}

	/**
	 * 
	 * 配置图片信息（像素）
	 * 
	 * @param image
	 * @return
	 */
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

	public void setDelay(int ms) {
		delay = Math.round(ms / 10.0f);
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

	public void setQuality(int quality) {
		if (quality < 1)
			quality = 1;
		sample = quality;
	}

	public void setRepeat(int iter) {
		if (iter >= 0) {
			repeat = iter;
		}
	}

	public void setTransparent(int c) {
		this.transparent = c;
	}

	public boolean start(OutputStream os) {
		if (os == null)
			return false;
		boolean ok = true;
		closeStream = false;
		out = os;
		try {
			writeString("GIF89a"); // header
		} catch (IOException e) {
			ok = false;
		}
		return started = ok;
	}

	public boolean start(String file) {
		boolean ok = true;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			ok = start(out);
			closeStream = true;
		} catch (IOException e) {
			ok = false;
		}
		return started = ok;
	}

	protected void writeGraphicCtrlExt(int transIndex) throws IOException {
		out.write(0x21); // extension introducer
		out.write(0xf9); // GCE label
		out.write(4); // data block size
		int transp, disp;
		if (transparent == 0) {
			transp = 0;
			disp = 0; // dispose = no action
		} else {
			transp = 1;
			disp = 2; // force clear if using transparent color
		}
		if (dispose >= 0) {
			disp = dispose & 7; // user override
		}
		disp <<= 2;

		// packed fields
		out.write(0 | // 1:3 reserved
				disp | // 4:6 disposal
				0 | // 7 user input - 0 = none
				transp); // 8 transparency flag

		writeShort(delay); // delay x 1/100 sec
		out.write(transIndex); // transparent color index
		out.write(0); // block terminator
	}
//4，Image Descriptor 结构
	protected void writeImageDesc(int sx,int sy) throws IOException {
		out.write(0x2c); // image separator
		writeShort(sx); // image position x,y = 0,0
		writeShort(sy);
		writeShort(w); // image size
		writeShort(h);
		// packed fields
		if (firstFrame) {
			// no LCT - GCT is used for first (or only) frame
			out.write(0);
		} else {
			// specify normal LCT
			out.write(0x80 | // 1 local color table 1=yes
					0 | // 2 interlace - 0=no
					0 | // 3 sorted - 0=no
					0 | // 4-5 reserved
					palSize); // 6-8 size of color table
		}
	}
	//2，Logical Screen Descriptor
	protected void writeLSD() throws IOException {
		// logical screen size
		writeShort(w);
		writeShort(h);
		// packed fields
		out.write((0x80 | // 1 : global color table flag = 1 (gct used)
		0x70 | // 2-4 : color resolution = 7
		0x00 | // 5 : gct sort flag = 0
		palSize)); // 6-8 : gct size

		out.write(Color.GRAY); // background color index
		out.write(0); // pixel aspect ratio - assume 1:1
	}

	protected void writeNetscapeExt() throws IOException {
		out.write(0x21); // extension introducer
		out.write(0xff); // app extension label
		out.write(11); // block size
		writeString("NETSCAPE" + "2.0"); // app id + auth code
		out.write(3); // sub-block size
		out.write(1); // loop sub-block id
		writeShort(repeat); // loop count (extra iterations, 0=repeat forever)
		out.write(0); // block terminator
	}
//3,  Global Color Table 结构：
	protected void writePalette(byte[] colorTab) throws IOException {
		out.write(colorTab, 0, colorTab.length);
		int n = (3 * 256) - colorTab.length;
		for (int i = 0; i < n; i++) {
			out.write(0);
		}
	}

	protected void writePixels(byte[] indexedPixels) throws IOException {
		LZWEncoder encoder = new LZWEncoder(w, h, indexedPixels, colorDepth);
		encoder.encode(out);
	}

	protected void writeShort(int value) throws IOException {
		out.write(value & 0xff);
		out.write((value >> 8) & 0xff);
	}

	protected void writeString(String s) throws IOException {
		for (int i = 0; i < s.length(); i++) {
			out.write((byte) s.charAt(i));
		}
	}

}
