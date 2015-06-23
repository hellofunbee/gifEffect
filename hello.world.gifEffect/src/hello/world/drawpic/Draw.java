package hello.world.drawpic;

import hello.world.decoder.GifDecoder;
import hello.world.domain.Mapp;
import hello.world.domain.Point;
import hello.world.domain.myInts;
import hello.world.paints.Paints;
import hello.world.paints.Pens;
import hello.world.togif.AnimatedGifEncoder;
import hello.world.togif.PreforJpgToGif;
import hello.world.utils.BS;
import hello.world.utils.GetColor;
import hello.world.utils.PicToPic;
import hello.world.utils.Sleep;
import hello.world.utils.SystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Draw extends ImageView {

	// 静态变量，
	private static final String sd = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	File filePath = new File(Environment.getExternalStorageDirectory(),
			"/mtemp");

	private static final int LINE = 1;

	private static final int BITMAP = 2;
	private static final int PAINT_NONE = -1;
	private static final int PAINT_HASE_STYLE = 1;
	// 控制变量
	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int NOPOINT = 4;

	private int what = 1; // 画点，线，图，？
	private float strokeWidth = 3; // 点与点之间的距离
	private long HANDLER_DELAY = 10; // 预览延时
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
			setPointBm(Color.WHITE, app.drawPic.md, null);
		} else {
			this.bitmapStyle = Pens.getPen_bitmap()[1];
			this.bitmap = BitmapFactory.decodeResource(getResources(),
					bitmapStyle);
		}
	}

	public void setPointBm(int color, int d, ImageView iv_show) {
		isgif = false;
		this.what = LINE;
		pointBm = Bitmap.createBitmap(d, d, Config.ARGB_8888);
		Canvas c = new Canvas(pointBm);
		pointColor = Color.parseColor(GetColor.argbToRgb(color));
		if (color == 0) {
			this.what = 3;
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
	private myInts ints;
	private List<Point> pointList;
	private List<Point> myList;
	public List<Point> allpointList;
	private Bitmap tempbm;
	private Mapp app;
	private boolean isgif;

	public void setBitmapStyle(int bitmapStyle, Context c) {
		this.bitmapStyle = bitmapStyle;
		this.bitmap = BitmapFactory.decodeResource(getResources(), bitmapStyle);
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
			if (msg.what == NOPOINT) {
				Toast.makeText(app, "make some draw,or add a picture first ", 0)
						.show();
				return;
			}
			invalidate();

		};
	};
	private int width;
	private int height;

	public Draw(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.app = (Mapp) context.getApplicationContext();
		bootUp();
	}

	public Draw(Context context) {
		super(context);
		this.app = (Mapp) context.getApplicationContext();
		bootUp();
	}

	private void bootUp() {
		ints = new myInts();
		pointList = new ArrayList<Point>();
		allpointList = new ArrayList<Point>();

		mPaint = Paints.getPaint(233, Color.RED, 20);
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

	Point p;
	private int myListsize;
	int i;
	int j;
	private boolean save;
	private boolean exist;

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
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
		}

		if (readin && w.last) {
			w.last = false;
			resumeView();
			readin = false;

			p = new Point();
			p.bitmapStyle = -100;
			p.pointColor = -100;
			app.run = false;
		}

		if (isUp) {
			isUp = false;
			resumeView();
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
						bitmap = BitmapFactory.decodeResource(getResources(),
								w1.bitmapStyle);
						p.bitmapStyle = w1.bitmapStyle;

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
					bitmap = BitmapFactory.decodeResource(getResources(),
							w.bitmapStyle);
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

	public void resumeView() {
		tempbm = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		c = new Canvas(tempbm);
		this.draw(c);
		mbm = tempbm;
		m = new Matrix();
		pointList.clear();
	}

	private Canvas c;

	AnimatedGifEncoder e;

	private int frameNum;
	private int mode;
	private int threadNum;

	public void save(AnimatedGifEncoder e, int frameNum, final Bitmap saveBm,
			int threadNum) {
		this.frameNum = frameNum;
		this.threadNum = threadNum;
		this.e = e;
		new Thread(new Runnable() {
			@Override
			public void run() {
				readinAndSave(saveBm);
			}
		}).start();

	}

	public void show(int frameNum, Bitmap showBm) {
		this.frameNum = frameNum;
		if (allpointList.size() > 0) {
			myListsize = allpointList.size();
			readList(showBm);
		} else {
			app.run = false;
			if (app.drawPic.temp == null || app.drawPic.temp.length == 0) {
				handler.sendEmptyMessage(NOPOINT);
			}
		}
	}

	public void clear(Bitmap bm) {
		mbm = bm;
		m = new Matrix();
		m.postScale(app.scale, app.scale);
		pointList.clear();
		invalidate();
	}

	public void delete() {
		if (!readin) {
			app.gifList = null;
			readin = false;
			myList = null;
			app.drawPic.backGround = null;
			app.drawPic.BackUpBm = null;
			pointList.clear();
			allpointList.clear();
			clear(Bitmap.createBitmap(app.w, app.h, Config.ARGB_4444));
			invalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (app.run) {
			return true;
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			x = (int) event.getX();
			y = (int) event.getY();
			isTouched = true;
			if (what == LINE) {
				lineInit(x, y, PAINT_NONE, pointD, pointColor, LINE);
			}
			mode = ONE;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			this.what = LINE;
			isgif = false;
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

			if (what == BITMAP) {
				bimapInit(x, y, BITMAP, bitmapStyle);
			}
			isUp = true;
			invalidate();
		}
		return true;
	}

	public boolean isTouched() {
		return isTouched;
	}

	/**
	 * 给画线设置属性 画笔
	 * 
	 * @param x
	 * @param y
	 * @param paintStyle
	 */
	private void lineInit(float x, float y, int paintStyle, int pointD,
			int pointColor, int drawWhat) {
		// TODO Auto-generated method stub
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

	/**
	 * 添加画线的位置
	 * 
	 * @param x
	 * @param y
	 * @param paintStyle
	 */
	private void lineAdd(float x, float y, int paintStyle, int pointD,
			int pointColor, int drawWhat) {
		// TODO Auto-generated method stub
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
		allpointList.add(w);
		handler.sendEmptyMessageDelayed(0, 0);
	}

	private void bimapInit(float x, float y, int drawWhat, int bitmapStyle) {
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
		if (!isgif) {
			allpointList.add(w);
		} else {
			if (app.gifList == null) {
				app.gifList = new ArrayList<GifDecoder>();

				gd = new GifDecoder(bitmapStyle, app);
				gd.points.add(new Point((int) (w.x / app.scale),
						(int) (w.y / app.scale), bitmapStyle));
				app.gifList.add(gd);
				return;
			}

			for (GifDecoder gd : app.gifList) {
				if (gd.points.get(0).bitmapStyle == bitmapStyle) {
					gd.points.add(new Point((int) (w.x / app.scale),
							(int) (w.y / app.scale), bitmapStyle));
					return;
				}
			}

			gd = new GifDecoder(bitmapStyle, app);
			gd.points.add(new Point((int) (w.x / app.scale),
					(int) (w.y / app.scale), bitmapStyle));
			app.gifList.add(gd);
		}

	}

	Bitmap screenshot;

	Bitmap bmToSave;
	Object[] objs;
	int a, k, t, frames;
	boolean first = true;
	int stroke = 5;

	Object o = new Object();

	private void readinAndSave(Bitmap saveBm) {
		save = true;
		readin = true;
		try {
			int size = allpointList.size();
			resetStroke(size);
			if (size % stroke != 0) {
				frames = size / stroke + 1;
			} else {
				frames = size / stroke;
			}
			objs = new Object[frames];

			screenshot = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas c = new Canvas(screenshot);
			Matrix m = new Matrix();
			m.postScale(app.scale, app.scale);
			c.drawBitmap(saveBm, m, mPaint);

			pointList.addAll(allpointList);
			p = new Point();
			p.bitmapStyle = -100;
			p.pointColor = -100;
			i = 0;
			j = 1;
			for (; i < size;) {
				w = pointList.get(i);
				mOndraw(c);
				i++;
				j = i + 1;
				if (i % stroke == 0 || i == size) {
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
					setImageBitmap(bmToSave);

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

			save = false;
			app.run = false;
		}

	}

	private void resetStroke(int size) {
		int p = 0, b = 0;
		for (Point mp : allpointList) {
			p += mp.drawWhat == LINE ? 1 : 0;
			b += mp.drawWhat == BITMAP ? 1 : 0;
		}

		if (size < 10) {
			stroke = 1;
			return;
		} else if (size < 20) {
			stroke = 2;
		} else if (size < 30) {
			stroke = 3;
		} else if (size < 40) {
			stroke = 4;
		} else if (size < 50) {
			stroke = 5;
		} else if (size < 100) {
			stroke = 6;
		} else if (size < 200) {
			stroke = 7;
		} else if (size < 300) {
			stroke = 10;
		} else if (size < 400) {
			stroke = 15;
		}
	}

	class MT extends Thread {
		private boolean lock = true;

		@Override
		public void run() {
			objs[Integer.parseInt(Thread.currentThread().getName())] = new PreforJpgToGif(
					150, 1).addFrame(bmToSave, app, false);

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

				if (k == frames && lock) {
					if (isgif) {
						setGifStyle(bitmapStyle, app);
					}
					lock = false;
					save = false;
					readin = false;
					pointList.clear();
					app.run = false;
					e.finish();
					e = null;
					k = 0;
					t = 0;
					app.drawPic.showWhatWeGot();
				}
			}
			a--;
		}
	}

	public void readin() {
		readin = true;
		try {
			ObjectInputStream oin = new ObjectInputStream(new FileInputStream(
					sd + "/a"));
			myInts ints = (myInts) oin.readObject();

			myList = ints.myList;
			myListsize = myList.size();
			new Thread(new Runnable() {

				@Override
				public void run() {

					Sleep.sleep(50);
					for (Point point : myList) {

						Sleep.sleep(HANDLER_DELAY);

						pointList.add(point);

						handler.sendEmptyMessageDelayed(0, 0);

					}

				}

			}).start();

			oin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void readList(final Bitmap showBm) {
		readin = true;

		myListsize = allpointList.size();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Sleep.sleep(50);
				Point point;
				for (int i = 0; i < myListsize;) {
					point = allpointList.get(i);
					if (i == myListsize - 1) {
						point.last = true;
					}
					i++;
					Sleep.sleep(HANDLER_DELAY);

					pointList.add(point);
					if (i % 5 == 0) {

						bmToSave = Bitmap.createBitmap(showBm);
						if (app.gifList != null) {
							for (GifDecoder gd : app.gifList) {
								Bitmap temp = gd.getFrameImage(frameNum);
								for (Point p : gd.points) {
									bmToSave = PicToPic.picToPic(temp,
											bmToSave, p.x, p.y);
								}
							}

							frameNum++;
						}
						setImageBitmap(bmToSave);
					}
					handler.sendEmptyMessageDelayed(0, 0);

				}

			}

		}).start();

	}

	/**
	 * 
	 * 向sd卡中写入数据
	 */
	public void writeIn() {

		ints.myList = allpointList;

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					FileOutputStream fos = new FileOutputStream(sd + "/a");
					ObjectOutputStream oos = new ObjectOutputStream(fos);

					oos.writeObject(ints);

					oos.flush();
					oos.close();
					allpointList.clear();
					myList.clear();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void setImageBitmap(Bitmap bm) {
		mbm = bm;
		m = new Matrix();
		m.postScale(app.scale, app.scale);
		handler.sendEmptyMessageDelayed(0, 0);
	}

	float dx, dy, bs, bmw, bmh;
	Matrix m;
	Bitmap mbm;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		this.width = getWidth();
		this.height = getHeight();
		super.onLayout(changed, left, top, right, bottom);
	}

	private Point w;
	private GifDecoder gd;

}
