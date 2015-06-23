package com.example.backdraw2;

import hello.world.builder.gifeffect.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.example.adomin.Mapp;
import com.example.drawpic.DrawPic;
import com.example.mylayout.CircleSeekBar;
import com.example.mylayout.CircleSeekBar.OnprogressChangeListenner;
import com.example.picgif.PicGif;
import com.example.utils.MPoint;
import com.example.utils.RotePoint;

public class SettingActivity extends BaseSetupActivity {

	private int w;
	private int h;
	private int sw;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		father = getIntent().getIntExtra("father", 0);
		Mapp app = (Mapp) getApplication();
		this.sp = app.sp;
		if (framesList == null) {

			sw = getWindowManager().getDefaultDisplay().getWidth();
			Bitmap setpb = BitmapFactory.decodeResource(getResources(),
					R.drawable.setpb);
			settingbg = BitmapFactory.decodeResource(getResources(),
					R.drawable.settingbg);
			settingbg = Bitmap.createScaledBitmap(settingbg, sw / 2, sw / 2,
					true);
			setpb = Bitmap.createScaledBitmap(setpb, sw / 2, sw / 2, true);
			w = setpb.getWidth();
			h = setpb.getHeight();
			List<Integer> listxyc = new ArrayList<Integer>();

			int[] pix = new int[w * h];
			int size = pix.length;
			setpb.getPixels(pix, 0, w, 0, 0, w, h);
			for (int i = 0; i < size; i++) {
				int x = i % w;
				int y = i / w;
				if (pix[i] != 0) {
					listxyc.add(x);
					listxyc.add(-y);
					listxyc.add(pix[i]);
				}

			}
			pix = null;
			framesList = new ArrayList<int[]>();
			for (int i = -9; i < 63; i++) {
				framesList.add(getFream(listxyc, i * 5));
			}
		}
		threadNum = sp.getInt("threadnum", 4);
		saveDelay = sp.getInt("savedelay", 100);
		picScale = sp.getInt("picscale", 50);
		showDelay = sp.getInt("showdelay", 50);

		CircleSeekBar cb1 = (CircleSeekBar) findViewById(R.id.cbar1);

		cb1.init(framesList, w, h, settingbg, threadNum, 8, 1);

		CircleSeekBar cb2 = (CircleSeekBar) findViewById(R.id.cbar2);
		cb2.init(framesList, w, h, settingbg, showDelay, 200, 1);

		CircleSeekBar cb3 = (CircleSeekBar) findViewById(R.id.cbar3);
		cb3.init(framesList, w, h, settingbg, saveDelay, 200, 1);

		CircleSeekBar cb4 = (CircleSeekBar) findViewById(R.id.cbar4);
		cb4.init(framesList, w, h, settingbg, picScale, 100, 1);

		cb1.setOnprogressChangeListenner(new OnprogressChangeListenner() {
			@Override
			public void getValue(int value) {
				threadNum = value;
			}
		});
		cb2.setOnprogressChangeListenner(new OnprogressChangeListenner() {
			@Override
			public void getValue(int value) {
				showDelay = value;
			}
		});
		cb3.setOnprogressChangeListenner(new OnprogressChangeListenner() {
			@Override
			public void getValue(int value) {
				saveDelay = value;
			}
		});
		cb4.setOnprogressChangeListenner(new OnprogressChangeListenner() {

			@Override
			public void getValue(int value) {
				picScale = value;
			}
		});

	}

	private int showDelay;
	private int saveDelay;
	private int picScale;
	private int threadNum;
	private List<int[]> framesList;
	private Bitmap settingbg;
	private int father;

	private int[] getFream(List<Integer> list, int degress) {
		int[] points = new int[list.size()];

		for (int i = 0; i < list.size() / 3; i++) {
			Canvas c;

			MPoint p = RotePoint.rotatePoint(
					new MPoint(list.get(3 * i), list.get(3 * i + 1)),
					new MPoint(w / 2, -h / 2), Math.toRadians(degress), true);
			points[i * 3] = (int) p.x;
			points[3 * i + 1] = (int) -p.y;
			points[3 * i + 2] = list.get(3 * i + 2);

		}
		return points;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mIntent();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			Editor edit = sp.edit();
			edit.putInt("threadnum", threadNum);
			edit.putInt("savedelay", saveDelay);
			edit.putInt("picscale", picScale);
			edit.putInt("showdelay", showDelay);
			edit.commit();

			finish();
			return true;

		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			mIntent();

			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			Editor edit = sp.edit();
			edit.putInt("threadnum", threadNum);
			edit.putInt("savedelay", saveDelay);
			edit.putInt("picscale", picScale);
			edit.putInt("showdelay", showDelay);
			edit.commit();
			// finish();
			return true;
		}
		return false;

	}

	private void mIntent() {
		if (father == 0) {
			startActivity(new Intent(this, DrawPic.class));
		} else {
			startActivity(new Intent(this, PicGif.class));

		}
	}

	@Override
	public void showPre() {
		mIntent();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		Editor edit = sp.edit();
		edit.putInt("threadnum", threadNum);
		edit.putInt("savedelay", saveDelay);
		edit.putInt("picscale", picScale);
		edit.putInt("showdelay", showDelay);
		edit.commit();

		// finish();

	}

	@Override
	public void showNext() {
		// TODO Auto-generated method stub

	}
}
