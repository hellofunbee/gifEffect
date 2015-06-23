package com.example.utils;

import android.content.Context;
import android.os.Vibrator;

public class Vib {
	public static void vib(Context context,int time) {
		Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(time);
	}
}
