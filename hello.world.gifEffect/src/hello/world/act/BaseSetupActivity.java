package hello.world.act;

import android.app.Activity;
import android.os.Bundle;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector gesture;
	private MGesture mgesture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mgesture = new MGesture();
		gesture = new GestureDetector(this, mgesture);

	}

	public abstract void showNext() ;

	public abstract void  showPre();
	
	public void next(View v) {
		showNext();
	}
	
	public void pre(View v) {
		showPre();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		gesture.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public class MGesture extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			
			if (e1.getRawX() - e2.getRawX() > 100) {
				showNext();
				return true;
			}else if (e1.getRawX() - e2.getRawX() < -100) {
				showPre();
				return true;
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}

	}

}
