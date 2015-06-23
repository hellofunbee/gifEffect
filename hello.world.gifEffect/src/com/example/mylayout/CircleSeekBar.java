package com.example.mylayout;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.paints.Paints;
import com.example.utils.MPoint;
import com.example.utils.RotePoint;

public class CircleSeekBar extends View {
	private int old;

	public void init(List<int[]> list, int w, int h, Bitmap settingbg,
			int value, float max, float min) {
		framesList = list;
		this.max = max;
		this.min = min;
		this.w = w;
		this.h = h;
		this.settingbg = settingbg;
		bm = Bitmap.createBitmap(w, h, Config.ARGB_4444);
		if (value >= 1) {
			value = (int) (value * 72 / max);
			if (value > 72)
				value = 72;
		}
		if (value == 0) {
			value = 1;
		}

		this.value = value;

		makeBm(value);
	}

	public CircleSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = Paints.getPaint();

	}

	private void makeBm(int k) {
		if (l != null) {
			int progress = (int) (max * value / 72.0);
			if (progress == 0) {
				progress = (int) min;
			}
			l.getValue(progress);
		}
		int[] p;

		if (k == old) {

			return;
		}
		if (k > old) {

			for (int i = old; i < k; i++) {
				p = framesList.get(i);
				for (int j = 0; j < p.length / 3; j++) {
					bm.setPixel(p[3 * j], p[3 * j + 1], p[3 * j + 2]);
				}
			}
		} else {

			bm = Bitmap.createBitmap(w, h, Config.ARGB_4444);

			for (int i = 0; i < k; i++) {
				p = framesList.get(i);
				for (int j = 0; j < p.length / 3; j++) {
					bm.setPixel(p[3 * j], p[3 * j + 1], p[3 * j + 2]);
				}
			}
		}
		invalidate();
		old = k;
	}

	private Paint paint;
	private Bitmap settingbg;

	@Override
	protected void onDraw(Canvas canvas) {
		if (settingbg != null && bm != null) {
			canvas.drawBitmap(settingbg, 0, 0, paint);
			canvas.drawBitmap(bm, 0, 0, paint);
		}
	}

	int sx, sy, nx, ny;
	private int value;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			sy = (int) event.getY();
			sx = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			nx = (int) event.getX();
			ny = (int) event.getY();
			int rad = RotePoint.rad(new MPoint(sx, sy), new MPoint(nx, ny),
					new MPoint(w / 2, h / 2));
			if (Math.abs(rad) > 0) {
				value += Math.abs(rad) / rad;
				if (value > 72) {
					value = 72;
				}
				if (value < 1) {
					value = 1;
				}
				makeBm(value);
			}
			sy = ny;
			sx = nx;
			break;
		case MotionEvent.ACTION_UP:
		}
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width = 0, height = 0;
		int desiredWidth = getPaddingLeft() + getPaddingRight()
				+ settingbg.getWidth();
		switch (widthMode) {
		case MeasureSpec.AT_MOST:
			width = Math.min(widthSize, desiredWidth);
			break;
		case MeasureSpec.EXACTLY:
			width = widthSize;
			break;
		case MeasureSpec.UNSPECIFIED:
			width = desiredWidth;
			break;
		}
		int contentHeight = settingbg.getHeight();
		int desiredHeight = getPaddingTop() + getPaddingBottom()
				+ contentHeight;
		switch (heightMode) {
		case MeasureSpec.AT_MOST:
			height = Math.min(heightSize, desiredHeight);
			break;
		case MeasureSpec.EXACTLY:
			height = heightSize;
			break;
		case MeasureSpec.UNSPECIFIED:
			height = contentHeight;
			break;
		}
		setMeasuredDimension(width, height);
	}

	private int h;
	private int w;
	private Bitmap bm;
	private List<int[]> framesList;
	private OnprogressChangeListenner l;
	private float max = 72;
	private float min = 1;

	public void setOnprogressChangeListenner(OnprogressChangeListenner l) {
		this.l = l;
	}

	public interface OnprogressChangeListenner {
		void getValue(int value);
	}

}
