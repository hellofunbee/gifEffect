package hello.world.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MAnimtoin {

	/**
	 * 控制盘的旋转
	 * 
	 * @param mm
	 *            要旋转的布局
	 * @param i
	 *            i>0,顺时针旋转，否则逆时针
	 */
	public static void run(View mm, int i,int s,int e) {
		// TODO Auto-generated method stub
		RotateAnimation rot;

		
			if (i > 0) {
				rot = new RotateAnimation(s, e, Animation.RELATIVE_TO_SELF,
						0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			} else {
				rot = new RotateAnimation(s, e, Animation.RELATIVE_TO_SELF,
						0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			}
		

		rot.setDuration(100);
		rot.setFillAfter(true);
		
		mm.startAnimation(rot);
		}
	
	public static void outScreen(int sx, int ex, int sy, int ey, int w, View v) {
		TranslateAnimation trans = new TranslateAnimation(2, sx, 2, ex, 2, sy,
				2, ey);
		trans.setFillAfter(true);
		trans.setDuration(5000);
		trans.setDuration(500);

		ScaleAnimation scale = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f);
		scale.setFillAfter(true);
		trans.setDuration(5000);
		AnimationSet set = new AnimationSet(true);
		set.addAnimation(trans);
		set.addAnimation(scale);
		set.setDuration(5000);
		v.startAnimation(scale);
	}

}
