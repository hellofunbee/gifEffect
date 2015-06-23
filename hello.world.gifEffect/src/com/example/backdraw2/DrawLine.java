package com.example.backdraw2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.ant.liao.GifDecoder;
import com.example.adomin.Mapp;
import com.example.adomin.Point;
import com.example.adomin.myInts;
import com.example.paints.Paints;
import com.example.paints.Pens;
import com.example.png2gif.AnimatedGifEncoder;
import com.example.png2gif.PreforJpgToGif;
import com.example.utils.BS;
import com.example.utils.GetColor;
import com.example.utils.PicToPic;
import com.example.utils.Sleep;
import com.example.utils.SystemUtils;

public class DrawLine extends View {
	// 静态变量，
	private static final String sd = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	File filePath = new File(Environment.getExternalStorageDirectory(),
			"/mtemp");

	private static final int LINE = 1;

	private static final int BITMAP = 2;
	private static final int PAINT_NONE = -1;
	private static final int PAINT_HASE_STYLE = 1;
	private static final int NOMAL_TOUCH = 1;
	private static final int READ_IN = 2;
	// 控制变量
	private static final int ONE = 1;
	private static final int TWO = 2;

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
		this.what = what;
		if (what == LINE) {
			setPointBm(Color.WHITE, 10, null);
			pointColor = Color.WHITE;
			pointD = 10;
		} else {
			this.bitmapStyle = 0;
			this.bitmap = BitmapFactory.decodeResource(getResources(),
					Pens.getPen_bitmap()[1]);
		}
	}

	public void setPointBm(int color, int d, ImageView iv_show) {
		this.what = LINE;
		pointBm = Bitmap.createBitmap(d, d, Config.ARGB_4444);
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
	private myInts ints;
	private List<Point> pointList;
	public List<Point> allpointList;
	private Bitmap tempbm;
	private Mapp app;

	public void setBitmapStyle(int bitmapStyle, Context c) {
		this.bitmapStyle = bitmapStyle;
		this.bitmap = BitmapFactory.decodeResource(getResources(), bitmapStyle);
		this.what = BITMAP;

	}

	public void setGifStyle(int gifId, Context c) {
		pointList.clear();
		gd = new GifDecoder(gifId, app);
		this.bitmap = BS.small(gd.getImage(0), app.scale);
		this.bitmapStyle = gifId;
		this.what = BITMAP;

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			invalidate();

		};
	};
	private int width;
	private int height;

	public DrawLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.app = (Mapp) context.getApplicationContext();
		bootUp();
	}

	public DrawLine(Context context) {
		super(context);
		this.app = (Mapp) context.getApplicationContext();
		bootUp();
	}

	private void bootUp() {
		// mbm = Bitmap.createBitmap(1, 1, Config.ARGB_4444);
		// this.setScaleType(ScaleType.FIT_END);
		ints = new myInts();
		pointList = new ArrayList<Point>();
		allpointList = new ArrayList<Point>();

		mPaint = Paints.getPaint(255, Color.RED, 20);
		linepainStyle = 1;

		setPointBm(Color.GREEN, 10, null);
		pointColor = Color.GREEN;
		pointD = 10;

		bitmap = BitmapFactory.decodeResource(getResources(),
				Pens.getPen_bitmap()[1]);

		m = new Matrix();
		if (SystemUtils.getSystemVersion() >= SystemUtils.V4_0) {
			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		readin();
	}

	Point p;
	private int myListsize;
	int i;
	int j;
	private boolean save;

	@Override
	protected void onDraw(Canvas canvas) {

		if (mbm != null) {
			canvas.drawBitmap(mbm, m, mPaint);
		}
		if (!save) {
			if (pointList.size() == 0) {
				return;
			}
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
				app.run = false;
			}
		}

		if (isUp) {
			isUp = false;
			resumeView(NOMAL_TOUCH);
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
								w.bitmapStyle);
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

	@SuppressLint("NewApi")
	public void resumeView(int mode) {
		tempbm = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_4444);
		c = new Canvas(tempbm);
		this.draw(c);
		mbm = tempbm;
		m = new Matrix();

		if (mode == NOMAL_TOUCH) {
			allpointList.addAll(pointList);
			pointList.clear();
		} else {
			pointList.clear();

		}
	}

	private Canvas c;

	AnimatedGifEncoder e;

	private int mode;
	private boolean stop;
	private boolean justpointList;

	public void show() {
		if (allpointList.size() > 0 && !readin) {
			myListsize = allpointList.size();
			readList();
		} else {
			app.run = false;
		}
	}

	public void clear() {
		stop = true;
		mbm = Bitmap.createBitmap(width, height, Config.ARGB_4444);
		m = new Matrix();
		pointList.clear();
		if (!justpointList)
			allpointList.clear();
		invalidate();
		stop = false;
	}

	public void ok() {
		justpointList = true;
		clear();
		show();
		writeIn();
		justpointList = false;

	}

	public void delete() {
		if (!readin) {
			app.gifList = null;
			readin = false;
			pointList.clear();
			allpointList.clear();
			clear();
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
		handler.sendEmptyMessageDelayed(0, 0);
	}

	private void bimapInit(float x, float y, int drawWhat, int bitmapStyle) {
		// TODO Auto-generated method stub
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

	public void readin() {
		readin = true;

		try {
			ObjectInputStream oin = new ObjectInputStream(new FileInputStream(
					sd + "/a"));
			myInts ints = (myInts) oin.readObject();
			allpointList.addAll(ints.myList);
			readList();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void readList() {
		readin = true;
		myListsize = allpointList.size();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Sleep.sleep(50);
				Point point;
				for (int i = 0; i < myListsize;) {
					if (stop) {
						return;
					}
					point = allpointList.get(i);
					if (i == myListsize - 1) {
						point.last = true;
					}
					i++;
					Sleep.sleep(HANDLER_DELAY);

					pointList.add(point);
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
					// allpointList.clear();
					// myList.clear();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
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