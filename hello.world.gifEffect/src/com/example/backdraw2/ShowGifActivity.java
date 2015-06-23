package com.example.backdraw2;

import hello.world.builder.gifeffect.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.adomin.Mapp;
import com.example.mylayout.GifImageView;

public class ShowGifActivity extends Activity {
	private List<String> imagePathList;
	private GridView gv_gif_show;
	private ImageView share_halper;
	private GestureDetector detector;
	private String imageUrl;
	private Options opts;
	private GifImageView giv;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_showgif);
		
		gv_gif_show = (GridView) findViewById(R.id.gv_show_gif);

		Mapp app = (Mapp) getApplication();
		imageUrl = app.getAddr();
		share_halper = (ImageView) findViewById(R.id.iv_share_helper);

		giv = (GifImageView) findViewById(R.id.miv_show_gif);
		giv.setAddr(imageUrl);
		imagePathList = getImagePathFromSD();

		MAdapter adapter = new MAdapter();
		gv_gif_show.setAdapter(adapter);
		gv_gif_show.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (giv.getVisibility() == View.VISIBLE) {

				} else {
					giv.setVisibility(View.VISIBLE);
				}
				imageUrl = imagePathList.get(position);
				giv.setAddr(imageUrl);
			}
		});

		creatDetector();

		share_halper.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				detector.onTouchEvent(event);
				return true;
			}
		});
		opts = new Options();
		opts.inSampleSize = 4;
	}

	private void creatDetector() {
		detector = new GestureDetector(getApplicationContext(),
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						// TODO Auto-generated method stub
						shareMsg("请选择", "haha", "niha", imageUrl);
						return true;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						int dx = (int) (e2.getX() - e1.getX());
						int dy = (int) (e2.getY() - e1.getY());
						share_halper.setLeft((int) (share_halper.getLeft() + dx));
						share_halper.setTop((int) (share_halper.getTop() + dy));
						share_halper.setRight((int) (share_halper.getRight() + dx));
						share_halper.setBottom((int) (share_halper.getBottom() + dy));

						return true;
					}
				});

	}

	/**
	 * 分享功能
	 * 
	 * @param context
	 *            上下文
	 * @param activityTitle
	 *            Activity的名字
	 * @param msgTitle
	 *            消息标题
	 * @param msgText
	 *            消息内容
	 * @param imgPath
	 *            图片路径，不分享图片则传null
	 */
	public void shareMsg(String activityTitle, String msgTitle, String msgText,
			String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/*");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, activityTitle));
	}

	private List<String> getImagePathFromSD() {
		/* 设定目前所在路径 */
		List<String> it = new ArrayList<String>();

		// 根据自己的需求读取SDCard中的资源图片的路径
		String imagePath = Environment.getExternalStorageDirectory().toString()
				+ "/haha";

		File mFile = new File(imagePath);
		File[] files = mFile.listFiles();

		/* 将所有文件存入ArrayList中 */
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (checkIsImageFile(file.getPath()))
				it.add(file.getPath());
		}
		return it;
	}

	private boolean checkIsImageFile(String fName) {
		boolean isImageFormat;

		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			isImageFormat = true;
		} else {
			isImageFormat = false;
		}
		return isImageFormat;
	}

	class MAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imagePathList.size();
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
						R.layout.gif_show_item, null);
			}
			ImageView iv = (ImageView) view.findViewById(R.id.iv_show_gif);
			iv.setImageBitmap(BitmapFactory.decodeFile(
					imagePathList.get(position), opts));
			return view;
		}

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (giv.getVisibility() == View.VISIBLE) {
				giv.setVisibility(View.GONE);
			} else {
				finish();
			}
			return true;
		}
		return false;
	}


}