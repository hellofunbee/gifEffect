package hello.world.drawpic;

import hello.world.builder.gifeffect.R;
import hello.world.domain.Mapp;
import hello.world.utils.To;
import hello.world.utils.Vib;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * @since 2015 06 24
 * @author funbee {@link https://github.com/hellofunbee/gifEffect}
 */
public class MRelativeLayout extends RelativeLayout {
	protected static final int ROT = 0;
	private int mw;
	private int mh;
	private Mapp app;
	private Context context;
	private View main_choice;
	private Draw draw;
	private GestureDetector pends_detector;
	private View delete;
	private View saveToGif;
	private View show;
	private View getPaints;
	private SharedPreferences sp;
	private boolean longPress;
	private GestureDetector home_detectoe;
	private GestureDetector menu_hand_detector;
	private int sw;
	private int sh;

	public void setSwSh(int sw, int sh) {
		this.sw = sw;
		this.sh = sh;
	}

	public MRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		app = (Mapp) context.getApplicationContext();
		sp = app.sp;
		initWork();
	}

	private void initWork() {
		pends_detector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						int dx = (int) (e2.getX() - e1.getX());
						int dy = (int) (e2.getY() - e1.getY());
						main_choice.setLeft((int) (main_choice.getLeft() + dx));
						main_choice.setTop((int) (main_choice.getTop() + dy));

						Editor edit = sp.edit();
						edit.putInt("x2", main_choice.getLeft() + dx);
						edit.putInt("y2", main_choice.getTop() + dy);
						edit.commit();
						return true;
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						return true;
					}

				});

		home_detectoe = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						if (menu_hand.getVisibility() == GONE) {
							menu_hand.setVisibility(VISIBLE);
							home.setVisibility(GONE);

						}
						return true;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						// TODO Auto-generated method stub
						int dx = (int) (e2.getX() - e1.getX());
						int dy = (int) (e2.getY() - e1.getY());
						setting_layout.setLeft((int) (setting_layout.getLeft() + dx));
						setting_layout.setTop((int) (setting_layout.getTop() + dy));

						Editor edit = sp.edit();
						edit.putInt("x6", setting_layout.getLeft() + dx);
						edit.putInt("y6", setting_layout.getTop() + dy);
						edit.commit();
						return true;
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						return true;
					}

				});
		menu_hand_detector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						home.setVisibility(VISIBLE);
						menu_hand.setVisibility(GONE);
						return true;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						// TODO Auto-generated method stub
						int dx = (int) (e2.getX() - e1.getX());
						int dy = (int) (e2.getY() - e1.getY());
						setting_layout.setLeft((int) (setting_layout.getLeft() + dx));
						setting_layout.setTop((int) (setting_layout.getTop() + dy));

						Editor edit = sp.edit();
						edit.putInt("x6", setting_layout.getLeft() + dx);
						edit.putInt("y6", setting_layout.getTop() + dy);
						edit.commit();
						return true;
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						return true;
					}

				});

	}

	private Handler handler = new Handler();
	Runnable run = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Vib.vib(context, 50);
			longPress = true;

		}
	};
	private int sx, sy, nx, ny, dx, dy;

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).layout(0, 0, mw, mh);
		}
		View dad_pic_paints = getChildAt(1);
		int y1 = sp.getInt("y1", sh - To.dip2px(context, 140));
		dad_pic_paints.layout(0, 0, mw, mh);
		dad_pic_paints.setTop(y1);
		dad_pic_paints.findViewById(R.id.mypaint_group).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						// viewpage.onTouchEvent(event);
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							sx = (int) event.getX();
							sy = (int) event.getY();
							handler.postDelayed(run, 500);
							return false;
						case MotionEvent.ACTION_MOVE:
							nx = (int) event.getX();
							ny = (int) event.getY();
							dx = nx - sx;
							dy = ny - sy;

							if (longPress) {
								getChildAt(1).setTop(
										(int) (getChildAt(1).getTop() + dy));

								Editor edit = sp.edit();

								edit.putInt("y1", getChildAt(1).getTop() + dy);
								edit.commit();
							} else {
								if (dx > To.dip2px(context, 4)
										|| dy > To.dip2px(context, 4)) {
									handler.removeCallbacks(run);
								}
							}
							break;
						case MotionEvent.ACTION_UP:
							if (longPress) {
								longPress = false;
							} else {
								handler.removeCallbacks(run);
							}
							break;

						default:
							break;
						}
						return false;
					}
				});

		views();

	}

	private RelativeLayout setting_layout;
	private LinearLayout draw_rl;

	private void views() {
		menu2 = (RelativeLayout) getChildAt(4);
		home = (ImageView) menu2.findViewById(R.id.home);
		
		
		int x2 = sp.getInt("x2", 0);
		int y2 = sp.getInt("y2", sh - To.dip2px(context, 245));
		int x6 = sp.getInt("x6", sw / 2 - home.getWidth());
		int y6 = sp.getInt("y6",  home.getWidth() / 2);

		View color_helper = getChildAt(3);
		View cv = color_helper.findViewById(R.id.color_helper);
		color_helper.setLeft(sw - cv.getWidth() / 2);
		color_helper.setTop(sh - cv.getHeight() / 2 - To.dip2px(app, 20));

		menu_hand = (RelativeLayout) menu2.findViewById(R.id.menu_hand);
		setting_layout = (RelativeLayout) menu2
				.findViewById(R.id.setting_layout);
		setting_layout.setLeft(x6);
		setting_layout.setTop(y6);

		draw_rl = (LinearLayout) getChildAt(0);
		draw = (Draw) draw_rl.findViewById(R.id.iv_draw);

		menu_hand.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				menu_hand_detector.onTouchEvent(event);
				return true;
			}
		});
		home.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				home_detectoe.onTouchEvent(event);
				return true;
			}
		});

		main_choice = getChildAt(2);

		choice_ll = getChildAt(2).findViewById(R.id.choice_ll);
		main_choice.setLeft(x2);
		main_choice.setTop(y2);
		choice_ll.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				pends_detector.onTouchEvent(event);
				return true;
			}
		});

		getPaints = menu2.findViewById(R.id.sb);
		delete = menu2.findViewById(R.id.sr);
		saveToGif = menu2.findViewById(R.id.st);
		show = menu2.findViewById(R.id.sl);
		show.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Vib.vib(context, 50);
				if (app.run) {
					Toast.makeText(context, "wait...", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				app.run = true;
//				draw.clear();
				home.setVisibility(VISIBLE);
				menu_hand.setVisibility(GONE);
				if (OnShowListenner != null) {
					OnShowListenner.onShow();
				}

			}
		});
		saveToGif.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Vib.vib(context, 50);
				if (app.run) {
					Toast.makeText(context, "wait...", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				app.run = true;
				home.setVisibility(VISIBLE);
				menu_hand.setVisibility(GONE);

				if (onSavelitenner != null) {
					onSavelitenner.save();
				}

			}
		});
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (app.run) {
					Toast.makeText(context, "wait...", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (onDeleteListener != null) {
					onDeleteListener.delete();
				}

				draw.delete();
				home.setVisibility(VISIBLE);
				menu_hand.setVisibility(GONE);
				Vib.vib(context, 50);

			}
		});
		getPaints.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				home.setVisibility(VISIBLE);
				menu_hand.setVisibility(GONE);

				dis_pens(true);

			}
		});

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mw = MeasureSpec.getSize(widthMeasureSpec);
		mh = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(mw, mh);
	}

	private void dis_pens(boolean b) {
		if (getChildAt(2).getVisibility() == View.VISIBLE) {
			getChildAt(2).setVisibility(View.GONE);
			getChildAt(2).setFocusable(false);

			if (b) {
				Vib.vib(context, 50);
			}

		} else {
			if (b) {
				Vibrator v = (Vibrator) context
						.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(50);
			}
			getChildAt(2).setVisibility(View.VISIBLE);
			getChildAt(2).setFocusable(true);
		}
	}

	private RelativeLayout menu2;
	private ImageView home;
	private RelativeLayout menu_hand;
	private OnSave onSavelitenner;

	public void onSavelitenner(OnSave l) {
		this.onSavelitenner = l;
	}

	public interface OnSave {
		void save();
	}

	private OnShow OnShowListenner;
	private View choice_ll;

	public void setOnShowListenner(OnShow onShowListenner) {
		OnShowListenner = onShowListenner;
	}

	public interface OnShow {
		void onShow();
	}

	private OnDeleteListener onDeleteListener;

	public void setOnDeleteListener(OnDeleteListener l) {
		this.onDeleteListener = l;
	}

	public interface OnDeleteListener {
		void delete();
	}

}
