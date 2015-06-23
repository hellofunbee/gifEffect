package com.example.picgif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.ant.liao.GifDecoder;
import com.example.adomin.Mapp;
import com.example.adomin.Point;
import com.example.paints.Paints;
import com.example.paints.Pens;
import com.example.png2gif.AnimatedGifEncoder;
import com.example.png2gif.PreforJpgToGif;
import com.example.utils.BS;
import com.example.utils.GetColor;
import com.example.utils.PicToPic;
import com.example.utils.Sleep;
import com.example.utils.SystemUtils;

public class PicGifDraw extends ImageView {

	// 静态变量，
	private static final int LINE = 1;
	private static final int BITMAP = 2;
	private static final int PAINT_NONE = -1;
	private static final int PAINT_HASE_STYLE = 1;
	private static final int NOMAL_TOUCH = 1;
	private static final int READ_IN = 2;
	// 控制变量
	public static final int HAVETOADD = 0;
	private static final int TWO = 2;
	private static final int ONE = 1;

	private int what = 1; // 画点，线，图，？
	private float strokeWidth = 3; // 点与点之间的距离
	private long handler_delay = 10; // 预览延时
	private Paint mPaint; // 画笔（大小，颜色，等设置）
	private Bitmap bitmap; // draw Bitmap
	private int bitmapStyle; // draw Bitmap style
	private int linepainStyle;// draw point style
	private int pointColor;// draw point style
	private int pointD; // 画点的半径
	private Bitmap pointBm;

	public void setWhat(int what) {
		isgif = false;
		this.what = what;
		if (what == LINE) {
			setPointBm(Color.WHITE, 10, null);
			pointColor = Color.WHITE;
			pointD = 10;
		} else {
			if (app.map == null) {
				app.map = new HashMap<Integer, Bitmap>();
			}
			this.bitmapStyle = Pens.getPen_bitmap()[1];
			app.map.put(bitmapStyle, null);
			this.bitmap = app.getBitmap(bitmapStyle);

		}
	}

	public void setPointBm(int color, int d, ImageView iv_show) {
		isgif = false;
		this.what = LINE;
		pointBm = Bitmap.createBitmap(d, d, Config.ARGB_8888);
		Canvas c = new Canvas(pointBm);
		pointColor = Color.parseColor(GetColor.argbToRgb(color));
		if (color == 0) {
			c.drawCircle(d / 2, d / 2, d / 2, Paints.getPaint(Color.BLACK, d));
			pointColor = Color.BLACK;
		} else
			c.drawCircle(d / 2, d / 2, d / 2, Paints.getPaint(pointColor, d));
		pointD = d;

		mPaint = Paints.getPaint(255, pointColor, pointD);
		if (iv_show != null) {

			iv_show.setImageBitmap(pointBm);
		}

	}

	// 一般变量
	private boolean isUp;
	private boolean readin;
	private boolean isTouched;
	private int x, x1;
	private int y, y1;
	private List<Point> pointList;
	public List<Point> allpointList;
	private Bitmap tempbm;
	private Mapp app;
	private boolean isgif;

	public void setBitmapStyle(int bitmapStyle) {
		this.bitmapStyle = bitmapStyle;
		this.bitmap = app.getBitmap(bitmapStyle);
		this.what = BITMAP;
		isUp = true;
		isgif = false;
	}

	public void setBitmapStyle(int bitmapStyle, Context c) {
		if (app.map == null) {
			app.map = new HashMap<Integer, Bitmap>();
		}
		app.map.put(bitmapStyle, null);
		this.bitmapStyle = bitmapStyle;
		this.bitmap = app.getBitmap(bitmapStyle);
		this.what = BITMAP;
		isgif = false;

	}

	public void setGifStyle(int gifId, Context c) {
		pointList.clear();
		gd = new GifDecoder(gifId, app);
		this.bitmap = BS.small(gd.getImage(0), app.scale);
		this.bitmapStyle = gifId;
		this.what = BITMAP;
		this.isgif = true;

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 10) {
				save(frameNum, gifFrames);
				return;
			}
			invalidate();

		};
	};

	public PicGifDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.app = (Mapp) context.getApplicationContext();
		bootUp();
	}

	public PicGifDraw(Context context) {
		super(context);
		this.app = (Mapp) context.getApplicationContext();
		bootUp();
	}

	private void bootUp() {
		pointList = new ArrayList<Point>();
		allpointList = new ArrayList<Point>();

		mPaint = Paints.getPaint(255, Color.RED, 20);
		linepainStyle = 1;

		setPointBm(Color.WHITE, 10, null);
		pointColor = Color.WHITE;
		pointD = 10;

		bitmap = BitmapFactory.decodeResource(getResources(),
				Pens.getPen_bitmap()[1]);

		m = new Matrix();
		if (SystemUtils.getSystemVersion() >= SystemUtils.V4_0) {
			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	private Point p;
	private Point w = new Point();
	private int i, j, myListsize, width, height;
	private boolean save, exist;

	@Override
	protected void onDraw(Canvas canvas) {
		if (mbm != null) {
			canvas.drawBitmap(mbm, m, mPaint);
		}
		if (!save) {

			p = new Point();
			p.bitmapStyle = -10;
			p.pointColor = -10;
			i = 0;
			j = 1;
			for (; i < pointList.size(); i++, j = i + 1) {
				w = pointList.get(i);
				mOndraw(canvas);
			}

			if (readin && w.last) {
				w.last = false;
				resumeView(READ_IN);
				readin = false;
				p = new Point();
				p.bitmapStyle = -10;
				p.pointColor = -10;
			}

		}

		if (isUp) {
			isUp = false;
			if (!isgif) {
				resumeView(NOMAL_TOUCH);
			} else {
				resumeView(2);

			}
		}

	}

	private void mOndraw(Canvas canvas) {
		switch (w.drawWhat) {
		case LINE:
			if (readin) {
				if (w.pointColor == p.pointColor && w.pointD == p.pointD) {

				} else {
					setPointBm(w.pointColor, w.pointD, null);
					p.pointColor = w.pointColor;
					p.pointD = w.pointD;
				}
				canvas.drawBitmap(pointBm, w.x - pointBm.getWidth() / 2, w.y
						- pointBm.getHeight() / 2, mPaint);
			} else {
				canvas.drawBitmap(pointBm, w.x - pointBm.getWidth() / 2, w.y
						- pointBm.getHeight() / 2, mPaint);
			}
			if (j < pointList.size()) {

				Point w1 = pointList.get(j);
				if (w1.drawWhat == 2) {
					if (readin) {
						bitmap = app.getBitmap(w1.bitmapStyle);
						p.bitmapStyle = w.bitmapStyle;

					}
					canvas.drawBitmap(bitmap, w1.x - bitmap.getWidth() / 2,
							w1.y - bitmap.getHeight() / 2, mPaint);

					break;
				}
				if (w1.paintStyle != PAINT_NONE) {
					if (readin) {
						if (w1.pointColor == p.pointColor
								&& w1.pointD == p.pointD) {

						} else {
							mPaint = Paints.getPaint(255, w1.pointColor,
									w1.pointD);
							p.pointColor = w1.pointColor;
							p.pointD = w1.pointD;
						}
						canvas.drawBitmap(pointBm, w1.x - pointBm.getWidth()
								/ 2, w1.y - pointBm.getHeight() / 2, mPaint);
						canvas.drawLine(w.x, w.y, w1.x, w1.y, mPaint);
					} else {
						canvas.drawLine(w.x, w.y, w1.x, w1.y, mPaint);
					}
				}
			}
			break;
		case BITMAP:

			if (readin) {
				if (w.bitmapStyle == p.bitmapStyle) {

				} else {
					// bitmap = BitmapFactory.decodeResource(getResources(),
					// w.bitmapStyle);
					bitmap = app.getBitmap(w.bitmapStyle);
					p.bitmapStyle = w.bitmapStyle;
				}
				canvas.drawBitmap(bitmap, w.x - bitmap.getWidth() / 2, w.y
						- bitmap.getHeight() / 2, mPaint);
			} else {
				canvas.drawBitmap(bitmap, w.x - bitmap.getWidth() / 2, w.y
						- bitmap.getHeight() / 2, mPaint);
			}
			break;
		}
	}

	@SuppressLint("NewApi")
	public void resumeView(int mode) {
		tempbm = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		c = new Canvas(tempbm);
		this.draw(c);
		mbm = tempbm;
		m = new Matrix();
		if (mode == 1) {
			allpointList.addAll(pointList);
			pointList.clear();
		} else {
			if (readin) {
				pointList.clear();
				return;
			}
			if (isgif) {
				if (app.gifList == null) {
					app.gifList = new ArrayList<GifDecoder>();
				}
				if (app.gifList.size() > 0) {
					exist = false;
					for (GifDecoder gd : app.gifList) {
						if (gd.points.size() == 0) {
							pointList.clear();
							return;
						}
						if (gd.points.get(0).bitmapStyle == bitmapStyle) {
							for (Point p : pointList) {
								Point point = new Point();
								point.x = (int) (p.x / app.scale);
								point.y = (int) (p.y / app.scale);
								gd.points.add(point);
							}
							exist = true;
							break;
						}
					}
				}
				if (!exist || app.gifList.size() == 0) {
					gd = new GifDecoder(bitmapStyle, app);

					for (Point p : pointList) {
						Point point = new Point();
						point.x = (int) (p.x / app.scale);
						point.y = (int) (p.y / app.scale);
						gd.points.add(point);
					}
					app.gifList.add(gd);
				}
			}
			pointList.clear();
		}
	}

	private Canvas c;
	AnimatedGifEncoder e;
	private int frameNum;
	private Bitmap demobm;
	private int mode;
	private int threadNum;
	private int showDelay;

	public void save(AnimatedGifEncoder e, int threadNum) {
		this.threadNum = threadNum;
		this.e = e;
		format();
		new Thread(new Runnable() {
			@Override
			public void run() {
				readinAndSave();
			}
		}).start();

	}

	public void show() {
		if ((app.gifList == null || app.gifList.size() == 0)
				&& allpointList.size() == 0) {
			app.run = false;
			return;
		} else {
			format();
			showDelay = app.sp.getInt("showdelay", 50);
			handler_delay = (showDelay - 40) > 0 ? showDelay - 40 : 1;
			readList();
		}
	}

	public void clear(Bitmap showbm) {
		if (showbm != null) {
			this.demobm = Bitmap.createBitmap(showbm);
			mbm = showbm;
			m = new Matrix();
			m.postScale(app.scale, app.scale);
			pointList.clear();
			invalidate();

		} else {
			mbm = Bitmap.createBitmap(app.w, app.h, Config.ARGB_8888);
			m = new Matrix();
			m.postScale(app.scale, app.scale);
		}
	}

	public void delete() {
		if (!readin) {
			app.gifList = null;
			readin = false;
			pointList.clear();
			allpointList.clear();
			clear(null);
			mbm = null;
			app.picGif.alterbm = Bitmap.createBitmap(app.w, app.h,
					Config.ARGB_8888);
			if (app.map != null)
				app.map.clear();
			invalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (app.run)
			return true;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			x = (int) event.getX();
			y = (int) event.getY();
			isTouched = true;

			if (what == LINE)
				lineInit(x, y, PAINT_NONE, pointD, pointColor, LINE);
			mode = ONE;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if (what == LINE) {
				lineInit(x, y, linepainStyle, pointD, pointColor, LINE);

			}
			mode = TWO;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = ONE;
		case MotionEvent.ACTION_MOVE:

			if (mode == ONE) {
				x = (int) event.getX();
				y = (int) event.getY();
				if (what == LINE) {
					lineInit(x, y, linepainStyle, pointD, pointColor, LINE);
				}
				if (what == BITMAP) {
					bimapInit(x, y, BITMAP, bitmapStyle);
				}
				isTouched = true;
			} else if (mode == TWO) {
				x = (int) event.getX(0);
				y = (int) event.getY(0);
				lineInit(x, y, linepainStyle, pointD, pointColor, LINE);
				x1 = (int) event.getX(1);
				y1 = (int) event.getY(1);
				lineInit(x1, y1, linepainStyle, pointD, pointColor, LINE);
			}
			break;
		case MotionEvent.ACTION_UP:
			x = (int) event.getX();
			y = (int) event.getY();

			if (what == BITMAP)
				bimapInit(x, y, BITMAP, bitmapStyle);
			isUp = true;
			invalidate();
		}
		return true;
	}

	public boolean isTouched() {
		return isTouched;
	}

	private void lineInit(float x, float y, int paintStyle, int pointD,
			int pointColor, int drawWhat) {
		if (pointList.size() == 0) {
			lineAdd(x, y, paintStyle, pointD, pointColor, drawWhat);
		} else {
			Point w = pointList.get(pointList.size() - 1);
			if (Math.abs(w.x - x) >= strokeWidth
					|| Math.abs(w.y - y) >= strokeWidth) {
				lineAdd(x, y, paintStyle, pointD, pointColor, drawWhat);
			}
		}

	}

	private void lineAdd(float x, float y, int paintStyle, int pointD,
			int pointColor, int drawWhat) {
		Point w = new Point();
		w.x = (int) x;
		w.y = (int) y;
		w.drawWhat = drawWhat;
		if (paintStyle == PAINT_HASE_STYLE) {
			w.paintStyle = paintStyle;
			w.pointColor = pointColor;
			w.pointD = pointD;
		} else {
			w.paintStyle = PAINT_NONE;
			w.pointColor = pointColor;
			w.pointD = pointD;
		}
		pointList.add(w);
		handler.sendEmptyMessageDelayed(0, 0);
	}

	public void bimapInit(float x, float y, int drawWhat, int bitmapStyle) {
		if (pointList.size() == 0) {
			addBitmap(x, y, drawWhat, bitmapStyle);
		} else {
			Point w = pointList.get(pointList.size() - 1);
			if (Math.abs(w.x - x) >= bitmap.getWidth()
					|| Math.abs(w.y - y) >= bitmap.getHeight()) {
				addBitmap(x, y, drawWhat, bitmapStyle);
			}
		}
	}

	private void addBitmap(float x, float y, int drawWhat, int bitmapStyle) {
		Point w = new Point();
		w.x = (int) x;
		w.y = (int) y;
		w.bitmapStyle = bitmapStyle;
		w.drawWhat = drawWhat;
		pointList.add(w);
		handler.sendEmptyMessageDelayed(0, 0);
	}

	Bitmap screenshot;

	Bitmap bmToSave;
	Object[] objs;
	int a, k, t, frames;
	int stroke = 5;

	Object o = new Object();
	private int gifFrames;
	private boolean haveToAdd;

	private void readinAndSave() {

		k = a = t = frames = gifFrames = frameNum = 0;

		haveToAdd = false;
		save = true;
		readin = true;
		try {
			if (app.gifList != null) {
				for (GifDecoder gd : app.gifList) {
					int count = gd.getFrameCount();
					gifFrames = count > gifFrames ? count : gifFrames;
				}
			}

			if (allpointList.size() == 0) {

				if (mbm != null) {
					bmToSave = mbm;
				} else {
					bmToSave = Bitmap.createBitmap(app.w, app.h,
							Config.ARGB_8888);
				}
				save(0, gifFrames);
				return;
			}

			int size = allpointList.size();
			resetStroke(size);

			if (size % stroke != 0) {
				frames = size / stroke + 1;
			} else {
				frames = size / stroke;
			}
			objs = new Object[frames + 1];

			if (frames < gifFrames) {
				haveToAdd = true;
			}

			screenshot = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas c = new Canvas(screenshot);
			m = new Matrix();
			m.postScale(app.scale, app.scale);
			c.drawBitmap(demobm, m, mPaint);

			for (int mi = 0; mi < size;) {
				pointList.add(allpointList.get(mi));
				mi++;
				p = new Point();
				p.bitmapStyle = -10;
				p.pointColor = -10;

				i = 0;
				j = 1;
				for (; i < pointList.size(); i++, j = i + 1) {
					w = pointList.get(i);
					mOndraw(c);
				}

				if (mi % stroke == 0 || mi == allpointList.size()) {

					bmToSave = Bitmap.createScaledBitmap(screenshot, app.w,
							app.h, true);
					if (app.gifList != null) {
						for (GifDecoder gd : app.gifList) {
							Bitmap temp = gd.getFrameImage(frameNum);
							for (Point p : gd.points) {
								bmToSave = PicToPic.picToPic(temp, bmToSave,
										p.x, p.y);
							}
						}
						frameNum++;
					}
					mbm = bmToSave;
					m = new Matrix();
					m.postScale(app.scale, app.scale);
					handler.sendEmptyMessageDelayed(0, 0);

					for (; t < frames;) {
						if (a < threadNum) {
							a++;
							MT mt = new MT();
							mt.setName(String.valueOf(t));
							mt.start();
							t++;
							break;
						} else {
							Sleep.sleep(10);
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (e != null) {
				e.finish();
			}
			save = false;
			app.run = false;
		}

	}

	// ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((
	private void resetStroke(int size) {
		int p = 0, b = 0;
		for (Point mp : allpointList) {
			p += mp.drawWhat == LINE ? 1 : 0;
			b += mp.drawWhat == BITMAP ? 1 : 0;
		}

		if (size < 10) {
			stroke = 1;
			return;
		}
	}

	class MT extends Thread {

		@Override
		public void run() {
			objs[Integer.parseInt(Thread.currentThread().getName())] = new PreforJpgToGif(
					120, 0).addFrame(bmToSave, app, false);

			synchronized (o) {
				for (; k < frames;) {
					if (objs[k] != null) {
						Object[] obj = (Object[]) objs[k];
						e.writeIn((byte[]) obj[0], (byte[]) obj[1],
								(Integer) obj[2], app.w, app.h, 0, 0);
						objs[k] = null;
						k++;
					} else {
						break;
					}
				}
				if (k == frames) {
					if (haveToAdd) {
						handler.sendEmptyMessage(10);
						return;
					} else {
						if (isgif) {
							setGifStyle(bitmapStyle, app);
						}
						save = false;
						readin = false;
						pointList.clear();
						app.run = false;
						// app.gifList = null;
						e.finish();
						e = null;
						k = 0;
						t = 0;
						app.picGif.showWhatWeGot();
					}
				}
			}
			a--;
		}
	}

	// ******************************************************************
	public void save(final int startFrame, final int endFrame) {
		final Object o = new Object();
		new Thread(new Runnable() {
			int a = 0, j = 0;
			Object[] objs;
			boolean lock;

			@Override
			public void run() {
				lock = true;
				frames = endFrame - startFrame;
				j = startFrame;
				objs = new Object[gifFrames];
				for (int i = startFrame; i < endFrame;) {
					if (a < threadNum) {
						a++;
						MThread mt = new MThread();
						mt.setName(String.valueOf(i));
						mt.start();
						i++;
					}
					Sleep.sleep(50);
				}
			}

			class MThread extends Thread {
				@Override
				public void run() {
					toGif();
					a--;
				}
			}

			private void toGif() {
				objs[Integer.parseInt(Thread.currentThread().getName())] = new PreforJpgToGif(
						100, 0).addFrame(demobm2, app, true);
				synchronized (o) {
					for (; j < gifFrames;) {
						if (objs[j] != null) {
							Object[] obj = (Object[]) objs[j];
							e.writeIn((byte[]) obj[0], (byte[]) obj[1],
									(Integer) obj[2], app.w, app.h, 0, 0);
							objs[j] = null;
							j++;
						} else {
							break;
						}
					}
					if (j == gifFrames && lock) {
						if (isgif) {
							setGifStyle(bitmapStyle, app);
						}
						lock = false;
						save = false;
						readin = false;
						pointList.clear();
						app.run = false;
						// app.gifList = null;
						e.finish();
						e = null;
						k = 0;
						t = 0;
						app.picGif.showWhatWeGot();

					}
				}
			}
		}).start();
	}

	private void format() {

		demobm2 = Bitmap.createBitmap(demobm);
		if (allpointList.size() != 0) {

			Bitmap screen = Bitmap
					.createBitmap(width, height, Config.ARGB_8888);
			c = new Canvas(screen);
			p = new Point();
			p.bitmapStyle = -10;
			p.pointColor = -10;
			i = 0;
			j = 1;
			readin = true;
			pointList.addAll(allpointList);
			for (; i < pointList.size(); i++, j = i + 1) {
				w = pointList.get(i);
				mOndraw(c);
			}
			pointList.clear();

			readin = false;
			c = new Canvas(demobm2);
			Matrix m = new Matrix();
			m.postScale(1 / app.scale, 1 / app.scale);

			c.drawBitmap(screen, m, mPaint);
			m = null;
		}
	}

	// *************************************************************************
	private void readList() {

		readin = true;
		new Thread(new Runnable() {
			private Bitmap bm;

			@Override
			public void run() {
				myListsize = allpointList.size();
				gifFrames = frames = frameNum = 0;
				haveToAdd = false;
				if (app.gifList != null) {
					for (GifDecoder gd : app.gifList) {
						int count = gd.getFrameCount();
						gifFrames = count > gifFrames ? count : gifFrames;
					}
				}

				if (allpointList.size() == 0) {

					if (mbm == null) {
						mbm = Bitmap.createBitmap(app.w, app.h,
								Config.ARGB_8888);
					}
					showIt(0, gifFrames);
					return;
				}
				int size = allpointList.size();

				if (size % stroke != 0) {
					frames = size / stroke + 1;
				} else {
					frames = size / stroke;
				}
				objs = new Object[frames + 1];

				if (frames < gifFrames) {
					haveToAdd = true;
				}
				Point point;
				for (int i = 0; i < myListsize;) {
					point = allpointList.get(i);
					if (i == myListsize - 1) {
						point.last = true;
					}
					i++;
					Sleep.sleep(handler_delay);
					pointList.add(point);
					if (i % stroke == 0 || i == allpointList.size()) {
						bm = Bitmap.createBitmap(demobm);
						if (app.gifList != null) {
							for (GifDecoder gd : app.gifList) {
								Bitmap temp = gd.getFrameImage(frameNum);
								for (Point p : gd.points) {
									bm = PicToPic.picToPic(temp, bm, p.x, p.y);
								}
							}

							frameNum++;
						}
						setMbm(bm);
					}
					handler.sendEmptyMessageDelayed(0, 0);

				}
				if (haveToAdd) {
					showIt(frameNum, gifFrames);
				} else {
					if (isgif) {
						setGifStyle(bitmapStyle, app);
					}
					app.run = false;
				}

			}

		}).start();

	}

	// *********************show++**********************************
	private void showIt(final int startFrame, final int endFrame) {

		new Thread(new Runnable() {
			private Bitmap bm;

			@Override
			public void run() {
				int t = startFrame;
				for (; t < endFrame; t++) {
					if (app.stopNow)
						return;

					bm = demobm2.copy(Config.ARGB_8888, true);
					for (GifDecoder gd : app.gifList) {

						Bitmap temp = gd.getFrameImage(t);
						for (Point p : gd.points) {
							bm = PicToPic.picToPic(temp, bm, p.x, p.y);
						}

					}
					setMbm(bm);
					Sleep.sleep(showDelay);
				}
				readin = false;
				app.run = false;
			}
		}).start();
	}

	// *******************************************************

	public void setMbm(Bitmap bm) {
		mbm = bm;
		m = new Matrix();
		m.postScale(app.scale, app.scale);
		handler.sendEmptyMessageDelayed(0, 0);
	}

	private Matrix m;
	private Bitmap mbm;

	private GifDecoder gd;
	private Bitmap demobm2;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		this.width = getWidth();
		this.height = getHeight();
		super.onLayout(changed, left, top, right, bottom);
	}

}
