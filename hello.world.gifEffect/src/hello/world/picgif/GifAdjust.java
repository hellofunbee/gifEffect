package hello.world.picgif;

import hello.world.decoder.GifDecoder;
import hello.world.domain.Mapp;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * @since 2015 06 24
 * @author funbee {@link https://github.com/hellofunbee/gifEffect}
 */
public class GifAdjust extends ImageView {
	private boolean notInitial = true;
	private GifDecoder gd;
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

	public GifAdjust(Context context, AttributeSet attrs) {
		super(context, attrs);
		mpaint = new Paint();
		m = new Matrix();
		ox = 360;
		oy = 640;
	}

	@Override
	public void setImageBitmap(Bitmap bitmap) {
		notInitial = false;
		invalidate();
	}

	public void setAddr(String path, Mapp app, int left, int top, int w, int h) {
		this.app = app;
		this.w = w;
		this.h = h;
		ox = app.scale * app.w / 2;
		oy = app.scale * app.h / 2;
		i = 0;
		gd = new GifDecoder(path, app);
		m.postScale(app.scale, app.scale);
		m.postTranslate(left * app.scale, top * app.scale);
		handler.sendEmptyMessageDelayed(0, 100);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (notInitial) {
			return;
		}
		canvas.drawBitmap(alterbm, m, mpaint);
		handler.sendEmptyMessageDelayed(0, 50);
	}

	int i;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (stop) {
				return;
			}
			alterbm = Bitmap.createScaledBitmap(gd.getFrameImage(i), w,
					h, true);
			setImageBitmap(alterbm);
			i++;
		}

	};
	private boolean stop;
	private float mscale = 1;
	private int mdegree;

	public void ok() {

		stop = true;
		if (app.gifList == null) {
			app.gifList = new ArrayList<GifDecoder>();
		}
		gd.reBorn(mdegree, w,h,(int)ox,(int)oy, mscale);

		app.gifList.add(gd);
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