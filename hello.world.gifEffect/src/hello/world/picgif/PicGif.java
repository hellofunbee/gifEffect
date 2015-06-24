package hello.world.picgif;

import hello.world.act.BaseSetupActivity;
import hello.world.act.SettingActivity;
import hello.world.act.ShowGifActivity;
import hello.world.act.Splash;
import hello.world.builder.gifeffect.R;
import hello.world.domain.Mapp;
import hello.world.mylayout.MyScroll;
import hello.world.mylayout.PicAdjust_Bg;
import hello.world.paints.Paints;
import hello.world.paints.Pens;
import hello.world.picgif.MRelativeLayoutPicGif.OnDeleteListener;
import hello.world.picgif.MRelativeLayoutPicGif.OnSave;
import hello.world.picgif.MRelativeLayoutPicGif.OnShow;
import hello.world.togif.AnimatedGifEncoder;
import hello.world.utils.GetColor;
import hello.world.utils.LPHelper;
import hello.world.utils.LPHelper_Large;
import hello.world.utils.MAnimtoin;
import hello.world.utils.MPoint;
import hello.world.utils.RotePoint;
import hello.world.utils.To;

import java.io.File;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @since 2015 06 24
 * @author funbee {@link https://github.com/hellofunbee/gifEffect}
 */
public class PicGif extends BaseSetupActivity {
	protected static final int PAINT_SHOW_OFF = 3;

	private static final int BITMAP_PAINT = 1;

	private static final int GIF_BITMAP_PAINT = 2;

	protected static final int HE_WANt_A_SD_GIF = 4;

	protected static final int HE_WANt_A_SD_Pic = 5;

	private int[] paints_bitmap;

	private MRelativeLayoutPicGif mlayout;

	private LinearLayout draw_rl;
	private PicGifDraw draw;

	private LinearLayout draw_choice;
	private GestureDetector color_helper_detector;

	private ImageView color_helper;

	private SeekBar mSeekBar_paint;
	private int colorpixel = Color.WHITE;
	public int md = 10;
	private int threadNum;
	private int saveDelay;
	private int dw;
	private int dh;

	private int[] paints_gif;

	protected boolean fromSdGif;

	private PicAdjust pa;

	private PicAdjust_Bg pa_b;
	public Bitmap bgbm;

	private TextView text_show_value;

	private ImageView color_pic_as_bg_color;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_picgif);

		sw = getWindowManager().getDefaultDisplay().getWidth();
		sh = getWindowManager().getDefaultDisplay().getHeight();
		app = (Mapp) getApplication();
		app.picGif = this;
		app.stopNow = false;
		mlayout = (MRelativeLayoutPicGif) findViewById(R.id.mrl);
		mlayout.setSwSh(sw, sh);
		draw_rl = (LinearLayout) View.inflate(getApplicationContext(),
				R.layout.iv_board_picgif, null);

		draw = (PicGifDraw) draw_rl.findViewById(R.id.iv_draw);
		dw = sw;
		dh = To.dip2px(app, 300);
		app.scale = 2;
		app.w = (int) (dw / app.scale);
		app.h = (int) (dh / app.scale);

		alterbm = Bitmap.createBitmap(app.w, app.h, Config.ARGB_8888);
		Canvas c = new Canvas(alterbm);
		c.drawColor(app.bgColor);
		draw.setBackgroundColor(app.bgColor);

		paint_show = (ImageView) draw_rl.findViewById(R.id.paint_show);
		text_show_value = (TextView) draw_rl.findViewById(R.id.text_show_value);
		mSeekBar_paint = (SeekBar) draw_rl.findViewById(R.id.sb_scale);
		setGridient(mSeekBar_paint, colorpixel);

		dad_color_helper = View.inflate(getApplicationContext(),
				R.layout.color_helper_picgif, null);
		color_helper = (ImageView) dad_color_helper
				.findViewById(R.id.color_helper);
		color_pic_as_bg_color = (ImageView) dad_color_helper
				.findViewById(R.id.as_bg_color);
		color_helper_detector = new GestureDetector(getApplicationContext(),
				new GestureDetector.SimpleOnGestureListener() {

					private float s;
					private float end;
					private Bitmap mbm;

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						if (app.run) {
							return true;
						}
						float x1 = e.getX();
						float y1 = -e.getY();
						int mw = color_helper.getWidth();
						int mh = color_helper.getHeight();
						int x0 = mw / 2;
						int y0 = -mh / 2;
						if ((new MPoint(x1, y1).distanceTo(new MPoint(x0, y0))) < To
								.dip2px(app, 45)) {

							app.bgColor = Color.parseColor(GetColor
									.argbToRgb(colorpixel));

							Canvas c = new Canvas(alterbm);
							c.drawColor(app.bgColor);
							if (bgbm != null)
								c.drawBitmap(bgbm, new Matrix(),
										Paints.getPaint());

							draw.setBackgroundColor(app.bgColor);
							app.run = true;
							showIt();

							return true;
						}
						if ((new MPoint(x1, y1).distanceTo(new MPoint(x0, y0))) > x0) {
							// draw.setPointBm(0, md, null);
							return true;
						}
						MPoint mp = RotePoint.rotatePoint(new MPoint(x1, y1),
								new MPoint(x0, y0),
								(float) Math.toRadians(end), false);
						x1 = mp.x;
						y1 = -mp.y;
						float px = x1 / (mw);
						float py = y1 / (mh);
						mbm = BitmapFactory.decodeResource(getResources(),
								R.drawable.color_pic);
						int w = mbm.getWidth();
						int h = mbm.getHeight();

						colorpixel = mbm.getPixel((int) (px * w),
								(int) (py * h));
						setGridient(mSeekBar_paint, colorpixel);
						draw.setPointBm(colorpixel, md, null);
						mbm = null;
						return true;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						int dx = (int) (e2.getX() - e1.getX());

						if (dx > 0) {
							end += 8;
							end = end % 360;
							MAnimtoin.run(color_helper, 1, (int) s, (int) end);
							MAnimtoin.run(color_pic_as_bg_color, 1, (int) s,
									(int) end);

							s = end;
						} else {
							end -= 8;
							end = end % 360;

							MAnimtoin.run(color_helper, -1, (int) s, (int) end);
							MAnimtoin.run(color_pic_as_bg_color, -1, (int) s,
									(int) end);
							s = end;
						}
						return true;
					}

				});

		color_helper.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				color_helper_detector.onTouchEvent(event);
				return true;
			}
		});
		mSeekBar_paint.setMax(200);
		mSeekBar_paint.setProgress(10);

		mSeekBar_paint
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						handler.sendEmptyMessageDelayed(PAINT_SHOW_OFF, 700);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (app.run) {
							return;
						}
						if (progress == 0) {
							progress = 1;
						}
						md = progress;
						draw.setPointBm(colorpixel, md, paint_show);
						if (paint_show.getVisibility() == View.GONE) {
							paint_show.setVisibility(View.VISIBLE);
						}
						text_show_value.setText(String.valueOf(progress));
						if (text_show_value.getVisibility() == View.GONE) {
							text_show_value.setVisibility(View.VISIBLE);
						}

					}
				});

		RelativeLayout menu1 = (RelativeLayout) View.inflate(
				getApplicationContext(), R.layout.setting, null);

		dad_pic_paints = (RelativeLayout) View.inflate(getApplicationContext(),
				R.layout.paints_group, null);
		dad_pic_paints.setVisibility(View.GONE);
		// viewGroup
		MyScroll pic_paints = (MyScroll) dad_pic_paints
				.findViewById(R.id.mypaint_group);

		// viewgroup Ìí¼Ó view
		paints_bitmap = Pens.getPen_bitmap();
		paints_gif = Pens.getPen_gif();

		pic_paints.addView(addPaints(paints_gif, GIF_BITMAP_PAINT));
		pic_paints.addView(addPaints(paints_bitmap, BITMAP_PAINT));
		draw_choice = (LinearLayout) View.inflate(getApplicationContext(),
				R.layout.main_choice, null);
		initSet(draw_choice);

		mlayout.addView(draw_rl, 0);
		mlayout.addView(dad_pic_paints, 1);
		mlayout.addView(draw_choice, 2);

		mlayout.addView(dad_color_helper, 3);
		mlayout.addView(menu1, 4);

		mlayout.onSavelitenner(new OnSave() {

			@Override
			public void save() {

				if (draw.allpointList.size() == 0 && app.gifList == null) {
					app.run = false;
					return;
				}

				preforSave();
				String addr = app.path + System.currentTimeMillis() + ".gif";
				e.start(addr);
				app.setAddr(addr);
				e.setDelay(saveDelay);
				draw.clear(alterbm);
				e.addFrame(alterbm, 0);
				draw.save(e, threadNum);
				return;

			}
		});
		mlayout.setOnShowListenner(new OnShow() {
			@Override
			public void onShow() {
				showIt();
			}
		});
		mlayout.setOnDeleteListener(new OnDeleteListener() {

			@Override
			public void delete() {
				if (bm != null) {
					bm = null;
				}
			}
		});

	}

	public void setGridient(SeekBar seekBar, int color) {
		if (outerRs == null) {
			outerRs = new float[] { 40, 40, 40, 40, 40, 40, 40, 40 };
			inset = new RectF(20, 20, 20, 20);
			innerRs = new float[] { 20, 20, 20, 20, 20, 20, 20, 20 };
		}

		sd1 = new ShapeDrawable(new RoundRectShape(outerRs, inset, innerRs));
		sd1.getPaint().setColor(Color.GRAY);
		sd = new ShapeDrawable(new RoundRectShape(outerRs, inset, innerRs));
		sd.getPaint().setColor(color);

		Drawable[] layers = new Drawable[] { sd1,
				new ClipDrawable(sd, Gravity.LEFT, ClipDrawable.HORIZONTAL) };
		LayerDrawable ld = new LayerDrawable(layers);
		ld.setId(0, android.R.id.background);
		ld.setId(1, android.R.id.progress);
		seekBar.setProgressDrawable(ld);
		sd1 = null;
		sd = null;

	}

	public void showWhatWeGot() {
		Intent intent = new Intent(PicGif.this, ShowGifActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	public void uiShow(final Bitmap showbm) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				draw.setMbm(showbm);
			}
		});
	}

	private void showIt() {

		draw.clear(alterbm);
		draw.show();
		return;
	}

	private void initSet(LinearLayout v) {

		v.findViewById(R.id.iv_color).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				colorpixel = Color.WHITE;
				setGridient(mSeekBar_paint, Color.WHITE);
				draw.setWhat(1);
				if (dad_pic_paints.getVisibility() == View.VISIBLE) {
					dad_pic_paints.setVisibility(View.GONE);
				}
				if (dad_color_helper.getVisibility() == View.VISIBLE) {
					dad_color_helper.setVisibility(View.GONE);
				} else {
					dad_color_helper.setVisibility(View.VISIBLE);
				}
			}
		});
		v.findViewById(R.id.iv_choice_pic).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						draw.setWhat(2);
						if (dad_color_helper.getVisibility() == View.VISIBLE) {
							dad_color_helper.setVisibility(View.GONE);
						}
						if (dad_pic_paints.getVisibility() == View.VISIBLE) {
							dad_pic_paints.setVisibility(View.GONE);
						} else {
							dad_pic_paints.setVisibility(View.VISIBLE);

						}
					}
				});
		v.findViewById(R.id.iv_pics).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, 1);

			}
		});
		v.findViewById(R.id.iv_camera).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);

						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File(app.path, "tmp_img")));

						startActivityForResult(intent, 3);

					}
				});
	}

	private View addPaints(int[] ids1, int paintType) {
		View paint = View.inflate(getApplicationContext(), R.layout.paints,
				null);
		GridView gv1 = (GridView) paint.findViewById(R.id.gv_paints_1);

		init(paint, gv1, ids1, paintType);
		return paint;
	}

	@Override
	protected void onDestroy() {
		app.run = false;
		app.gifList = null;
		app.stopNow = true;
		app.map = null;
		app.key = 0;
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK && requestCode == 4) {
			if (app.run) {
				Toast.makeText(app, "wait... ", Toast.LENGTH_SHORT).show();
				return;
			}
			app.run = true;
			Uri uri = data.getData();
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String path = cursor.getString(column_index);

			String end = path.substring(path.lastIndexOf(".") + 1,
					path.length()).toLowerCase();

			if (end.equals("jpg") || end.equals("gif") || end.equals("png")
					|| end.equals("jpeg") || end.equals("bmp")) {

			} else {
				Toast.makeText(app, "open failed ", Toast.LENGTH_SHORT).show();
				app.run = false;
				return;
			}

			bm = LPHelper.loadBitmap(path, true, sw, sh);
			int w = bm.getWidth();
			int h = bm.getHeight();

			View view = View.inflate(app, R.layout.pic_adjust, null);
			pa = (PicAdjust) view.findViewById(R.id.iv_adjust);
			SeekBar scale = (SeekBar) view.findViewById(R.id.scale);
			SeekBar rot = (SeekBar) view.findViewById(R.id.rot);

			scale.setMax(200);
			scale.setProgress(100);
			rot.setMax(360);

			scale.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if (progress == 0) {
						progress = 1;
					}
					pa.setScale(progress);
				}
			});
			rot.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					pa.setRot(progress);

				}
			});
			view.findViewById(R.id.ok).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									pa.ok(draw);
									mlayout.removeViewAt(5);
									app.run = false;
								}
							});
						}
					});
			pa.setBitmap(app, dw, dh, w, h, bm);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, -1);
			mlayout.addView(view, 5, layoutParams);
			setTouchStop(view);
		}
		if (resultCode == RESULT_OK && requestCode == 1) {

			if (app.run) {
				Toast.makeText(app, "wait... ", Toast.LENGTH_SHORT).show();
				return;
			}
			app.run = true;
			Uri uri = data.getData();
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String path = cursor.getString(column_index);
			String end = path.substring(path.lastIndexOf(".") + 1,
					path.length()).toLowerCase();

			if (end.equals("jpg") || end.equals("gif") || end.equals("png")
					|| end.equals("jpeg") || end.equals("bmp")) {

			} else {
				Toast.makeText(app, "open failed ", Toast.LENGTH_SHORT).show();
				app.run = false;
				return;
			}

			bm = LPHelper_Large.loadBitmap(path, true, sw, sh);
			setBackGround();
		}
		if (resultCode == RESULT_OK && requestCode == 2) {
			if (app.run) {
				Toast.makeText(app, "wait... ", Toast.LENGTH_SHORT).show();
				return;
			}
			app.run = true;
			Uri uri = data.getData();
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String path = cursor.getString(column_index);

			String end = path.substring(path.lastIndexOf(".") + 1,
					path.length()).toLowerCase();

			if (!end.equals("gif")) {
				Toast.makeText(app, "it is not a gif", Toast.LENGTH_SHORT)
						.show();
				app.run = false;
				return;
			}
			bm = LPHelper.loadBitmap(path, true, sw, sh);
			int w = bm.getWidth();
			int h = bm.getHeight();
			bm.recycle();
			int sx = (app.w - w) / 2;
			int sy = (app.h - h) / 2;

			View view = View.inflate(app, R.layout.gif_adjust, null);
			ga = (GifAdjust) view.findViewById(R.id.iv_adjust);
			SeekBar scale = (SeekBar) view.findViewById(R.id.scale);
			SeekBar rot = (SeekBar) view.findViewById(R.id.rot);

			scale.setMax(200);
			scale.setProgress(100);
			rot.setMax(360);

			scale.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if (progress == 0) {
						progress = 1;
					}
					ga.setScale(progress);
				}
			});
			rot.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					ga.setRot(progress);

				}
			});

			view.findViewById(R.id.ok).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									ga.ok();
									mlayout.removeViewAt(5);
									get();
								}
							});
						}
					});
			ga.setAddr(path, app, sx, sy, w, h);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, -1);
			mlayout.addView(view, 5, layoutParams);
			setTouchStop(view);
		}
		if (resultCode == RESULT_OK && requestCode == 3) {
			if (app.run) {
				Toast.makeText(app, "wait... ", Toast.LENGTH_SHORT).show();
				return;
			}
			app.run = true;
			bm = LPHelper_Large.loadBitmap(app.path + "tmp_img", true, sw, sh);
			setBackGround();

		}
	}

	private void setTouchStop(View view) {
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}

	private void setBackGround() {
		int w = bm.getWidth();
		int h = bm.getHeight();

		View view = View.inflate(app, R.layout.pic_adjust_bg, null);
		pa_b = (PicAdjust_Bg) view.findViewById(R.id.iv_adjust);
		SeekBar scale = (SeekBar) view.findViewById(R.id.scale);
		SeekBar rot = (SeekBar) view.findViewById(R.id.rot);

		scale.setMax(200);
		scale.setProgress(100);
		rot.setMax(360);

		scale.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress == 0) {
					progress = 1;
				}
				pa_b.setScale(progress);
			}
		});
		rot.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				pa_b.setRot(progress);
			}
		});
		view.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						bgbm = Bitmap.createBitmap(app.w, app.h,
								Config.ARGB_8888);
						Canvas c = new Canvas(bgbm);

						c.drawBitmap(pa_b.ok(), new Matrix(), Paints.getPaint());
						c = new Canvas(alterbm);
						c.drawBitmap(bgbm, new Matrix(), Paints.getPaint());

						mlayout.removeViewAt(5);
						app.run = false;
						get();
					}
				});
			}
		});
		pa_b.setBitmap(app, dw, dh, w, h, bm);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, -1);
		mlayout.addView(view, 5, layoutParams);
		setTouchStop(view);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Intent intent = new Intent(this, SettingActivity.class);
			intent.putExtra("father", 1);
			startActivity(intent);
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mlayout.getChildCount() == 6) {
				mlayout.removeViewAt(5);
				app.run = false;
			} else {
				finish();
			}
			return true;
		}
		return false;
	}

	private void init(View v, GridView gv, final int[] pens, final int paintType) {
		gv.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return pens.length;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view;
				if (convertView != null) {
					view = convertView;
				} else {
					view = View.inflate(getApplicationContext(),
							R.layout.pics_demo_item, null);
					ImageView iv = (ImageView) view
							.findViewById(R.id.item_demopic);
					iv.setImageResource(pens[position]);
				}

				return view;
			}
		});
		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (paintType == BITMAP_PAINT) {
					if (position == 0) {
						handler.sendEmptyMessage(HE_WANt_A_SD_Pic);
					} else
						draw.setBitmapStyle(pens[position],
								getApplicationContext());
				} else {

					if (position == 0) {
						handler.sendEmptyMessage(HE_WANt_A_SD_GIF);
					} else {
						draw.setGifStyle(pens[position],
								getApplicationContext());
					}

				}
			}
		});

	}

	private Bitmap bm;
	public Bitmap alterbm;

	public void get() {
		draw.clear(alterbm);
		draw.show();
	}

	int n;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == PAINT_SHOW_OFF) {
				paint_show.setVisibility(View.GONE);
				text_show_value.setVisibility(View.GONE);

			} else if (msg.what == HE_WANt_A_SD_GIF) {
				doAsHeSaid();
			} else if (msg.what == HE_WANt_A_SD_Pic) {
				doAsHeSaidAgain();
			}
		}

	};

	private AnimatedGifEncoder e;

	protected void doAsHeSaid() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 2);
	}

	protected void doAsHeSaidAgain() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 4);
	}

	private void preforSave() {
		e = new AnimatedGifEncoder();
		e.setRepeat(1);

	}

	@Override
	protected void onResume() {
		threadNum = app.sp.getInt("threadnum", 4);
		saveDelay = app.sp.getInt("savedelay", 100);
		app.sp.getInt("picscale", 50);
		super.onResume();
	}

	private int sw;
	private int sh;
	private Mapp app;
	private View dad_color_helper;
	private RelativeLayout dad_pic_paints;
	private ImageView paint_show;

	private GifAdjust ga;

	private float[] outerRs;

	private RectF inset;

	private float[] innerRs;

	private ShapeDrawable sd1;

	private ShapeDrawable sd;

	@Override
	public void showNext() {
		Intent intent = new Intent(this, SettingActivity.class);
		intent.putExtra("father", 1);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Splash.class);
		intent.putExtra("father", 0);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

}
