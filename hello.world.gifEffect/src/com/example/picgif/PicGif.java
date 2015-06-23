package com.example.picgif;

import hello.world.builder.gifeffect.R;

import java.io.File;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.GestureDetector;
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
import android.widget.Toast;

import com.example.adomin.Mapp;
import com.example.backdraw2.BaseSetupActivity;
import com.example.backdraw2.SettingActivity;
import com.example.backdraw2.ShowGifActivity;
import com.example.backdraw2.Splash;
import com.example.mylayout.MyScroll;
import com.example.paints.Paints;
import com.example.paints.Pens;
import com.example.picgif.MRelativeLayoutPicGif.OnDeleteListener;
import com.example.picgif.MRelativeLayoutPicGif.OnSave;
import com.example.picgif.MRelativeLayoutPicGif.OnShow;
import com.example.png2gif.AnimatedGifEncoder;
import com.example.utils.LPHelper;
import com.example.utils.MAnimtoin;
import com.example.utils.MPoint;
import com.example.utils.RotePoint;
import com.example.utils.To;

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
	private int colorpixel;
	private int md = 10;
	private int threadNum;
	private int saveDelay;
	private int dw;
	private int dh;
	private int sx;
	private int sy;

	private int[] paints_gif;

	protected boolean fromSdGif;

	private PicAdjust pa;

	@SuppressWarnings("deprecation")
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

		paint_show = (ImageView) draw_rl.findViewById(R.id.paint_show);
		mSeekBar_paint = (SeekBar) draw_rl.findViewById(R.id.sb_scale);

		dad_color_helper = View.inflate(getApplicationContext(),
				R.layout.color_helper_picgif, null);
		color_helper = (ImageView) dad_color_helper
				.findViewById(R.id.color_helper);
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

						if ((new MPoint(x1, y1).distanceTo(new MPoint(x0, y0))) > x0) {
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
						draw.setPointBm(colorpixel, md, null);

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
							s = end;
						} else {
							end -= 8;
							end = end % 360;

							MAnimtoin.run(color_helper, -1, (int) s, (int) end);
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

		pic_paints.addView(addPaints(paints_bitmap, BITMAP_PAINT));
		pic_paints.addView(addPaints(paints_gif, GIF_BITMAP_PAINT));
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
				String addr = Environment.getExternalStorageDirectory()
						+ "/haha/m" + System.currentTimeMillis() + ".gif";
				e.start(addr);
				app.setAddr(addr);
				if (alterbm == null)
					alterbm = Bitmap.createBitmap(app.w, app.h,
							Config.ARGB_8888);

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
				// TODO Auto-generated method stub
				if (bm != null) {
					bm = null;
				}
			}
		});
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
						if (true) {
							file = new File(Environment
									.getExternalStorageDirectory()
									+ "/sys/temp");
							if (!file.exists()) {
								file.mkdirs();
							}

							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(new File(file, "tmp_img")));

							startActivityForResult(intent, 3);

						}

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
			bm = LPHelper.loadBitmap(path, true, sw, sh);
			w = bm.getWidth();
			h = bm.getHeight();
			float drawShape = dw / (float) dh;
			float picShape = w / (float) h;
			if (drawShape > picShape) {
				app.scale = dh / (float) h;
				app.h = h;
				app.w = (int) (drawShape * h);
			} else {
				app.scale = dw / (float) w;

				app.w = w;
				app.h = (int) (w / drawShape);
			}
			sx = (app.w - w) / 2;
			sy = (app.h - h) / 2;

			alterbm = Bitmap.createBitmap(app.w, app.h, Config.RGB_565);
			Canvas c = new Canvas(alterbm);
			c.drawBitmap(bm, sx, sy, Paints.getPaint());
			bm.recycle();
			get();
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
		}
		if (resultCode == RESULT_OK && requestCode == 3) {
			if (app.run) {
				Toast.makeText(app, "wait... ", Toast.LENGTH_SHORT).show();
				return;
			}
			app.run = true;

			bm = LPHelper.loadBitmap(file + "/tmp_img", true, sw, sh);
			w = bm.getWidth();
			h = bm.getHeight();
			float drawShape = dw / (float) dh;
			float picShape = w / (float) h;
			if (drawShape > picShape) {
				app.scale = dh / (float) h;
				app.h = h;
				app.w = (int) (drawShape * h);
			} else {
				app.scale = dw / (float) w;
				app.w = w;
				app.h = (int) (w / drawShape);
			}
			sx = (app.w - w) / 2;
			sy = (app.h - h) / 2;
			alterbm = Bitmap.createBitmap(app.w, app.h, Config.RGB_565);
			Canvas c = new Canvas(alterbm);
			c.drawBitmap(bm, sx, sy, Paints.getPaint());
			bm.recycle();
			get();

		}
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
			finish();
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
	private int h;
	private int w;
	public Bitmap alterbm;

	private File file;

	public void get() {
		draw.clear(alterbm);
		draw.show();
	}

	File filePath = new File(Environment.getExternalStorageDirectory(),
			"/mtemp");
	int n;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == PAINT_SHOW_OFF) {
				paint_show.setVisibility(View.GONE);
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
