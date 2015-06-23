package hello.world.mylayout;

import hello.world.domain.Mapp;
import hello.world.picgif.PicGifDraw;
import hello.world.utils.BS;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;


public class PicAdjust_Bg extends ImageView {
	private boolean notInitial = true;
	private Mapp app;
	private int h, w;
	private Bitmap alterbm;
	private Matrix m;

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;
	private float oriDis = 1f;
	private float ox, oy;
	private Paint mpaint;
	private int sx, sy, nx, ny;
	private float oldD;
	private float dd;
	private float ds;
	private float oldS = 100;
	private int dw;
	private int dh;

	public PicAdjust_Bg(Context context, AttributeSet attrs) {
		super(context, attrs);
		mpaint = new Paint();
		m = new Matrix();
	}

	public void setBitmap(Mapp app, int dw, int dh, int w, int h, Bitmap bm) {
		this.alterbm = bm;
		this.app = app;
		this.w = w;
		this.h = h;
		this.dw = dw;
		this.dh = dh;
		ox = dw / 2;
		oy = dh / 2;
		m.postTranslate((dw - w) / 2, (dh - h) / 2);
		notInitial = false;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (notInitial) {
			return;
		}
		canvas.drawBitmap(alterbm, m, mpaint);
	}

	private float mscale = 1;
	private int mdegree;

	public Bitmap ok() {
		w = (int) (w * mscale);
		h = (int) (h * mscale);
		Bitmap bg = Bitmap.createBitmap(dw, dh, Config.ARGB_8888);
		Canvas c = new Canvas(bg);
		this.draw(c);
		return Bitmap.createScaledBitmap(bg, app.w, app.h, true);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			sx = (int) event.getX();
			sy = (int) event.getY();
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oriDis = distance(event);
			if (oriDis > 10f) {
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				nx = (int) event.getX();
				ny = (int) event.getY();
				int dx = nx - sx;
				int dy = ny - sy;
				ox += dx;
				oy += dy;
				m.postTranslate(dx, dy);
				sx = nx;
				sy = ny;
				invalidate();
			} else if (mode == ZOOM) {
				float newDist = distance(event);
				if (newDist > 10f) {
					float scale = newDist / oriDis;
					mscale = scale * mscale;
					m.postScale(scale, scale, ox, oy);
					oriDis = newDist;
					invalidate();
				}
			}
			break;
		}
		return true;
	}

	private float distance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	public void setScale(float scale) {
		ds = scale / oldS;
		mscale = ds * mscale;
		m.postScale(ds, ds, ox, oy);
		oldS = scale;
		invalidate();
	}

	public void setRot(int degree) {
		mdegree = degree;
		dd = degree - oldD;
		m.postRotate(dd, ox, oy);
		oldD = degree;
		invalidate();
	}

}