package hello.world.act;

import hello.world.domain.Mapp;
import hello.world.domain.Point;
import hello.world.domain.myInts;
import hello.world.paints.Paints;
import hello.world.utils.GetColor;
import hello.world.utils.Sleep;
import hello.world.utils.SystemUtils;
import hello.world.utils.Vib;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification.Action;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
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

public class DrawLine extends View {
	// 静态变量，
	private static final String sd = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	private static final int LINE = 1;

	private static final int PAINT_NONE = -1;
	private static final int PAINT_HASE_STYLE = 1;
	// 控制变量
	private static final int ONE = 1;
	private static final int TWO = 2;

	private int what = 1; // 画点，线，图，？
	private float strokeWidth = 3; // 点与点之间的距离
	private long HANDLER_DELAY = 10; // 预览延时
	private Paint mPaint; // 画笔（大小，颜色，等设置）
	private int linepainStyle;// draw point style
	private int pointColor;// draw point style
	private int pointD; // 画点的半径
	private Bitmap pointBm;

	public void setPointBm(int color, int d, ImageView iv_show) {
		this.what = LINE;
		pointBm = Bitmap.createBitmap(d, d, Config.ARGB_8888);
		Canvas c = new Canvas(pointBm);
		pointColor = Color.parseColor(GetColor.argbToRgb(color));
		c.drawCircle(d / 2, d / 2, d / 2, Paints.getPaint(pointColor, d));
		pointD = d;
		mPaint = Paints.getPaint(255, pointColor, pointD);

	}

	// 一般变量
	private boolean readin;

	private int x, x1;
	private int y, y1;
	private myInts ints;
	private List<Point> pointList;
	public List<Point> allpointList;
	private Mapp app;

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
		ints = new myInts();
		pointList = new ArrayList<Point>();
		allpointList = new ArrayList<Point>();

		mPaint = Paints.getPaint(255, Color.RED, 20);
		linepainStyle = 1;

		setPointBm(Color.GREEN, 10, null);
		pointColor = Color.GREEN;
		pointD = 10;

		m = new Matrix();
		if (SystemUtils.getSystemVersion() >= SystemUtils.V4_0) {
			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

	}

	Point p;
	int i;
	int j;
	int alpha = 255;
	private boolean add;

	@Override
	protected void onDraw(Canvas canvas) {

		if (up && showBitmap != null) {
			canvas.drawBitmap(showBitmap, m, Paints.getPaint());
		}
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
			// if (longPress || mode == 2) {
			if (add) {
				alpha += 2;
			} else {
				alpha -= 2;
			}
			if (alpha >= 255) {
				add = false;
				alpha = 255;
			} else if (alpha <= 0) {
				alpha = 0;
				add = true;
			}
			mPaint.setAlpha(alpha);
			// }

			mOndraw(mCanvas);
			canvas.drawBitmap(showBitmap, m, Paints.getPaint());
			if (readin) {
				Sleep.sleep(HANDLER_DELAY);
			}
		}
		pointList.clear();

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
		}
	}

	private int mode;

	public void show() {
		if (allpointList.size() > 0 && !readin) {
			readList();
		} else {
			app.run = false;
		}
	}

	public void clear() {
		showBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mCanvas.setBitmap(showBitmap);
		m = new Matrix();
		pointList.clear();

		allpointList.clear();
		invalidate();
	}

	public void ok() {
		showBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mCanvas.setBitmap(showBitmap);
		show();

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

	private boolean longPress;
	private int oy;
	private int ox;
	private Handler hand = new Handler();
	Runnable run = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Vib.vib(app, 50);
			longPress = true;
			ox = x;
			oy = y;

		}
	};

	private boolean up;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (app.run) {
			return true;
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			x = (int) event.getX();
			y = (int) event.getY();
			handler.postDelayed(run, 500);
			if (what == LINE) {
				lineInit(x, y, PAINT_NONE, pointD, pointColor, LINE);
			}
			mode = ONE;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if (longPress) {
				longPress = false;
				break;
			}
			handler.removeCallbacks(run);
			if (what == LINE) {
				lineInit(x, y, linepainStyle, pointD, pointColor, LINE);
			}
			mode = TWO;

			break;
		case MotionEvent.ACTION_POINTER_UP:
			handler.removeCallbacks(run);
			mode = ONE;

		case MotionEvent.ACTION_MOVE:
			handler.removeCallbacks(run);
			if (mode == ONE) {
				x = (int) event.getX();
				y = (int) event.getY();
				if (longPress) {
					x = (int) event.getX();
					y = (int) event.getY();
					if (what == LINE) {
						lineInit(x, y, linepainStyle, pointD, pointColor, LINE);
						lineInit(ox, oy, linepainStyle, pointD, pointColor,
								LINE);
					}
					break;
				}
				if (what == LINE) {
					lineInit(x, y, linepainStyle, pointD, pointColor, LINE);
				}
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
			handler.removeCallbacks(run);
			x = (int) event.getX();
			y = (int) event.getY();
			invalidate();
			up = true;
		}
		return true;
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

	public void readin() {
		readin = true;

		try {
			ObjectInputStream oin = new ObjectInputStream(new FileInputStream(
					sd + "/a"));
			myInts ints = (myInts) oin.readObject();
			oin.close();
			allpointList.addAll(ints.myList);

			readList();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void readList() {
		readin = true;
		pointList = allpointList;
		handler.sendEmptyMessageDelayed(0, 0);

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
		showBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mCanvas = new Canvas(showBitmap);
		super.onLayout(changed, left, top, right, bottom);
	}

	private Point w;

	private Bitmap showBitmap;

	private Canvas mCanvas;

}