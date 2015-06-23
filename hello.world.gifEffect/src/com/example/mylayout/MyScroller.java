package com.example.mylayout;

import android.os.SystemClock;

public class MyScroller {

	private int startX;
	private int startY;
	private int dX;
	private int dY;
	private int duation;

	private long startTime;

	
	private boolean isFinished;
	private float currX;
	private float currY;

	public MyScroller() {
		// TODO Auto-generated constructor stub
	}
/**
 * 
 * �����Ƿ�������������˴�Ϊʱ����ƣ�ֻҪʱ�����꣬�ͻ��������
 * @return
 */
	public boolean computeScrollOffset() {
//
		if (isFinished) {
			return false;
		}

	float passTime = SystemClock.uptimeMillis() - startTime;
		if (passTime < duation) {

			currX =  (startX + dX * passTime / duation);
			currY =  (startY + dY * passTime / duation);

		} else {
			currX = startX + dX;
			currY = startY + dY;
			
			isFinished = true;
		}
		return true;


	}
/**
 * ����������ʼ��
 * @param startX 
 * @param dX
 * @param startY
 * @param dY
 * @param duation
 */
	public void startScroll(int startX, int dX, int startY, int dY, int duation) {
		// TODO Auto-generated method stub

		this.startX = startX;
		this.dX = dX;
		this.startY = startY;
		this.dY = dY;
		this.duation = duation;
		startTime = SystemClock.uptimeMillis();
		isFinished = false;

	}

	public float getCurrX() {
		return currX;
	}


	public float getCurrY() {
		return currY;
	}

}
