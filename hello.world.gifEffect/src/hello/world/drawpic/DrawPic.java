package hello.world.drawpic;

import hello.world.act.BaseSetupActivity;
import hello.world.act.SettingActivity;
import hello.world.act.ShowGifActivity;
import hello.world.act.Splash;
import hello.world.builder.gifeffect.R;
import hello.world.decoder.GifDecoder;
import hello.world.domain.Mapp;
import hello.world.domain.Point;
import hello.world.domain.XY;
import hello.world.drawpic.MRelativeLayout.OnDeleteListener;
import hello.world.drawpic.MRelativeLayout.OnSave;
import hello.world.drawpic.MRelativeLayout.OnShow;
import hello.world.mylayout.MyScroll;
import hello.world.mylayout.PicAdjust_Bg;
import hello.world.paints.Paints;
import hello.world.paints.Pens;
import hello.world.togif.AnimatedGifEncoder;
import hello.world.togif.PreforJpgToGif;
import hello.world.utils.GetColor;
import hello.world.utils.LPHelper;
import hello.world.utils.LPHelper_Large;
import hello.world.utils.MAnimtoin;
import hello.world.utils.MPoint;
import hello.world.utils.PicToPic;
import hello.world.utils.RotePoint;
import hello.world.utils.Sleep;
import hello.world.utils.To;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
public class DrawPic extends BaseSetupActivity {
	protected static final int PAINT_SHOW_OFF = 3;

	private static final int BITMAP_PAINT = 1;

	private static final int GIF_BITMAP_PAINT = 2;

	private int[] paints_bitmap;

	private MRelativeLayout mlayout;

	private LinearLayout draw_rl;
	private Draw draw;

	private LinearLayout draw_choice;

	private GestureDetector color_helper_detector;

	private ImageView color_helper;

	private SeekBar mSeekBar_paint;
	private int colorpixel = Color.WHITE;
	public int md = 10;
	private int showDelay;
	private int threadNum;
	private int saveDelay;
	private int dw;
	private int dh;
	private int sx;
	private int sy;

	private int[] paints_gif;

	private PicAdjust_Bg pa;
	public Bitmap backGround;

	private ImageView color_helper_ok;

	private float[] innerRs;

	private RectF inset;

	private float[] outerRs;

	private ShapeDrawable sd1;

	private ShapeDrawable sd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		sw = getWindowManager().getDefaultDisplay().getWidth();
		sh = getWindowManager().getDefaultDisplay().getHeight();

		app = (Mapp) getApplication();
		app.drawPic = this;
		mlayout = (MRelativeLayout) findViewById(R.id.mrl);
		mlayout.setSwSh(sw, sh);

		draw_rl = (LinearLayout) View.inflate(getApplicationContext(),
				R.layout.iv_board, null);
		draw = (Draw) draw_rl.findViewById(R.id.iv_draw);

		dw = sw;
		dh = To.dip2px(app, 300);
		app.scale = 2;
		app.w = (int) (dw / app.scale);
		app.h = (int) (dh / app.scale);
		alterbm = Bitmap.createBitmap(app.w, app.h, Config.ARGB_8888);

		paint_show = (ImageView) draw_rl.findViewById(R.id.paint_show);
		text_show_value = (TextView) draw_rl.findViewById(R.id.text_show_value);
		mSeekBar_paint = (SeekBar) draw_rl.findViewById(R.id.mpb_paint);
		setGridient(mSeekBar_paint, colorpixel);
		mSeekBar = (SeekBar) draw_rl.findViewById(R.id.mpb);
		setGridient(mSeekBar, mColor);

		dad_color_helper = View.inflate(getApplicationContext(),
				R.layout.color_helper, null);
		color_helper = (ImageView) dad_color_helper
				.findViewById(R.id.color_helper);
		color_helper_ok = (ImageView) dad_color_helper
				.findViewById(R.id.color_helper_ok);
		color_helper_detector = new GestureDetector(getApplicationContext(),
				new GestureDetector.SimpleOnGestureListener() {
					private float s;
					private float end;
					private Bitmap mbm;

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						if (app.run) {
							// Toast.makeText(app, "正在执行,请稍后 ", 0).show();
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
							if (mSeekBar.getVisibility() == View.VISIBLE) {
								mColor = Color.parseColor(GetColor
										.argbToRgb(colorpixel));
								setGridient(mSeekBar, mColor);
							}
							return true;
						}
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
						setGridient(mSeekBar_paint, colorpixel);
						draw.setPointBm(colorpixel, md, null);

						return true;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						// TODO Auto-generated method stub
						int dx = (int) (e2.getX() - e1.getX());

						if (dx > 0) {
							end += 8;
							end = end % 360;
							MAnimtoin.run(color_helper, 1, (int) s, (int) end);
							MAnimtoin.run(color_helper_ok, 1, (int) s,
									(int) end);
							s = end;
						} else {
							end -= 8;
							end = end % 360;

							MAnimtoin.run(color_helper, -1, (int) s, (int) end);
							MAnimtoin.run(color_helper_ok, -1, (int) s,
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

		mSeekBar.setProgress(120);
		mSeekBar.setMax(300);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (BackUpBm == null) {
					return;
				}
				if (app.run) {
					Toast.makeText(app, "wait... ", Toast.LENGTH_SHORT).show();
					return;
				}
				app.run = true;
				if (Math.abs(oldthresh - thresh) > 20) {
					again();
				} else {
					showIt();
				}
				oldthresh = thresh;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress < 50) {
					progress = 50;
				}
				thresh = progress;
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

		// viewgroup 添加 view
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

				final Object o = new Object();
				final Object p = new Object();
				if (temp == null || temp.length == 0) {
					if (draw.allpointList.size() == 0) {
						app.run = false;
						Toast.makeText(app,
								"make some draw,or add a picture first ",
								Toast.LENGTH_SHORT).show();

						return;
					} else {
						preforSave();
						String addr = app.path + System.currentTimeMillis()
								+ ".gif";
						e.start(addr);
						app.setAddr(addr);
						if (backGround != null) {
							alterbm = Bitmap.createScaledBitmap(backGround,
									app.w, app.h, true);
						} else {
							alterbm = Bitmap.createBitmap(app.w, app.h,
									Config.ARGB_8888);
						}
						e.setDelay(saveDelay);
						e.addFrame(alterbm, 1);
						draw.save(e, 0, alterbm, threadNum);
					}
					return;
				} else {
					preforSave();
					String addr = app.path + System.currentTimeMillis()
							+ ".gif";
					e.start(addr);
					app.setAddr(addr);

					// draw.clear();
					if (backGround != null) {
						alterbm = Bitmap.createScaledBitmap(backGround, app.w,
								app.h, true);
					} else {
						alterbm = Bitmap.createBitmap(app.w, app.h,
								Config.ARGB_8888);
					}
					e.setDelay(saveDelay);
					e.addFrame(alterbm, 1);

				}

				new Thread(new Runnable() {

					private int frames;
					private int a;
					int j = 0;
					private Object[] objs;

					@Override
					public void run() {

						frames = 1;
						int stroke = 600;
						if (size > 40000 && size < 80000) {
							stroke = 1000;
						} else if (size > 80000 && size < 100000) {
							stroke = 1800;
						} else if (size > 100000) {
							stroke = 2500;
						}
						if (size % stroke != 0) {
							frames = size / stroke + 1;
						} else {
							frames = size / stroke;
						}
						objs = new Object[frames];
						int end = 0;
						int start = 0;
						for (int i = 0; i < frames;) {

							if (a < threadNum) {

								a++;
								start = end;
								end += stroke;
								if (end > size) {
									end = size;
								}
								MThread mt = new MThread(start, end);
								mt.setName(String.valueOf(i));
								mt.start();
								i++;
							}
							Sleep.sleep(10);
						}

					}

					class MThread extends Thread {
						private int end;
						private int start;

						public MThread(int start, int end) {
							super();
							this.end = end;
							this.start = start;

						}

						@Override
						public void run() {
							toGif(start, end);
							a--;

						}
					}

					private void toGif(int start, int end) {
						synchronized (p) {
							for (int i = start; i < end; i++) {
								xy = temp[i];
								alterbm.setPixel(xy.x + sx, xy.y + sy, mColor);
							}

						}

						objs[Integer.parseInt(Thread.currentThread().getName())] = new PreforJpgToGif(
								saveDelay, 1).addFrame(alterbm, app, true);

						synchronized (o) {
							for (; j < frames;) {
								if (objs[j] != null) {
									Object[] obj = (Object[]) objs[j];
									e.writeIn((byte[]) obj[0], (byte[]) obj[1],
											(Integer) obj[2], app.w, app.h, 0,
											0);
									objs[j] = null;
									j++;
								} else {
									break;
								}
							}
							if (j == frames) {
								bm = null;
								objs = null;
								if (draw.allpointList.size() > 0) {
									draw.save(e, frames + 1, alterbm, threadNum);
								} else {
									// temp = null;
									app.gifList = null;
									e.finish();
									app.run = false;
									showWhatWeGot();
								}
							}
						}

					}

				}).start();
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
				if (temp != null) {
					temp = null;
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

		startActivity(new Intent(DrawPic.this, ShowGifActivity.class));
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	public void uiShow(final Bitmap alterbm) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				draw.setImageBitmap(alterbm);
			}
		});
	}

	private void showIt() {

		if (backGround != null) {
			alterbm = Bitmap.createScaledBitmap(backGround, app.w, app.h, true);
		} else {
			alterbm = Bitmap.createBitmap(app.w, app.h, Config.ARGB_8888);
		}
		draw.clear(alterbm);
		if (temp == null) {
			draw.show(0, alterbm);
			return;
		}

		new Thread(new Runnable() {
			int m;

			@Override
			public void run() {

				int stroke = 600;
				if (size > 40000 && size < 80000) {
					stroke = 1000;
				} else if (size > 80000 && size < 100000) {
					stroke = 1800;
				} else if (size > 100000) {
					stroke = 2500;
				}

				for (int i = 0; i < size;) {
					xy = temp[i];
					i++;
					alterbm.setPixel(xy.x + sx, xy.y + sy, mColor);

					if (i % stroke == 0 || i == size) {

						bm = Bitmap.createBitmap(alterbm);
						if (app.gifList != null) {
							for (GifDecoder gd : app.gifList) {

								Bitmap temp = gd.getFrameImage(m);
								for (Point p : gd.points) {
									bm = PicToPic.picToPic(temp, bm, p.x, p.y);
								}
								temp = null;
							}
							m++;
						}
						uiShow(bm);
						Sleep.sleep(showDelay);
					}
				}
				draw.show(m, alterbm);

			}
		}).start();
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

	protected void heWantASdPic() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 2);
	}

	private View addPaints(int[] ids1, int paintType) {
		// TODO Auto-generated method stub
		int[] ids2;
		if (paintType != BITMAP_PAINT) {
			ids2 = new int[ids1.length - 1];
			for (int i = 0; i < ids1.length - 1; i++) {
				ids2[i] = ids1[i + 1];
			}
		} else {
			ids2 = ids1;
		}
		// paint布局文件
		View paint = View.inflate(getApplicationContext(), R.layout.paints,
				null);
		GridView gv1 = (GridView) paint.findViewById(R.id.gv_paints_1);

		init(paint, gv1, ids2, paintType);
		return paint;
	}

	@Override
	protected void onDestroy() {
		app.run = false;
		stopNow = true;
		app.gifList = null;
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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
			BackUpBm = Bitmap.createBitmap(bm);
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

			if (end.equals("jpg") || end.equals("gif") || end.equals("png")
					|| end.equals("jpeg") || end.equals("bmp")) {

			} else {
				Toast.makeText(app, "open failed ", Toast.LENGTH_SHORT).show();
				app.run = false;
				return;
			}

			bm = LPHelper_Large.loadBitmap(path, true, sw, sh);
			int w = bm.getWidth();
			int h = bm.getHeight();

			View view = View.inflate(app, R.layout.pic_adjust_bg, null);
			pa = (PicAdjust_Bg) view.findViewById(R.id.iv_adjust);
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

									if (backGround != null) {
										Bitmap temp = Bitmap.createBitmap(
												app.w, app.h, Config.ARGB_8888);
										Canvas c = new Canvas(temp);
										backGround = Bitmap.createScaledBitmap(
												backGround, app.w, app.h, true);
										c.drawBitmap(backGround, new Matrix(),
												Paints.getPaint());
										c.drawBitmap(pa.ok(), new Matrix(),
												Paints.getPaint());
										backGround = temp;

									} else {
										backGround = pa.ok();
									}

									draw.setImageBitmap(backGround);
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
		if (resultCode == RESULT_OK && requestCode == 3) {
			if (app.run) {
				Toast.makeText(app, "wait... ", Toast.LENGTH_SHORT).show();
				return;
			}
			app.run = true;

			bm = LPHelper.loadBitmap(app.path + "/tmp_img", true, sw, sh);
			BackUpBm = Bitmap.createBitmap(bm);
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
			get();

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Intent intent = new Intent(this, SettingActivity.class);
			intent.putExtra("father", 0);
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
						heWantASdPic();
					} else
						draw.setBitmapStyle(pens[position],
								getApplicationContext());
				} else {

					draw.setGifStyle(pens[position], getApplicationContext());
				}
			}
		});

		/**
		 * 
		 * 一旦移动了，事件被消费掉，OnItemClick 无法获得事件
		 */
		gv.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					return true;
				}
				return false;
			}
		});

	}

	protected static final int FINISH = 0;

	private Bitmap bm;
	public Bitmap BackUpBm;
	private int[] pixs;
	private int h;
	private int w;
	private Bitmap alterbm;
	private List<Integer> listxy;
	private boolean has;

	private SeekBar mSeekBar;
	private int thresh = 300;
	private int oldthresh = 300;

	// private File file;
	private boolean again;

	public void again() {
		try {
			if (BackUpBm != null) {
				bm = Bitmap.createBitmap(BackUpBm);
				get();
			} else {
				app.run = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void get() {

		draw_rl.findViewById(R.id.pv_wait).setVisibility(View.VISIBLE);

		if (again) {
			again = false;
		}
		pixs = new int[w * h];
		bm.getPixels(pixs, 0, w, 0, 0, w, h);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int[] pix = detect(pixs, w, h, 1, thresh, true);
				if (pix == null) {
					return;
				}
				pixs = null;
				listxy = new ArrayList<Integer>();
				for (int i = 0; i < pix.length; i++) {

					if (pix[i] != -1) {
						listxy.add(i);

					}
				}

				size = listxy.size();
				if (size == 0) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							draw_rl.findViewById(R.id.pv_wait).setVisibility(
									View.GONE);
						}

					});
					draw.show(0, alterbm);
					return;
				}
				pix = null;
				handler.sendEmptyMessage(FINISH);

			}

		}).start();

	}

	private void drawImg() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (backGround != null) {
					alterbm = Bitmap.createScaledBitmap(backGround, app.w,
							app.h, true);
				} else {
					alterbm = Bitmap.createBitmap(app.w, app.h,
							Config.ARGB_8888);
				}
				temp = sort();

				size = temp.length;

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						draw_rl.findViewById(R.id.pv_wait).setVisibility(
								View.GONE);
					}

				});
				for (int i = 0; i < size;) {
					xy = temp[i];
					alterbm.setPixel(xy.x + sx, xy.y + sy, mColor);
					i++;

					if (i % 500 == 0 || i == size) {

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								draw.setImageBitmap(alterbm);
							}
						});
						Sleep.sleep(showDelay);
					}
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mSeekBar.setVisibility(View.VISIBLE);
						color_helper_ok.setVisibility(View.VISIBLE);
					}
				});
				draw.show(0, alterbm);
			}

		}).start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == FINISH) {
				drawImg();
			}
			if (msg.what == PAINT_SHOW_OFF) {
				paint_show.setVisibility(View.GONE);
				text_show_value.setVisibility(View.GONE);

			}
		}

	};

	private XY[] sort() {
		c = 0;
		XY[] temp = new XY[size];
		tempArrays = new int[listxy.size()];
		tempArrays[0] = listxy.get(0);
		listxy.remove(0);

		while (listxy.size() > 0) {
			int k = 0;
			for (int j = 0; j < listxy.size(); j++) {

				has = false;
				int it = tempArrays[c];

				k++;
				Integer il = listxy.get(j);
				int d = Math.abs(il - it);
				if ((d == 1)) {
					c++;
					tempArrays[c] = il;
					listxy.remove(j);

					has = true;
					k = 0;
					continue;
				}
				if ((d == w - 1 || (d == w || d == w + 1))) {
					c++;
					tempArrays[c] = il;
					listxy.remove(j);

					has = true;
					k = 0;
					continue;
				}
				if (k >= w) {
					break;
				}
				if (stopNow) {
					stopNow = false;
					return null;
				}

			}
			if (!has) {
				c++;
				tempArrays[c] = (listxy.get(0));
				listxy.remove(0);
			}
		}
		int j;
		for (int i = 0; i < size; i++) {
			j = tempArrays[i];
			xy = new XY();
			xy.x = j % w;
			xy.y = j / w;
			temp[i] = xy;

		}

		tempArrays = null;
		return temp;

	}

	public XY[] temp;
	private int c;
	private boolean stopNow;
	private AnimatedGifEncoder e;
	private int size;

	private int tn;

	/**
	 * 保存gif前的准备
	 */
	private void preforSave() {
		e = new AnimatedGifEncoder();
		e.setRepeat(1);

	}

	/**
	 * 
	 * 素描转化
	 * 
	 * @param px
	 *            要转化的像素数组
	 * @param iw
	 *            宽度
	 * @param ih
	 *            高度
	 * @param num
	 *            num = 1，进行转换，否则不进行任何处理
	 * @param thresh
	 *            调整素描区域画面显示情况
	 * @param flag
	 *            边缘检修
	 * @return
	 */
	int[][][] edge;
	int[][] gray;
	int count = 0;
	int tem;

	public int[] detect(int[] px, final int iw, final int ih, int num,
			int thresh, boolean flag) {
		int i, j, r;
		int[][] inr = new int[iw][ih];// 红色分量矩阵
		int[][] ing = new int[iw][ih];// 绿色分量矩阵
		int[][] inb = new int[iw][ih];// 蓝色分量矩阵
		gray = new int[iw][ih];// 灰度图像矩阵

		for (j = 0; j < ih; j++) {
			for (i = 0; i < iw; i++) {
				inr[i][j] = Color.red(px[i + j * iw]);
				ing[i][j] = Color.green(px[i + j * iw]);
				inb[i][j] = Color.blue(px[i + j * iw]);
				// 转变为灰度图像矩阵
				gray[i][j] = (int) ((inr[i][j] + ing[i][j] + inb[i][j]) / 3.0);
				if (stopNow) {
					stopNow = false;
					return null;
				}
			}
		}
		// Kirsch
		if (num == 1) {
			final byte[][][] kir = {
					{ { 5, 5, 5 }, { -3, 0, -3 }, { -3, -3, -3 } },// kir0
					{ { -3, 5, 5 }, { -3, 0, 5 }, { -3, -3, -3 } },// kir1
					{ { -3, -3, 5 }, { -3, 0, 5 }, { -3, -3, 5 } },// kir2
					{ { -3, -3, -3 }, { -3, 0, 5 }, { -3, 5, 5 } },// kir3
					{ { -3, -3, -3 }, { -3, 0, -3 }, { 5, 5, 5 } },// kir4
					{ { -3, -3, -3 }, { 5, 0, -3 }, { 5, 5, -3 } },// kir5
					{ { 5, -3, -3 }, { 5, 0, -3 }, { 5, -3, -3 } },// kir6
					{ { 5, 5, -3 }, { 5, 0, -3 }, { -3, -3, -3 } } };// kir7
			// 边缘检测
			if (flag) {
				edge = new int[8][iw][ih];
				for (; count < 7;) {
					if (tn < 3) {
						tn++;
						new Thread(new Runnable() {
							int cc = count;

							@Override
							public void run() {

								edge[cc] = edge(gray, kir[cc], iw, ih);

								tn--;
								count++;
							}
						}).start();

					} else {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
				edge[7] = edge(gray, kir[7], iw, ih);

				for (int k = 1; k < 8; k++) {
					for (j = 0; j < ih; j++) {
						for (i = 0; i < iw; i++) {
							if (edge[0][i][j] < edge[k][i][j]) {
								edge[0][i][j] = edge[k][i][j];
							}
							if (stopNow) {
								stopNow = false;
								return null;
							}
						}
					}
				}
				for (j = 0; j < ih; j++) {
					for (i = 0; i < iw; i++) {
						if (edge[0][i][j] > thresh) {
							r = 0;
						} else {
							r = 255;
						}
						px[i + j * iw] = (255 << 24) | (r << 16) | (r << 8) | r;
						if (stopNow) {
							stopNow = false;
							return null;
						}
					}
				}
			}
		}
		return px;
	}

	public int[][] edge(int[][] in, byte[][] tmp, int iw, int ih) {
		int[][] ed = new int[iw][ih];
		for (int j = 1; j < ih - 1; j++) {
			for (int i = 1; i < iw - 1; i++) {
				ed[i][j] = Math.abs(tmp[0][0] * in[i - 1][j - 1] + tmp[0][1]
						* in[i - 1][j] + tmp[0][2] * in[i - 1][j + 1]
						+ tmp[1][0] * in[i][j - 1] + tmp[1][1] * in[i][j]
						+ tmp[1][2] * in[i][j + 1] + tmp[2][0]
						* in[i + 1][j - 1] + tmp[2][1] * in[i + 1][j]
						+ tmp[2][2] * in[i + 1][j + 1]);
				if (stopNow) {
					stopNow = false;
					return null;
				}
			}
		}
		return ed;
	}

	@Override
	protected void onResume() {
		threadNum = app.sp.getInt("threadnum", 4);
		saveDelay = app.sp.getInt("savedelay", 100);
		app.sp.getInt("picscale", 50);
		showDelay = app.sp.getInt("showdelay", 50);
		super.onResume();
	}

	@Override
	public void showNext() {
		Intent intent = new Intent(this, SettingActivity.class);
		intent.putExtra("father", 0);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, Splash.class);
		intent.putExtra("father", 0);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	private int mColor = Color.BLUE;
	private XY xy;
	private int sw;

	private int sh;

	private Mapp app;

	private View dad_color_helper;

	private RelativeLayout dad_pic_paints;

	private ImageView paint_show;

	private int[] tempArrays;

	private TextView text_show_value;

}
