package hello.world.mylayout;

import hello.world.utils.To;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
/**
 * @since 2015 06 24
 * @author funbee {@link https://github.com/hellofunbee/gifEffect}
 */
public class MyScroll extends ViewGroup {

	private static int nowId;
	private GestureDetector detector;
	private Context context;

	public MyScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// TODO Auto-generated constructor stub
		initView();
	}

	public MyScroll(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
		initView();
	}

	private long count;
	private boolean isFling;

	private void initView() {
		myScroller = new MyScroller();
		detector = new GestureDetector(context, new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub

				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {

				scrollBy((int) distanceX, 0);
				if (distanceY > distanceX) {
					return false;
				}

				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO Auto-generated method stub
				isFling = true;
				if (velocityX > 0 && currId > 0) {
					currId--;

				} else if (velocityX < 0 && currId < getChildCount() - 1) {
					currId++;

				}

				moveToNextId(currId);
				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		for (int i = 0; i < getChildCount(); i++) {

			View v = getChildAt(i);

			v.layout(getWidth() * i, 0, getWidth() * (i + 1), getHeight());

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	private int startX;
	private int endX;
	private int currId;
	private int nextId;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// 为this设置touch事件，（实际上点击事件是viewpage的，移动的也是viewpage，显示在viewpage的内容并为移动）
		detector.onTouchEvent(event);
		// 把事件分配给子控件上
		getChildAt(currId).dispatchTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = (int) event.getX();
			int wx = getWidth() / 2;
////			System.out.println("==============="+wx);
//			if (wx - 20 < event.getX() && event.getX() < wx + 20) {
//				return false;
//			}
			return true;

		case MotionEvent.ACTION_UP:
			// 对子控件进行移动
			if (!isFling) {
				endX = (int) event.getX();
				if (endX - startX > getWidth() / 2) {
					nextId = currId - 1;
				} else if (startX - endX > getWidth() / 2) {
					nextId = currId + 1;
				} else {
					nextId = currId;
				}

				moveToNextId(nextId);
			}
			isFling = false;

			break;
		default:
			break;
		}
		return true;
	}

	private MyScroller myScroller;

	public void moveToNextId(int nextId) {
		// TODO Auto-generated method stub

		if (nextId < 0) {
			currId = getChildCount() - 1;
			// scrollTo((getChildCount() -1)*getWidth() + 1/2*getWidth(), 0);
		} else if (nextId > getChildCount() - 1) {
			currId = 0;
			// scrollTo(-1/2*getWidth(),0);
		} else {
			currId = nextId;

			// currId = (nextId >= 0) ? nextId : getChildCount() - 1;
			//
			//
			//
			//
			// if (nextId > 0) {
			// currId = (nextId <= getChildCount() - 1 ? nextId : 0);
			//
			// }

			// scrollTo(nextId*getWidth(), 0);
			int distance = currId * getWidth() - getScrollX();

			myScroller.startScroll(getScrollX(), distance, 0, 0, 500);
		}
		if (onPageChangeListener != null) {
			onPageChangeListener.moveToDest(currId);
		}
		invalidate();

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return true;
	}

	int nowX;
	int nowY;

	/**
	 * 
	 * 当invalidate() 时会触发这个
	 */
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (myScroller.computeScrollOffset()) {
			nowX = (int) myScroller.getCurrX();
			nowY = (int) myScroller.getCurrY();
			scrollTo(nowX, nowY);
			invalidate();
		}

	}

	private OnPageChangeListener onPageChangeListener;

	public OnPageChangeListener getOnPageChangeListener() {
		return onPageChangeListener;
	}

	public void setOnPageChangedLinstener(
			OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}

	public interface OnPageChangeListener {
		void moveToDest(int destId);
	}

}
