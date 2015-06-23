package com.example.adomin;

import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ant.liao.GifDecoder;
import com.example.drawpic.DrawPic;
import com.example.picgif.PicGif;

public class Mapp extends Application {
	private String addr;
	public List<GifDecoder> gifList;
	public float scale;
	public boolean run = false;
	public DrawPic drawPic;
	public PicGif picGif;
	public int h, w;
	public List<Bitmap> freams;
	public boolean stopNow;
	public Map<Integer, Bitmap> map;
	public int key;
	public SharedPreferences sp;

	@Override
	public void onCreate() {
		sp = getSharedPreferences("location", Mapp.MODE_PRIVATE);
		super.onCreate();
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public Bitmap getBitmap(int bitmapStyle) {
		if (map.get(bitmapStyle) == null) {
			return BitmapFactory.decodeResource(getResources(), bitmapStyle);
		} else {
			return map.get(bitmapStyle);
		}
	}
}
